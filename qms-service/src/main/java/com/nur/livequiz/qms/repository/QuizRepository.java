package com.nur.livequiz.qms.repository;

import com.nur.livequiz.qms.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    @Query("""
           select distinct q
           from Quiz q
           left join fetch q.questions
           where q.createdBy = :createdBy
           """)
    List<Quiz> findAllWithQuestionsByOwner(@Param("createdBy") Long createdBy);

    @Query("""
           select q
           from Quiz q
           left join fetch q.questions
           where q.id = :id and q.createdBy = :createdBy
           """)
    Optional<Quiz> findByIdWithQuestionsByOwner(@Param("id") Long id,
                                                @Param("createdBy") Long createdBy);

    Optional<Quiz> findByIdAndCreatedBy(Long id, Long createdBy);

    @Query("""
           select q
           from Quiz q
           left join fetch q.questions
           where q.id = :id
           """)
    Optional<Quiz> findByIdWithQuestions(@Param("id") Long id);
}
