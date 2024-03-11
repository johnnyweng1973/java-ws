package com.example.mvc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.example.mvc.model.MathProblem;
import com.example.mvc.model.MathSubCategory;
import com.example.mvc.repository.MathProblemsRepository;
import com.example.mvc.repository.MathSubCategoryRepository;

@Service
public class MathProblemService {
	@Autowired
	private MathProblemsRepository mathProblemsRepository;

	@Autowired
	private MathSubCategoryRepository mathSubCategoryRepository;

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

	public List<MathProblem> findMathProblems(Map<Long, List<Long>> excludeMap) {
		List<MathProblem> combinedMathProblems = new ArrayList<>();

		// Check if the excludeMap is empty
	    if (excludeMap.isEmpty()) {
	        // Retrieve two problems for each available subcategory
	        List<MathSubCategory> availableSubcategories = mathSubCategoryRepository.findAll();
	        for (MathSubCategory subcategory : availableSubcategories) {
	            List<MathProblem> mathProblemsForSubcategory = mathProblemsRepository.findTop2ByMathSubCategory_Id(subcategory.getId());
	            combinedMathProblems.addAll(mathProblemsForSubcategory);
	        }
	    } else {
	        // Iterate over the entries of the exclude map
	        for (Map.Entry<Long, List<Long>> entry : excludeMap.entrySet()) {
	            Long subcategoryId = entry.getKey();
	            List<Long> excludeProblemIds = entry.getValue();

	            // Query the database to retrieve math problems for the given subcategory ID,
	            // excluding specified problem IDs
	            List<MathProblem> mathProblems = mathProblemsRepository.findTop2ByMathSubCategoryIdAndIdNotIn(subcategoryId, excludeProblemIds);

	            // Add the retrieved math problems to the combined list
	            combinedMathProblems.addAll(mathProblems);
	        }
	    }
		return combinedMathProblems;
	}
}
