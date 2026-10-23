
package com.pardha.langtrack.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.pardha.langtrack.dto.AnswerFeedback;
import com.pardha.langtrack.dto.QuizAnswer;
import com.pardha.langtrack.dto.QuizResult;
import com.pardha.langtrack.dto.QuizSubmission;
import com.pardha.langtrack.entity.Question;
import com.pardha.langtrack.entity.QuizAttempt;
import com.pardha.langtrack.entity.User;
import com.pardha.langtrack.repository.LessonRepository;
import com.pardha.langtrack.repository.ModuleRepository;
import com.pardha.langtrack.repository.ProgressRepository;
import com.pardha.langtrack.repository.QuestionRepository;
import com.pardha.langtrack.repository.QuizAttemptRepository;
import com.pardha.langtrack.repository.SubModuleRepository;
import com.pardha.langtrack.repository.UserRepository;

@Service
public class QuizAttemptService {

    private static final int MAX_XP_PER_FIRST_PASS = 35;

    private final QuizAttemptRepository repository;
    private final QuestionRepository questionRepository;
    private final LessonRepository lessonRepository;
    private final ProgressRepository progressRepository;
    private final SubModuleRepository subModuleRepository;
    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;

    public QuizAttemptService(
            QuizAttemptRepository repository,
            QuestionRepository questionRepository,
            LessonRepository lessonRepository,
            ProgressRepository progressRepository,
            SubModuleRepository subModuleRepository,
            ModuleRepository moduleRepository,
            UserRepository userRepository) {

        this.repository = repository;
        this.questionRepository = questionRepository;
        this.lessonRepository = lessonRepository;
        this.progressRepository = progressRepository;
        this.subModuleRepository = subModuleRepository;
        this.userRepository = userRepository;
        this.moduleRepository = moduleRepository;
    }

    public List<Question> getQuestionsForQuiz(Long subModuleId) {
        return questionRepository.findBySubModuleId(subModuleId);
    }

