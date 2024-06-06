package com.example.mvc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.example.mvc.model.MathProblem;
import com.example.mvc.model.MathSubCategory;
import com.example.mvc.repository.MathProblemsRepository;
import com.example.mvc.repository.MathSubCategoryRepository;
//import com.example.mvc.mathtest.model.TestProblem;
import com.example.mvc.util.TestSubjectType;

import jakarta.persistence.NonUniqueResultException;

@Service
public class MathProblemService {
	@Autowired
	private MathProblemsRepository mathProblemsRepository;

	@Autowired
	private MathSubCategoryRepository mathSubCategoryRepository;

	public List<MathProblem> findAll() {
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
	
	public boolean findByDescriptionAndCategory(String description, String category) {
		 try {
			 Optional<MathProblem> math = 
				 mathProblemsRepository.findByDescriptionAndCategory(description, category);
              
			 if (math.isPresent()) {
				 return true;
			 }
			 else {
				 return false;
			 }
		 } catch (NonUniqueResultException e) {
	            // Handle NonUniqueResultException
	            // Example: Log the error and inform the user
	            System.out.println("Multiple results found for the query.");
	            e.printStackTrace(); // Log the stack trace for debugging
	            // Other error handling logic can go here
	            return true;
	    }
		 catch (IncorrectResultSizeDataAccessException e) {
			    // Log the error and inform the user
			    System.out.println("Incorrect result size for the query.");
			    e.printStackTrace(); // Log the stack trace for debugging

			    // Returning true might not be appropriate here, consider rethrowing the exception or handling it differently
			    return true;
			} 
	}

	public List<MathProblem> findMathProblems(TestSubjectType subject, 
			                                  String category,
			                                  String chineseSubcategory,
			                                  Map<Long, List<Long>> excludeMap) {
		List<MathProblem> combinedMathProblems = new ArrayList<>();
		
		if (subject == TestSubjectType.chinese) {
			// Fetch the subcategoryId from mathSubCategoryRepository
		    Optional<MathSubCategory> optionalMathSubCategory = mathSubCategoryRepository.findByName(chineseSubcategory);
		    if (optionalMathSubCategory.isPresent()) {
		    	MathSubCategory mathSubCategory = optionalMathSubCategory.get();
		    	Long subcategoryId = mathSubCategory.getId();
		    	List<MathProblem> list1 = 
		    	    mathProblemsRepository.findByMathSubCategory_IdAndSubjectAndCategory(subcategoryId, subject, category);
		    	if ("短句".equals(category)) {
		    		list1.addAll(
		    		    mathProblemsRepository.findByMathSubCategory_IdAndSubjectAndCategory(subcategoryId, subject, "生字"));
		    	}
		    	return list1;
		    } else {
		    	if ("全部".equals(chineseSubcategory)){
		    		List<MathProblem> list1 = mathProblemsRepository.findBySubjectAndCategory(subject, category);
		    		if ("短句".equals(category)) {
			    		list1.addAll(
			    		    mathProblemsRepository.findBySubjectAndCategory(subject, "生字"));
			    	}
		    		return list1;
		    	}
		    	else {
		    		return combinedMathProblems;
		    	}
		    }
		}
		// Check if the excludeMap is empty
	    if (excludeMap.isEmpty()) {
	        // Retrieve two problems for each available subcategory
	        List<MathSubCategory> availableSubcategories = mathSubCategoryRepository.findAll();
	        for (MathSubCategory subcategory : availableSubcategories) {
	            List<MathProblem> mathProblemsForSubcategory = mathProblemsRepository.findTop2ByMathSubCategory_IdAndSubject(subcategory.getId(), subject);
	            combinedMathProblems.addAll(mathProblemsForSubcategory);
	        }
	    } else {
	        // Iterate over the entries of the exclude map
	        for (Map.Entry<Long, List<Long>> entry : excludeMap.entrySet()) {
	            Long subcategoryId = entry.getKey();
	            List<Long> excludeProblemIds = entry.getValue();

	            // Query the database to retrieve math problems for the given subcategory ID,
	            // excluding specified problem IDs
	            List<MathProblem> mathProblems = mathProblemsRepository.findTop2ByMathSubCategoryIdAndIdNotInAndSubject(subcategoryId, excludeProblemIds, subject);

	            // Add the retrieved math problems to the combined list
	            combinedMathProblems.addAll(mathProblems);
	        }
	    }
		return combinedMathProblems;
	}
	
	@Transactional
	public void saveAll(List<MathProblem> problems) {
    	mathProblemsRepository.saveAll(problems);
    	mathProblemsRepository.flush();
    }
	
	public List<MathProblem> findBySubject(TestSubjectType subject){
	    return mathProblemsRepository.findBySubject(subject);
	}
			
}
