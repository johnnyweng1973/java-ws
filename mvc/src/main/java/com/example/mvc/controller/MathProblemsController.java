package com.example.mvc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.mvc.model.MathProblem;
import com.example.mvc.model.MathSubCategory;
import com.example.mvc.service.MathProblemService;
import com.example.mvc.service.MathSubCategoryService;
import com.example.mvc.util.TestSubjectType;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/math")
public class MathProblemsController {
	private final ObjectMapper objectMapper = new ObjectMapper();

	private static final Logger log = LoggerFactory.getLogger(MathProblemsController.class);

	@Autowired
	private MathProblemService mathProblemService;

	@Autowired
	private MathSubCategoryService mathSubcategoryService;

	@GetMapping
	public String manageMathProblems(Model model) {
		List<MathProblem> mathProblems = mathProblemService.getAll();
		if(mathProblems == null){
			mathProblems = new ArrayList<>();
		}
		// Logging the number of elements in the 'memos' list
		log.info("Number of elements in the 'problems' list: {}", mathProblems.size());

		model.addAttribute("mathProblems", mathProblems);
        model.addAttribute("subjects", TestSubjectType.values());
		model.addAttribute("newMathProblem", new MathProblem());
		return "manage_mathproblem";
	}

	@GetMapping("/add-chinese-character")
	public String addCharacter() {
			return "add_chinese_character_test";
	}

	@PostMapping("/add")
	public String addMathProblem(@ModelAttribute("newMathProblem") MathProblem newMathProblem) {
		MathSubCategory subcategory = mathSubcategoryService.findOrCreateSubcategory(newMathProblem.getMathSubCategory().getName());

		String[] problemsArray = newMathProblem.getDescription().split("Problem:");
		List<MathProblem> problemList = new ArrayList<>();
		//newMathProblem.setId(1); 
		if(problemsArray.length > 1) {
			for(String problem: problemsArray) {
				if(problem.trim().length() > 0) {
					MathProblem mathProblem = new MathProblem(newMathProblem);
					String[] problemAnswer = problem.split("Answer:");
					if (problemAnswer.length <= 1) {
						problemAnswer = problem.split("Answers:");
					}
					mathProblem.setDescription(problemAnswer[0].trim());
					if (problemAnswer.length > 1) {
					    mathProblem.setAnswer(problemAnswer[1].trim());
					}
					mathProblem.setMathSubCategory(subcategory);
					log.info("create a new problem {}", mathProblem.toString());
					problemList.add(mathProblem);		
				}
			}
		}
		else {
			newMathProblem.setMathSubCategory(subcategory);
			problemList.add(newMathProblem);
		}
		mathProblemService.saveAll(problemList);
		return "redirect:/math";
	}
	
    @PostMapping("/test-rest")
    public ResponseEntity<List<MathProblem>> handleRequestBody(
    	    @RequestParam TestSubjectType subject,
    	    @RequestParam(name = "category", required = false) String category,
      	    @RequestParam(name = "subcategory", required = false) String subcategory,
    	    @RequestBody String requestBody)
    {
    	try {
    		 List<MathProblem> mathProblems;
    		if (subject != TestSubjectType.chinese) {
	            // Convert the JSON string from the request body into a map
	            Map<Long, List<Long>> requestBodyMap = objectMapper.readValue(
	                    requestBody,
	                    new TypeReference<Map<Long, List<Long>>>() {}
	            );
	            // Handle the map as needed (e.g., perform database queries)
	            mathProblems = mathProblemService.findMathProblems(subject,category,subcategory, requestBodyMap);
	        }
    		else {
    			mathProblems = mathProblemService.findMathProblems(subject,category,subcategory, null);
    		}

           
            // Return the map in the response
            return ResponseEntity.ok(mathProblems);
        } catch (JsonParseException | JsonMappingException e) {
            e.printStackTrace();
            // Return a response for JSON parsing/mapping exception
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (IOException e) {
            e.printStackTrace();
            // Return a response for IO exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
