package com.nur.livequiz.qms.repository;

import com.nur.livequiz.qms.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    @Query("select distinct q from Quiz q left join fetch q.questions")
    List<Quiz> findAllWithQuestions();

    @Query("select q from Quiz q left join fetch q.questions where q.id = :id")
    Optional<Quiz> findByIdWithQuestions(Long id);
}
