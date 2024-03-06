package com.example.mvc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MathProblem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
	private String category;
	@Column(name = "subcategory") // Specify custom column name
    private String subcategory;
    private String description;
    private String solution;
    private String answer;
    
    public MathProblem(MathProblem problem){
		this.id = problem.id;
		this.category = problem.category;
		this.subcategory = problem.subcategory;
		this.description = problem.description;
		this.solution = problem.solution;
		this.answer = problem.answer;
	}
	
}
