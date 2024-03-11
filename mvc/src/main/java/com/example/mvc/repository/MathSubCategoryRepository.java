package com.example.mvc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.mvc.model.MathSubCategory;

public interface MathSubCategoryRepository extends JpaRepository<MathSubCategory, Long> {
	 Optional<MathSubCategory> findByName(String name);
}