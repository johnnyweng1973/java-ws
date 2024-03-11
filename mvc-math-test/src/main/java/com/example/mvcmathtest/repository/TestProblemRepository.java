package com.example.mvcmathtest.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.example.mvcmathtest.model.TestProblem;

public interface TestProblemRepository extends JpaRepository<TestProblem, Long> {

}
