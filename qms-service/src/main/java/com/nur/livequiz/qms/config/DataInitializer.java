package com.nur.livequiz.qms.config;

import com.nur.livequiz.qms.domain.Question;
import com.nur.livequiz.qms.domain.Quiz;
import com.nur.livequiz.qms.domain.QuizStatus;
import com.nur.livequiz.qms.repository.QuestionRepository;
import com.nur.livequiz.qms.repository.QuizRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Seeds one sample quiz if DB is empty. */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seed(QuizRepository quizzes, QuestionRepository questions) {
        return args -> {
            if (quizzes.count() == 0) {
                Quiz q = new Quiz();
                q.setTitle("Sample Quiz");
                q.setDescription("Seeded quiz for POC");
                q.setStatus(QuizStatus.DRAFT);
                q = quizzes.save(q);

                Question a = new Question();
                a.setQuestionText("What is 2 + 2?");
                a.setCorrectOption(2);
                a.setQuiz(q);
                questions.save(a);
            }
        };
    }
}
