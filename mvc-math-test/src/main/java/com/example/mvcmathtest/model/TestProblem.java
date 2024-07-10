package com.example.mvcmathtest.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.mvcmathtest.controller.TestController;
import com.example.mvcmathtest.dto.MathProblemDTO;
import com.example.mvcmathtest.util.TestSubjectType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
	private static final Logger log = LoggerFactory.getLogger(TestProblem.class);

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
    
    @Enumerated(EnumType.STRING)
    @Column(name = "subject")
    private TestSubjectType subject;
    
    @Column(name = "multiple_answers")
    private boolean multipleAnswers;
    
    @Column(name = "audio_answer", columnDefinition = "BLOB")
    private byte[] audioAnswer; // new field for storing audio data

  
    
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
        this.subject = dto.getSubject();
        this.multipleAnswers = dto.isMultipleAnswers();
        this.audioAnswer = null;
    }
    
   }
