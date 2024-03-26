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
    }
    
    public static List<TestProblem> cloneAndModify(List<TestProblem> testProblems) {
        List<TestProblem> clonedProblems = new ArrayList<>();
        Random random = new Random();

        for (TestProblem originalProblem : testProblems) {
            String originalDescription = originalProblem.getProblemDescription();
            int length = originalDescription.length();

            // Generate a sequence of integers from 0 to length - 1
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                indices.add(i);
            }

            // Shuffle the indices to randomize the order
            shuffleList(indices);

            for (int index = 0; index < length; index++) {
            	log.info("index {} char{}", indices.get(index), originalDescription.charAt(indices.get(index)));
                TestProblem clonedProblem = new TestProblem();

                // Set the problem description to the character at the shuffled index
                clonedProblem.setProblemDescription(String.valueOf(originalDescription.charAt(indices.get(index))));

                // Clone other fields using getters and setters
                clonedProblem.setId(originalProblem.getId());
                clonedProblem.setProblemId(originalProblem.getProblemId());
                clonedProblem.setCategory(originalProblem.getCategory());
                clonedProblem.setSubcategory(originalProblem.getSubcategory());
                clonedProblem.setSubcategoryId(originalProblem.getSubcategoryId());
                clonedProblem.setSolution(originalProblem.getSolution());
                clonedProblem.setAnswer(originalProblem.getAnswer());
                clonedProblem.setCorrect(originalProblem.isCorrect());
                clonedProblem.setTimestamp(originalProblem.getTimestamp());
                clonedProblem.setSubject(originalProblem.getSubject());
                clonedProblem.setMultipleAnswers(originalProblem.isMultipleAnswers());

                clonedProblems.add(clonedProblem);
            }
        }

        return clonedProblems;
    }

    // Shuffle the given list
    private static void shuffleList(List<Integer> list) {
        Random random = new Random();
        for (int i = list.size() - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = list.get(index);
            list.set(index, list.get(i));
            list.set(i, temp);
        }
    }
}
