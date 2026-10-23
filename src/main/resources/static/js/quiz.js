function moveQuizToHost() {
  const host = $('quizHost');
  if (host && quizCard.parentElement !== host) host.appendChild(quizCard);
  document.querySelectorAll('.submodule-item.has-active-quiz').forEach(item => item.classList.remove('has-active-quiz'));
}

async function startQuiz(submodule) {
  if (currentLives <= 0) { const refreshed = await loadLives(); if (refreshed !== null && refreshed <= 0) { learningPathStatus.textContent = '💔 You are out of lives. One life regenerates every 30 minutes.'; return; } }
  moveQuizToHost();
  const submoduleItem = $(`submodule-item-${submodule.id}`);
  if (submoduleItem) {
    submoduleItem.classList.add('has-active-quiz');
    submoduleItem.appendChild(quizCard);
  }
  activeSubmodule = submodule; selectedAnswers = []; questions = []; currentQuestionIndex = 0; answering = false;
  $('quizTitle').textContent = submodule.title || 'Submodule practice'; $('quizSubtitle').textContent = 'Answer the questions one at a time.';
  quizCard.hidden = false; questionArea.hidden = true; $('answerFeedback').textContent = ''; quizStatus.textContent = 'Loading questions...'; $('nextQuestion').hidden = true;
  quizCard.scrollIntoView({behavior:'smooth', block:'start'});
  try {
    const data = await api(`/questions/submodule/${encodeURIComponent(submodule.id)}?userId=${encodeURIComponent(currentUser.userId)}`);
    if (!Array.isArray(data) || !data.length) { quizStatus.textContent = 'No questions found for this submodule.'; return; }
    questions = data; questionArea.hidden = false; renderQuestion();
  } catch (error) {
    quizStatus.textContent = error.message.includes('403') ? '🔒 This submodule is locked. Pass the previous submodule first.' : error.message;
    if (/403|locked/i.test(error.message)) quizStatus.textContent = '🔒 This submodule is locked. Pass the previous submodule first.';
    console.error('Unable to start quiz:', error);
  }
}
function renderQuestion() {
  const question = questions[currentQuestionIndex]; if (!question) return;
  answering = false; $('nextQuestion').hidden = true; $('answerFeedback').textContent = ''; $('optionsArea').replaceChildren();
  $('questionNumber').textContent = `Question ${currentQuestionIndex + 1} of ${questions.length}`;
  $('questionText').textContent = question.questionText ?? question.text ?? question.question ?? '';
  $('quizStatus').textContent = 'Choose the best answer.';
  ['A','B','C','D'].forEach(key => {
    const value = question[`option${key}`] ?? question[key.toLowerCase()]; if (value == null || value === '') return;
    const button = document.createElement('button'); button.type = 'button'; button.className = 'quiz-option'; button.textContent = `${key}. ${value}`;
    button.addEventListener('click', () => submitAnswer(key, button)); $('optionsArea').appendChild(button);
  });
}
async function submitAnswer(selectedAnswer, selectedButton) {
  if (answering) return; answering = true;
  const question = questions[currentQuestionIndex];
  selectedAnswers = selectedAnswers.filter(answer => Number(answer.questionId) !== Number(question.id));
  selectedAnswers.push({questionId:question.id, selectedAnswer});
  $('optionsArea').querySelectorAll('button').forEach(button => button.disabled = true);
  try {
    const result = await api('/quiz-attempts/answer', {method:'POST',body:JSON.stringify({userId:Number(currentUser.userId),questionId:question.id,selectedAnswer})});
    selectedButton.classList.add('selected');
    if (result.correct) { selectedButton.classList.add('correct'); $('answerFeedback').textContent = 'Correct! Great job. 🎉'; }
    else { selectedButton.classList.add('incorrect'); $('answerFeedback').textContent = `Not quite. Correct answer: ${result.correctAnswer ?? 'review this question and try again next time.'}`; }
    $('quizStatus').textContent = result.correct ? 'Correct answer!' : 'Keep going—you are learning.';
    await loadLives(); $('nextQuestion').hidden = false; $('nextQuestion').textContent = currentQuestionIndex === questions.length - 1 ? 'Finish lesson' : 'Continue';
  } catch (error) {
    $('quizStatus').textContent = error.message;
    $('optionsArea').querySelectorAll('button').forEach(button => button.disabled = false); answering = false;
    if (/409|life|lives/i.test(error.message)) await loadLives();
  }
}
$('nextQuestion').addEventListener('click', async () => {
  if (currentQuestionIndex < questions.length - 1) { currentQuestionIndex++; renderQuestion(); return; }
  const next = $('nextQuestion'); next.disabled = true; quizStatus.textContent = 'Saving your result...';
  try {
    const completedSubmoduleId = Number(activeSubmodule.id);
    const result = await api('/quiz-attempts', {method:'POST',body:JSON.stringify({userId:Number(currentUser.userId),subModuleId:completedSubmoduleId,answers:selectedAnswers})});
    questionArea.hidden = true;
    quizCard.hidden = true;
    moveQuizToHost();
    if (result.passed && typeof passedSubmoduleIds !== 'undefined') passedSubmoduleIds.add(completedSubmoduleId);
    quizStatus.textContent = `${result.message || (result.passed ? 'Submodule completed!' : 'Quiz completed.')} Score: ${result.score}/${result.totalQuestions}. ${Number(result.percentage ?? 0).toFixed(1)}%. XP earned: ${result.xpEarned ?? 0}.`;
    await Promise.allSettled([loadLives(),loadStreak(),loadTotalXp(),loadLeaderboard()]);
    await loadLessons();
  } catch (error) { quizStatus.textContent = error.message; console.error('Quiz submission failed:', error); }
  finally { next.disabled = false; next.hidden = true; }
});
$('closeQuiz').addEventListener('click', () => { quizCard.hidden = true; questions = []; activeSubmodule = null; moveQuizToHost(); });

