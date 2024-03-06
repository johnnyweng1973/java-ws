package com.example.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mvc.model.MathProblem;

public interface MathProblemsRepository extends JpaRepository<MathProblem, Long> {

}
