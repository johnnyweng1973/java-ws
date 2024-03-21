package com.example.mvcmathtest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mvcmathtest.model.TestProblem;
import com.example.mvcmathtest.repository.TestProblemRepository;
import com.example.mvcmathtest.util.TestSubjectType;

@Service
public class TestProblemService {
    @Autowired
    private TestProblemRepository testProblemRepository;

    public List<TestProblem> getAll() {
        return testProblemRepository.findAll();
    }
    
    public TestProblem add(TestProblem testProblem) {
        TestProblem savedTestProblem = testProblemRepository.save(testProblem);
        testProblemRepository.flush(); // Flush changes to the database
        return savedTestProblem;
    }
    
    public Optional<TestProblem> findById(Long problemId) {
        return testProblemRepository.findById(problemId);
    }

    public void update(TestProblem testProblem) {
        testProblemRepository.save(testProblem);
        return;
    }
    
    public void saveAll(List<TestProblem> problems) {
    	testProblemRepository.saveAll(problems);
    }

	public List<TestProblem> getBySubject(TestSubjectType subject) {
		return testProblemRepository.findBySubject(subject);
	}
}
