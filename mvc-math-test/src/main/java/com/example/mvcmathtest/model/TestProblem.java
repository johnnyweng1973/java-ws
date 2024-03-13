package com.example.mvcmathtest.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.mvcmathtest.dto.MathProblemDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TestProblem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "problem_id")
    private Long problemId;
    
    //@Column(name = "date_created")
    //private LocalDate dateCreated;
   
    @Column(name = "category")
    private String category;
    
    @Column(name = "subcategory")
    private String subcategory;
    
    @Column(name = "subcategory_id")
    private int subcategoryId;
        
    @Column(name = "problem_description")
    private String problemDescription;
    
    @Column(name = "solution")
    private String solution;
    
    @Column(name = "answer")
    private String answer;

    @Column(name = "is_correct")
    private boolean isCorrect;
    
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    
 // Constructor
    public TestProblem(MathProblemDTO dto) {
    	this.id = (long) 0;
    	this.problemId = dto.getId();
        this.category = dto.getCategory();
        this.subcategory = dto.getMathSubCategory().getName();
        this.subcategoryId = dto.getMathSubCategory().getId();
        this.problemDescription = dto.getDescription();
        this.solution = dto.getSolution();
        this.answer = dto.getAnswer();
        this.isCorrect = false;
    }
}