    @Transactional
    public AnswerFeedback checkSingleAnswer(QuizAnswer answer) {

        if (answer == null
                || answer.getUserId() == null
                || answer.getQuestionId() == null
                || answer.getSelectedAnswer() == null) {
            throw new IllegalArgumentException(
                    "User ID, question ID, and selected answer are required");
        }

        String selected = answer.getSelectedAnswer()
                .trim()
                .toUpperCase();

        if (!selected.matches("[ABCD]")) {
            throw new IllegalArgumentException(
                    "Selected answer must be A, B, C, or D");
        }

        User user = userRepository.findByIdForUpdate(answer.getUserId())
        .orElseThrow(() -> new IllegalArgumentException(
                "User not found"));
                
        int currentLives = getLives(answer.getUserId());

        if (currentLives <= 0) {
            throw new IllegalStateException(
                    "No lives remaining. Restore your lives before continuing.");
        }

        Question question = questionRepository
                .findById(answer.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Question not found"));

        boolean correct = isCorrectAnswer(question, selected);

        // Deduct exactly one life for an incorrect answer.
        if (!correct) {
            int livesBefore = user.getLives();

            user.setLives(livesBefore - 1);

            if (livesBefore == 5) {
                user.setLastLifeLostAt(LocalDateTime.now());
            }

            userRepository.save(user);
        }

        return new AnswerFeedback(
                question.getId(),
                selected,
                question.getCorrectAnswer(),
                correct);
    }

    private boolean isCorrectAnswer(
            Question question,
            String selectedAnswer) {

        return question.getCorrectAnswer()
                .equalsIgnoreCase(selectedAnswer);
    }

    private boolean hasPassedSubModule(
            Long userId,
            Long subModuleId) {

        return repository.existsByUserIdAndSubModuleIdAndPassedTrue(
                userId,
                subModuleId);
    }

    public boolean hasPassedSubModuleForUser(
            Long userId,
            Long subModuleId) {

        if (userId == null || subModuleId == null) {
            return false;
        }

        return hasPassedSubModule(userId, subModuleId);
    }

    public boolean canAccessSubModule(
            Long userId,
            Long subModuleId) {

        if (userId == null || subModuleId == null) {
            return false;
        }

        var currentSubModule = subModuleRepository.findById(subModuleId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Submodule not found"));

        Long lessonId = currentSubModule.getModule()
                .getLesson()
                .getId();

        var modules = moduleRepository
                .findByLesson_IdOrderByModuleNumberAsc(lessonId);

        for (var module : modules) {
            var subModules = subModuleRepository
                    .findByModule_IdOrderByOrderNumber(module.getId());

            for (var subModule : subModules) {

                if (subModule.getId().equals(subModuleId)) {
                    return true;
                }

                if (!hasPassedSubModule(userId, subModule.getId())) {
                    return false;
                }
            }
        }

        return false;
    }

    private boolean areAllSubModulesPassed(
            Long userId,
            Long lessonId) {

        var subModules = subModuleRepository
                .findByModule_Lesson_Id(lessonId);

        if (subModules.isEmpty()) {
            return false;
        }

        for (var subModule : subModules) {
            if (!hasPassedSubModule(userId, subModule.getId())) {
                return false;
            }
        }

        return true;
    }

    private void markLessonCompleted(
            Long userId,
            Long lessonId) {

        var existingProgress = progressRepository
                .findByUserIdAndLessonId(userId, lessonId);

        if (existingProgress.isPresent()) {
            var progress = existingProgress.get();
            progress.setCompleted(true);
            progressRepository.save(progress);
        } else {
            var progress = new com.pardha.langtrack.entity.Progress(
                    userId, lessonId, 0, true);

            progressRepository.save(progress);
        }
    }

    
private void addXpToLessonProgress(
        Long userId,
        Long lessonId,
        int xpEarned) {

    var existingProgress = progressRepository
            .findByUserIdAndLessonId(userId, lessonId);

    var progress = existingProgress.orElseGet(() ->
            new com.pardha.langtrack.entity.Progress(
                    userId, lessonId, 0, false));

    progress.setXpEarned(
            progress.getXpEarned() + xpEarned);

    progressRepository.save(progress);
}


private void updateStreakOnSuccessfulPass(User user) {

    LocalDate today = LocalDate.now();
    LocalDate lastStreakDate = user.getLastStreakDate();

    // Multiple successful passes on the same day count once.
    if (today.equals(lastStreakDate)) {
        return;
    }

    int newStreak;

    // Continue the streak if yesterday was a qualifying day.
    if (lastStreakDate != null
            && lastStreakDate.equals(today.minusDays(1))) {

        newStreak = user.getCurrentStreak() + 1;

    } else {
        // Start a new streak after a missed day.
        newStreak = 1;
    }

    user.setCurrentStreak(newStreak);

    if (newStreak > user.getLongestStreak()) {
        user.setLongestStreak(newStreak);
    }

    user.setLastStreakDate(today);

    userRepository.save(user);
}

    @Transactional
    public QuizResult evaluateQuiz(QuizSubmission submission) {

        if (submission == null
                || submission.getUserId() == null
                || submission.getSubModuleId() == null
                || submission.getAnswers() == null) {
            throw new IllegalArgumentException(
                    "User, sub-module, and answers are required");
        }

        Long userId = submission.getUserId();
        Long subModuleId = submission.getSubModuleId();

        // Lock this user's row before checking the first-pass reward.
        // Concurrent quiz submissions for this user must wait.
       User user = userRepository.findByIdForUpdate(userId)
        .orElseThrow(() -> new IllegalArgumentException(
                "User not found"));

        var subModule = subModuleRepository.findById(subModuleId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Submodule not found"));

        // Prevent users from submitting quizzes for locked submodules.
        if (!canAccessSubModule(userId, subModuleId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "This submodule is locked. Pass the previous submodule first.");
        }

        var questions = getQuestionsForQuiz(subModuleId);

        if (questions.isEmpty()) {
            throw new IllegalArgumentException(
                    "No questions found for this sub-module");
        }

        Map<Long, Question> questionMap = new HashMap<>();

        for (Question question : questions) {
            questionMap.put(question.getId(), question);
        }

        List<QuizAnswer> answers = submission.getAnswers();

        if (answers.size() != questions.size()) {
            throw new IllegalArgumentException(
                    "Submit an answer for every quiz question");
        }

        Map<Long, String> submittedAnswers = new HashMap<>();

        for (QuizAnswer answer : answers) {

            if (answer == null
                    || answer.getQuestionId() == null
                    || answer.getSelectedAnswer() == null) {
                throw new IllegalArgumentException(
                        "Question ID and selected answer are required");
            }

            String selected = answer.getSelectedAnswer()
                    .trim()
                    .toUpperCase();

            if (!selected.matches("[ABCD]")) {
                throw new IllegalArgumentException(
                        "Selected answer must be A, B, C, or D");
            }

            if (!questionMap.containsKey(answer.getQuestionId())) {
                throw new IllegalArgumentException(
                        "Question does not belong to this sub-module");
            }

            if (submittedAnswers.put(
                    answer.getQuestionId(), selected) != null) {
                throw new IllegalArgumentException(
                        "Duplicate question answers are not allowed");
            }
        }

        if (submittedAnswers.size() != questions.size()) {
            throw new IllegalArgumentException(
                    "Every question must be answered exactly once");
        }

        int score = 0;

        for (Question question : questions) {
            String selected = submittedAnswers.get(question.getId());

            if (isCorrectAnswer(question, selected)) {
                score++;
            }
        }

        int totalQuestions = questions.size();
        double percentage = score * 100.0 / totalQuestions;

        // Preserve the existing passing threshold.
        boolean passed = percentage >= 50.0;

        // Award XP only when this is the first successful pass.
        boolean firstSuccessfulPass = passed
                && !hasPassedSubModule(userId, subModuleId);

        // Award XP proportionally to the score on the first successful pass.
// The passing threshold remains 50%, and the maximum reward is 35 XP.
        int xpEarned = firstSuccessfulPass
                ? (int) Math.floor((percentage / 100.0) * MAX_XP_PER_FIRST_PASS)
                : 0;

        QuizAttempt attempt = new QuizAttempt(
                userId,
                subModuleId,
                score,
                totalQuestions,
                passed,
                LocalDate.now());

      
        // Persist the actual XP reward with this attempt.
        attempt.setXpEarned(xpEarned);
        repository.save(attempt);

        boolean lessonCompleted = false;

        Long lessonId = subModule.getModule()
                .getLesson()
                .getId();

        // Add only XP actually earned, never repeat-pass XP.
        if (xpEarned > 0) {
            addXpToLessonProgress(userId, lessonId, xpEarned);
        }

        if (passed) {
    // A successful submodule pass qualifies for today's streak.
    updateStreakOnSuccessfulPass(user);

    lessonCompleted = areAllSubModulesPassed(
            userId, lessonId);

    if (lessonCompleted) {
        markLessonCompleted(userId, lessonId);
    }
}

        String message;

        if (!passed) {
            message = "Quiz not passed. Try again. You earned 0 XP.";
        } else if (firstSuccessfulPass && lessonCompleted) {
            message = "Quiz passed! Lesson completed. You earned "
                    + xpEarned + " XP.";
        } else if (firstSuccessfulPass) {
            message = "Quiz passed! Complete the remaining submodules "
                    + "to finish this lesson. You earned "
                    + xpEarned + " XP.";
        } else if (lessonCompleted) {
            message = "Quiz passed! Lesson completed. "
                    + "No additional XP awarded for this repeat pass.";
        } else {
            message = "Quiz passed! Complete the remaining submodules "
                    + "to finish this lesson. "
                    + "No additional XP awarded for this repeat pass.";
        }

        return new QuizResult(
                score,
                totalQuestions,
                percentage,
                passed,
                message,
                xpEarned);
    }

    @Transactional
    public int getLives(Long userId) {

        if (userId == null || userId < 1) {
            throw new IllegalArgumentException(
                    "Valid user ID is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found"));

        int lives = user.getLives();

        if (lives >= 5) {
            return 5;
        }

        LocalDateTime lastLifeLostAt = user.getLastLifeLostAt();

        if (lastLifeLostAt == null) {
            return lives;
        }

        LocalDateTime now = LocalDateTime.now();

        long minutesPassed = java.time.Duration.between(
                lastLifeLostAt, now).toMinutes();

        int livesToRestore = (int) (minutesPassed / 30);

        if (livesToRestore <= 0) {
            return lives;
        }

        int restoredLives = Math.min(5, lives + livesToRestore);
        user.setLives(restoredLives);

        if (restoredLives == 5) {
            user.setLastLifeLostAt(null);
        } else {
            user.setLastLifeLostAt(
                    lastLifeLostAt.plusMinutes(livesToRestore * 30L));
        }

        userRepository.save(user);

        return restoredLives;
    }

    

}
