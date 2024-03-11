package com.example.mvcmathtest.dto;

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

}
