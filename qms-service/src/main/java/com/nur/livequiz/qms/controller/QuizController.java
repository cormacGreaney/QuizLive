package com.nur.livequiz.qms.controller;

import com.nur.livequiz.qms.domain.Question;
import com.nur.livequiz.qms.domain.Quiz;
import com.nur.livequiz.qms.domain.QuizStatus;
import com.nur.livequiz.qms.dto.AddQuestionRequest;
import com.nur.livequiz.qms.dto.CreateQuizRequest;
import com.nur.livequiz.qms.repository.QuestionRepository;
import com.nur.livequiz.qms.repository.QuizRepository;
import com.nur.livequiz.qms.service.QuizService;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    @SuppressWarnings("unused")
    private final QuizService quizService;

    public QuizController(QuizRepository quizRepository, QuestionRepository questionRepository, QuizService quizService) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.quizService = quizService;
    }

    private Long requireUserId(String header) {
        if (header == null || header.isBlank()) throw new ForbiddenException("Missing X-User-Id");
        try { return Long.parseLong(header); } catch (NumberFormatException e) { throw new ForbiddenException("Invalid X-User-Id"); }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    static class ForbiddenException extends RuntimeException { public ForbiddenException(String m){super(m);} }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    static class BadRequest extends RuntimeException { public BadRequest(String m){super(m);} }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    static class NotFound extends RuntimeException { public NotFound(String m){super(m);} }

    // ===== Admin: list only my quizzes =====
    @GetMapping
    @Transactional(readOnly = true)
    public List<Quiz> list(@RequestHeader(name = "X-User-Id", required = false) String userHeader) {
        Long userId = requireUserId(userHeader);
        return quizRepository.findAllWithQuestionsByOwner(userId);
    }

    // ===== Admin: create (owns it) =====
    @PostMapping
    public Quiz create(@RequestHeader(name = "X-User-Id", required = false) String userHeader,
                       @RequestBody CreateQuizRequest req) {
        Long userId = requireUserId(userHeader);
        Quiz q = new Quiz();
        q.setTitle(req.getTitle());
        q.setDescription(req.getDescription());
        q.setStatus(QuizStatus.DRAFT);
        q.setCreatedBy(userId);
        return quizRepository.save(q);
    }

    // ===== Public: get quiz by id (participants) =====
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public Quiz getPublic(@PathVariable Long id) {
        return quizRepository.findByIdWithQuestions(id)
                .orElseThrow(() -> new NotFound("Quiz not found"));
    }

    // ===== Admin: start/end only if owned =====
    @PostMapping("/{id}/start")
    public Quiz start(@PathVariable Long id,
                      @RequestHeader(name = "X-User-Id", required = false) String userHeader) {
        Long userId = requireUserId(userHeader);
        Quiz q = quizRepository.findByIdAndCreatedBy(id, userId)
                .orElseThrow(() -> new ForbiddenException("Not found or not owned"));
        q.setStatus(QuizStatus.LIVE);
        return quizRepository.save(q);
    }

    @PostMapping("/{id}/end")
    public Quiz end(@PathVariable Long id,
                    @RequestHeader(name = "X-User-Id", required = false) String userHeader) {
        Long userId = requireUserId(userHeader);
        Quiz q = quizRepository.findByIdAndCreatedBy(id, userId)
                .orElseThrow(() -> new ForbiddenException("Not found or not owned"));
        q.setStatus(QuizStatus.ENDED);
        return quizRepository.save(q);
    }

    // ===== Admin: add/update/delete question (owner) =====
    @PostMapping("/{id}/questions")
    public Quiz addQuestion(@PathVariable Long id,
                            @RequestHeader(name = "X-User-Id", required = false) String userHeader,
                            @RequestBody AddQuestionRequest req) {
        Long userId = requireUserId(userHeader);

        var opts = req.normalizedOptions();
        if (req.getQuestionText() == null || req.getQuestionText().isBlank())
            throw new BadRequest("questionText is required");
        if (opts.isEmpty())
            throw new BadRequest("options must contain at least one value");

        // FIX: accept 1-based correctOption from the frontend
        if (req.getCorrectOption() == null || req.getCorrectOption() < 1 || req.getCorrectOption() > opts.size())
            throw new BadRequest("correctOption must be between 1 and " + opts.size());

        Quiz quiz = quizRepository.findByIdAndCreatedBy(id, userId)
                .orElseThrow(() -> new ForbiddenException("Not found or not owned"));

        Question q = new Question();
        q.setQuestionText(req.getQuestionText());
        q.setOptions(opts);
        q.setCorrectOption(req.getCorrectOption()); // store 1-based to match clients
        q.setQuiz(quiz);
        questionRepository.save(q);

        return quizRepository.findByIdWithQuestionsByOwner(id, userId).orElseThrow();
    }

    @PutMapping("/{quizId}/questions/{questionId}")
    public Quiz updateQuestion(@PathVariable Long quizId,
                               @PathVariable Long questionId,
                               @RequestHeader(name = "X-User-Id", required = false) String userHeader,
                               @RequestBody AddQuestionRequest req) {
        Long userId = requireUserId(userHeader);

        var opts = req.normalizedOptions();
        if (req.getQuestionText() == null || req.getQuestionText().isBlank())
            throw new BadRequest("questionText is required");
        if (opts.isEmpty())
            throw new BadRequest("options must contain at least one value");

        // FIX: accept 1-based correctOption from the frontend
        if (req.getCorrectOption() == null || req.getCorrectOption() < 1 || req.getCorrectOption() > opts.size())
            throw new BadRequest("correctOption must be between 1 and " + opts.size());

        quizRepository.findByIdAndCreatedBy(quizId, userId)
                .orElseThrow(() -> new ForbiddenException("Not found or not owned"));

        Question q = questionRepository.findById(questionId)
                .orElseThrow(() -> new BadRequest("Question not found"));
        q.setQuestionText(req.getQuestionText());
        q.setOptions(opts);
        q.setCorrectOption(req.getCorrectOption()); // keep 1-based
        questionRepository.save(q);

        return quizRepository.findByIdWithQuestionsByOwner(quizId, userId).orElseThrow();
    }

    @DeleteMapping("/{quizId}/questions/{questionId}")
    public Quiz deleteQuestion(@PathVariable Long quizId,
                               @PathVariable Long questionId,
                               @RequestHeader(name = "X-User-Id", required = false) String userHeader) {
        Long userId = requireUserId(userHeader);
        quizRepository.findByIdAndCreatedBy(quizId, userId)
                .orElseThrow(() -> new ForbiddenException("Not found or not owned"));
        questionRepository.deleteById(questionId);
        return quizRepository.findByIdWithQuestionsByOwner(quizId, userId).orElseThrow();
    }

    // ===== Admin: delete quiz (owner) =====
    @DeleteMapping("/{id}")
    public void deleteQuiz(@PathVariable Long id,
                           @RequestHeader(name = "X-User-Id", required = false) String userHeader) {
        Long userId = requireUserId(userHeader);
        Quiz q = quizRepository.findByIdAndCreatedBy(id, userId)
                .orElseThrow(() -> new ForbiddenException("Not found or not owned"));
        quizRepository.delete(q);
    }

    // Compat for clients that can't send DELETE
    @PostMapping("/{id}/delete")
    public void deleteQuizCompat(@PathVariable Long id,
                                 @RequestHeader(name = "X-User-Id", required = false) String userHeader) {
        deleteQuiz(id, userHeader);
    }
}
