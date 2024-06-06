package com.example.mvc.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mvc.model.MathSubCategory;
import com.example.mvc.repository.MathSubCategoryRepository;

@Service
public class MathSubCategoryService {

    @Autowired
    private MathSubCategoryRepository subcategoryRepository;
    
    public List<MathSubCategory> findAll(){
    	return subcategoryRepository.findAll();
    }

    public MathSubCategory findOrCreateSubcategory(String subcategoryName) {
        // Try to find the subcategory by name
        Optional<MathSubCategory> optionalSubcategory = subcategoryRepository.findByName(subcategoryName);

        if (optionalSubcategory.isPresent()) {
            return optionalSubcategory.get(); // Return the existing subcategory
        } else {
            // Create a new subcategory if it doesn't exist
            MathSubCategory subcategory = new MathSubCategory();
            subcategory.setName(subcategoryName);
            return subcategoryRepository.save(subcategory);
        }
    }
}

