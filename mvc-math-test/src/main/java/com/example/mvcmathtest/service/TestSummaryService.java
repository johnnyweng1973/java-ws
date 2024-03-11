package com.example.mvcmathtest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mvcmathtest.model.TestSummary;
import com.example.mvcmathtest.repository.TestSummaryRepository;

@Service
public class TestSummaryService {
    @Autowired
    private TestSummaryRepository testSummaryRepository;

    public List<TestSummary> getAll() {
        return testSummaryRepository.findAll();
    }

    public TestSummary add(TestSummary testSummary) {
        TestSummary savedTestSummary = testSummaryRepository.save(testSummary);
        testSummaryRepository.flush(); // Flush changes to the database
        return savedTestSummary;
    }
    
    public Optional<TestSummary> findById(Long problemId) {
        return testSummaryRepository.findById(problemId);
    }

    public void update(TestSummary testSummary) {
        testSummaryRepository.save(testSummary);
        return;
    }
}
