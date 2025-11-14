package com.nur.livequiz.qms.controller;

import com.nur.livequiz.qms.domain.Question;
import com.nur.livequiz.qms.domain.Quiz;
import com.nur.livequiz.qms.domain.QuizStatus;
import com.nur.livequiz.qms.dto.AddQuestionRequest;
import com.nur.livequiz.qms.dto.CreateQuizRequest;
import com.nur.livequiz.qms.repository.QuestionRepository;
import com.nur.livequiz.qms.repository.QuizRepository;
import com.nur.livequiz.qms.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Public API (no auth) for POC:
 * - List quizzes (participants see available ones)
 * - Get a quiz
 * - Create quiz, add question (admin-side for now; we keep it open for POC)
 * - Start/end quiz (switch status LIVE/CLOSED)
 */
@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
public class QuizController {

    @Autowired private QuizService quizService;
    @Autowired private QuizRepository quizRepository;
    @Autowired private QuestionRepository questionRepository;

    // Create a new quiz
    @PostMapping
    public Quiz create(@RequestBody CreateQuizRequest req) {
        Quiz q = new Quiz();
        q.setTitle(req.getTitle());
        q.setDescription(req.getDescription());
        q.setStatus(QuizStatus.DRAFT);
        return quizRepository.save(q);
    }

    // POST /api/quizzes/{quizId}/questions
    @PostMapping("/{quizId}/questions")
    public Quiz addQuestion(@PathVariable Long quizId, @RequestBody AddQuestionRequest req) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        Question question = new Question();
        question.setQuestionText(req.getQuestionText());
        question.setCorrectOption(req.getCorrectOption());

        // Convert options list to JSON string
        if (req.getOptions() != null && !req.getOptions().isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String optionsJson = mapper.writeValueAsString(req.getOptions());
                question.setOptions(optionsJson);
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize options", e);
            }
        }

        question.setQuiz(quiz);
        questionRepository.save(question);
        // Re-fetch with questions loaded
        return quizRepository.findByIdWithQuestions(quizId).orElseThrow();
    }

    // GET /api/quizzes
    @GetMapping
    public List<Quiz> getAll() {
        return quizRepository.findAllWithQuestions();
    }

    // GET /api/quizzes/{id}
    @GetMapping("/{id}")
    public Quiz getOne(@PathVariable Long id) {
        return quizRepository.findByIdWithQuestions(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    }

    // Start a quiz (LIVE)
    @PostMapping("/{id}/start")
    public Quiz startQuiz(@PathVariable Long id) {
        return quizService.startQuiz(id);
    }

    // End a quiz (CLOSED)
    @PostMapping("/{id}/end")
    public Quiz endQuiz(@PathVariable Long id) {
        return quizService.endQuiz(id);
    }

    // Delete a quiz
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        quizRepository.deleteById(id);
    }

    // Update a question
    @PutMapping("/{quizId}/questions/{questionId}")
    public Quiz updateQuestion(@PathVariable Long quizId, @PathVariable Long questionId, @RequestBody AddQuestionRequest req) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        question.setQuestionText(req.getQuestionText());
        question.setCorrectOption(req.getCorrectOption());

        // Convert options list to JSON string
        if (req.getOptions() != null && !req.getOptions().isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String optionsJson = mapper.writeValueAsString(req.getOptions());
                question.setOptions(optionsJson);
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize options", e);
            }
        }

        questionRepository.save(question);
        // Re-fetch with questions loaded
        return quizRepository.findByIdWithQuestions(quizId).orElseThrow();
    }

    // Delete a question
    @DeleteMapping("/{quizId}/questions/{questionId}")
    public Quiz deleteQuestion(@PathVariable Long quizId, @PathVariable Long questionId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        questionRepository.deleteById(questionId);
        // Re-fetch with questions loaded
        return quizRepository.findByIdWithQuestions(quizId).orElseThrow();
    }
}
