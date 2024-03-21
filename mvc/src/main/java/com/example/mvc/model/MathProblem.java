package com.example.mvc.model;

import com.example.mvc.util.TestSubjectType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
	
	@ManyToOne
    @JoinColumn(name = "subcategory_id")
    private MathSubCategory mathSubCategory;
    
	private String description;
    private String solution;
    private String answer;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "subject")
    private TestSubjectType subject;
    
    @Column(name = "multiple_answers")
    private boolean multipleAnswers;
    
    public MathProblem(MathProblem problem){
		this.id = problem.id;
		this.category = problem.category;
		this.mathSubCategory = problem.mathSubCategory;
		this.description = problem.description;
		this.solution = problem.solution;
		this.answer = problem.answer;
		this.subject = problem.subject;
		this.multipleAnswers = problem.multipleAnswers;
	}
	
}
