package com.example.mvc.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.example.mvc.model.MathProblem;
import com.example.mvc.repository.MathProblemsRepository;

@Service
public class MathProblemService {
	 @Autowired
    private TransactionTemplate transactionTemplate;

	
    @Autowired
    private MathProblemsRepository mathProblemsRepository;

    public List<MathProblem> getAll() {
        return mathProblemsRepository.findAll();
    }

    public MathProblem add(MathProblem mathProblem) {
        MathProblem savedMathProblem = mathProblemsRepository.save(mathProblem);
        mathProblemsRepository.flush(); // Flush changes to the database
        return savedMathProblem;
    }
    
   	public Optional<MathProblem> findById(Long problemId) {
		return mathProblemsRepository.findById(problemId);
	}

	public void update(MathProblem mathProblem) {
		mathProblemsRepository.save(mathProblem);
		return;
	}

}
