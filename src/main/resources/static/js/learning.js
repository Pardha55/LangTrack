let selectedLessonId = null;
let selectedLessonTitle = '';
let passedSubmoduleIds = new Set();
let submoduleStatusLoaded = false;

async function loadLessons() {
  lessonsContainer.innerHTML = '<p class="muted">Loading subjects...</p>';
  learningPathStatus.textContent = '';
  try {
    const lessons = await api('/lessons');
    lessonsContainer.replaceChildren();
    if (!Array.isArray(lessons) || !lessons.length) {
      lessonsContainer.innerHTML = '<div class="card">No subjects available yet.</div>';
      return;
    }

    // A lesson is the subject selector. Only one subject's learning path is shown at a time.
    const selectedLesson = lessons.find(item => Number(item.id) === Number(selectedLessonId));
    if (selectedLesson) {
      await renderSelectedSubject(selectedLesson);
      return;
    }

    selectedLessonId = null;
    const intro = document.createElement('div');
    intro.className = 'subject-picker-intro';
    intro.innerHTML = '<h3>Choose a subject</h3><p class="muted">Select one subject to see its modules and learning path.</p>';
    const grid = document.createElement('div');
    grid.className = 'subject-grid';
    lessons.forEach(lesson => {
      const button = document.createElement('button');
      button.type = 'button';
      button.className = 'subject-card';
      const icon = document.createElement('span'); icon.className = 'subject-icon'; icon.textContent = '📚';
      const name = document.createElement('strong'); name.textContent = lesson.title || `Subject ${lesson.id}`;
      const action = document.createElement('span'); action.className = 'subject-action'; action.textContent = 'View learning path →';
      button.append(icon, name, action);
      button.addEventListener('click', async () => {
        selectedLessonId = lesson.id;
        selectedLessonTitle = lesson.title || `Subject ${lesson.id}`;
        await loadLessons();
      });
      grid.appendChild(button);
    });
    lessonsContainer.append(intro, grid);
  } catch (error) {
    lessonsContainer.innerHTML = '<div class="card">Could not load subjects. Please refresh and try again.</div>';
    learningPathStatus.textContent = error.message;
    console.error(error);
  }
}

