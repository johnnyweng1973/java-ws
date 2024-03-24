package com.example.mvc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.mvc.model.MathProblem;
import com.example.mvc.util.TestSubjectType;

public interface MathProblemsRepository extends JpaRepository<MathProblem, Long> {
	 List<MathProblem> findTop2ByMathSubCategoryIdAndIdNotInAndSubject(Long subcategoryId, List<Long> excludeProblemIds, TestSubjectType subject);
	 List<MathProblem> findTop2ByMathSubCategory_IdAndSubject(Long subcategoryId, TestSubjectType subject);
	 List<MathProblem> findTop40BySubject(TestSubjectType subject);
     
	// @Query("SELECT * FROM MathProblem WHERE mathSubCategory.id = :subcategoryId AND id NOT IN :excludeProblemIds ORDER BY id LIMIT 2")
	// List<MathProblem> findTop2ByMathSubCategoryIdAndIdNotIn(@Param("subcategoryId") Long subcategoryId, @Param("excludeProblemIds") List<Long> excludeProblemIds);

}
