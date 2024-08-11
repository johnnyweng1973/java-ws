package com.example.mvcmathtest.dto;

import com.example.mvcmathtest.util.TestSubjectType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class MathProblemDTO {
    private long id;
	private String category;
    private SubCategoryDTO mathSubCategory;
	private String description;
    private String solution;
    private String answer;
    private TestSubjectType subject;
    private Integer importance;
    private boolean multipleAnswers;
    private byte[] image; 
}
