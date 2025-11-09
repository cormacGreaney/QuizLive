package com.nur.livequiz.qms.repository;

import com.nur.livequiz.qms.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    // later add custom queries like:
    // List<Quiz> findByStatus(QuizStatus status);
}