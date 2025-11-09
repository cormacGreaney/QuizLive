package com.nur.livequiz.qms.repository;

import com.nur.livequiz.qms.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    // Find all questions for a given quiz
    List<Question> findByQuizId(Long quizId);
}