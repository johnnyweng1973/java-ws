package com.example.mvcmathtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.mvcmathtest.model.TestSummary;

public interface TestSummaryRepository extends JpaRepository<TestSummary, Long> {

}
