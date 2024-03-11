package com.example.mvc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.mvc.model.MathProblem;

public interface MathProblemsRepository extends JpaRepository<MathProblem, Long> {
	 List<MathProblem> findTop2ByMathSubCategoryIdAndIdNotIn(Long subcategoryId, List<Long> excludeProblemIds);
	 List<MathProblem> findTop2ByMathSubCategory_Id(Long subcategoryId);

	// @Query("SELECT * FROM MathProblem WHERE mathSubCategory.id = :subcategoryId AND id NOT IN :excludeProblemIds ORDER BY id LIMIT 2")
	// List<MathProblem> findTop2ByMathSubCategoryIdAndIdNotIn(@Param("subcategoryId") Long subcategoryId, @Param("excludeProblemIds") List<Long> excludeProblemIds);

}