async function renderSelectedSubject(lesson) {
  selectedLessonTitle = lesson.title || `Subject ${lesson.id}`;
  const header = document.createElement('div');
  header.className = 'selected-subject-header';
  const copy = document.createElement('div');
  const eyebrow = document.createElement('p'); eyebrow.className = 'subject-eyebrow'; eyebrow.textContent = 'SELECTED SUBJECT';
  const title = document.createElement('h3'); title.textContent = selectedLessonTitle;
  const description = document.createElement('p'); description.className = 'muted'; description.textContent = 'Pass each submodule to unlock the next circle.';
  copy.append(eyebrow, title, description);
  const back = document.createElement('button'); back.type = 'button'; back.className = 'secondary'; back.textContent = '← All subjects';
  back.addEventListener('click', () => { moveQuizToHost(); selectedLessonId = null; quizCard.hidden = true; loadLessons(); });
  header.append(copy, back);
  lessonsContainer.appendChild(header);

  const path = document.createElement('div'); path.className = 'selected-subject-path';
  path.innerHTML = '<p class="muted">Loading modules and progress...</p>';
  lessonsContainer.appendChild(path);

  try {
    const [modules, submodules] = await Promise.all([
      api(`/modules/lesson/${encodeURIComponent(lesson.id)}`),
      api(`/submodules/lesson/${encodeURIComponent(lesson.id)}`)
    ]);
    path.replaceChildren();
    if (!Array.isArray(modules) || !modules.length) {
      path.innerHTML = '<div class="card">No modules found for this subject yet.</div>';
      return;
    }
    await refreshSubmoduleStatuses(lesson.id);

    const orderedSubmodules = [];
    modules.forEach(module => {
      const children = (Array.isArray(submodules) ? submodules : [])
        .filter(sub => Number(sub.module?.id ?? sub.moduleId) === Number(module.id))
        .sort((a, b) => Number(a.orderNumber ?? a.subModuleNumber ?? a.id) - Number(b.orderNumber ?? b.subModuleNumber ?? b.id));
      children.forEach(sub => orderedSubmodules.push(sub));
    });

    let previousPassed = true;
    modules.forEach((module, moduleIndex) => {
      const moduleCard = document.createElement('section'); moduleCard.className = 'module-path-card';
      const heading = document.createElement('div'); heading.className = 'module-path-heading';
      const number = document.createElement('span'); number.className = 'module-number'; number.textContent = String(module.moduleNumber ?? moduleIndex + 1).padStart(2, '0');
      const titleWrap = document.createElement('div');
      const headingTitle = document.createElement('h4'); headingTitle.textContent = module.title || `Module ${module.moduleNumber ?? moduleIndex + 1}`;
      const subCount = document.createElement('p'); subCount.className = 'muted';
      const children = (Array.isArray(submodules) ? submodules : [])
        .filter(sub => Number(sub.module?.id ?? sub.moduleId) === Number(module.id))
        .sort((a, b) => Number(a.orderNumber ?? a.subModuleNumber ?? a.id) - Number(b.orderNumber ?? b.subModuleNumber ?? b.id));
      subCount.textContent = `${children.length} submodule${children.length === 1 ? '' : 's'}`;
      titleWrap.append(headingTitle, subCount); heading.append(number, titleWrap); moduleCard.appendChild(heading);
      const circles = document.createElement('div'); circles.className = 'submodule-circle-grid';
      children.forEach((sub, idx) => {
        const passed = passedSubmoduleIds.has(Number(sub.id));
        const unlocked = passed || previousPassed;
        const item = document.createElement('div');
        item.className = 'submodule-item';
        item.id = `submodule-item-${sub.id}`;
        const node = document.createElement('button');
        node.type = 'button';
        node.className = `submodule-node${passed ? ' is-passed' : unlocked ? ' is-unlocked' : ' is-locked'}`;
        node.disabled = !unlocked;
        node.setAttribute('aria-label', `${sub.title || `Submodule ${idx + 1}`}: ${passed ? 'passed' : unlocked ? 'available' : 'locked'}`);
        const circle = document.createElement('span'); circle.className = 'submodule-circle';
        circle.textContent = passed ? '✓' : unlocked ? String(idx + 1) : '🔒';
        const name = document.createElement('span'); name.className = 'submodule-node-title'; name.textContent = sub.title || `Submodule ${idx + 1}`;
        const state = document.createElement('small'); state.className = 'submodule-node-state'; state.textContent = passed ? 'Passed' : unlocked ? 'Start quiz' : 'Locked';
        node.append(circle, name, state);
        if (unlocked) node.addEventListener('click', () => startQuiz(sub));
        item.appendChild(node);
        circles.appendChild(item);
        // Progression is sequential across all submodules in this subject.
        previousPassed = passed;
      });
      if (!children.length) { const empty = document.createElement('p'); empty.className = 'muted'; empty.textContent = 'No submodules in this module yet.'; moduleCard.appendChild(empty); }
      else moduleCard.appendChild(circles);
      path.appendChild(moduleCard);
    });
  } catch (error) {
    path.innerHTML = `<p class="status">Could not load modules: ${escapeHtml(error.message)}</p>`;
    console.error(error);
  }
}

async function refreshSubmoduleStatuses(lessonId) {
  submoduleStatusLoaded = false;
  if (!currentUser?.userId) return;
  try {
    const result = await api(`/quiz-attempts/submodule-status?userId=${encodeURIComponent(currentUser.userId)}&lessonId=${encodeURIComponent(lessonId)}`);
    const ids = extractPassedIds(result);
    if (ids !== null) {
      passedSubmoduleIds = new Set([...passedSubmoduleIds, ...ids]);
      submoduleStatusLoaded = true;
    }
  } catch (error) {
    // Keep in-session results if this optional status endpoint is unavailable.
    console.info('Could not refresh saved submodule status; using current-session progress.', error.message);
  }
}

function extractPassedIds(data) {
  if (Array.isArray(data)) {
    const ids = [];
    data.forEach(item => {
      if (typeof item === 'number' || typeof item === 'string') { if (Number.isFinite(Number(item))) ids.push(Number(item)); return; }
      if (!item || typeof item !== 'object') return;
      const id = item.subModuleId ?? item.submoduleId ?? item.subModule?.id ?? item.submodule?.id ?? item.id;
      const passed = item.passed ?? item.completed ?? item.isPassed ?? item.isCompleted ?? item.status === 'PASSED' ?? false;
      if (passed && id != null) ids.push(Number(id));
    });
    return ids;
  }
  if (data && typeof data === 'object') {
    const direct = data.passedSubmoduleIds ?? data.passedSubModuleIds ?? data.completedSubmoduleIds ?? data.completedSubModuleIds;
    if (Array.isArray(direct)) return direct.map(Number).filter(Number.isFinite);
    for (const key of ['statuses', 'submodules', 'subModules', 'results', 'data']) {
      if (Array.isArray(data[key])) return extractPassedIds(data[key]);
    }
    const ids = Object.entries(data).filter(([, value]) => value === true || value === 'PASSED' || value?.passed === true).map(([key]) => Number(key)).filter(Number.isFinite);
    if (ids.length) return ids;
  }
  return null;
}

function escapeHtml(value) {
  return String(value).replace(/[&<>"']/g, char => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[char]));
}
