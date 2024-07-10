package com.example.mvcmathtest.repository;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.mvcmathtest.model.TestProblem;
import com.example.mvcmathtest.util.TestSubjectType;

public interface TestProblemRepository extends JpaRepository<TestProblem, Long> {
    List<TestProblem> findBySubject(TestSubjectType subject);
	List<TestProblem> findByTimestampBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
