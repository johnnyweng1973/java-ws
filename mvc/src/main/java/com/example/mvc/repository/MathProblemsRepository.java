package com.example.mvc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.mvc.model.MathProblem;
import com.example.mvc.util.TestSubjectType;

public interface MathProblemsRepository extends JpaRepository<MathProblem, Long> {
	 List<MathProblem> findTop2ByMathSubCategoryIdAndIdNotInAndSubject(Long subcategoryId, List<Long> excludeProblemIds, TestSubjectType subject);
	 List<MathProblem> findTop2ByMathSubCategory_IdAndSubject(Long subcategoryId, TestSubjectType subject);
	 List<MathProblem> findBySubjectAndCategory(TestSubjectType subject,String category);
	 List<MathProblem> findByMathSubCategory_IdAndSubjectAndCategory(Long subcategoryId, TestSubjectType subject, String category);
	 List<MathProblem> findBySubject(TestSubjectType subject); 
	  
	 Optional<MathProblem> findByDescriptionAndCategory(String description, String category);
	// @Query("SELECT * FROM MathProblem WHERE mathSubCategory.id = :subcategoryId AND id NOT IN :excludeProblemIds ORDER BY id LIMIT 2")
	// List<MathProblem> findTop2ByMathSubCategoryIdAndIdNotIn(@Param("subcategoryId") Long subcategoryId, @Param("excludeProblemIds") List<Long> excludeProblemIds);
	Page<MathProblem> findByCategory(String category, Pageable pageable);

}
