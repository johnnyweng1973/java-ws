package com.example.mvc.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.mvc.model.MathProblem;
import com.example.mvc.model.MathSubCategory;
import com.example.mvc.service.MathProblemService;
import com.example.mvc.service.MathSubCategoryService;
import com.example.mvc.util.TestSubjectType;
import com.example.radical.ChineseCharacter;
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
	
		// Logging the number of elements in the 'memos' list
		log.info("Number of elements in the 'problems' list: {}", mathProblems.size());

		model.addAttribute("mathProblems", mathProblems);
        model.addAttribute("subjects", TestSubjectType.values());
		model.addAttribute("newMathProblem", new MathProblem());
		return "manage_mathproblem";
	}

	@GetMapping("/tabs")
	public String list(Model model) {
		// JPA will return an empty list if empty
		List<MathProblem> mathProblems = mathProblemService.getAll();
		
		Map<String, List<MathProblem>> map = new HashMap<>();
		for (MathProblem problem: mathProblems) {
			map.computeIfAbsent(problem.getCategory(), k->new ArrayList<>()).add(problem);
		}
		model.addAttribute("categories", map.keySet());
		model.addAttribute("problemMap", map);
		return "problem_list";
	}
	
	@GetMapping("/query-chinese")
	public String addChinese() {
			return "qeury_chinese";
	}

	@GetMapping("/general")
	public String getGeneralPage()
	{
        return "general_table";
	}
	
	@GetMapping("/general/data")
	public ResponseEntity<List<MathProblem>> general(
		@RequestParam(name = "table") String tableName, 
		@RequestParam(name = "subject") TestSubjectType subject)
	{
	    if ("math_problem".equals(tableName)) {
	    	 return ResponseEntity.ok(mathProblemService.findBySubject(subject));
	    }
	    else {
	    	 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	    }
	}

	
	@PostMapping("/add")
	public String addMathProblem(@ModelAttribute("newMathProblem") MathProblem newMathProblem) {
		MathSubCategory subcategory = mathSubcategoryService.findOrCreateSubcategory(newMathProblem.getMathSubCategory().getName());

		if ("生字".equals(newMathProblem.getCategory())) {
			try {
	            // Read JSON file and convert to Map
			    ObjectMapper objectMapper = new ObjectMapper();
			    // Read the file from the classpath
	            InputStream inputStream = MathProblemsController.class.getResourceAsStream("/中文字库.txt");
	            
	            Map<String, ChineseCharacter> characterWholeMap = objectMapper.readValue(
	            		inputStream, 
	                new TypeReference<Map<String, ChineseCharacter>>() {}
	            );
	            
	            // Create a list to hold the new MathProblem objects
	            List<MathProblem> mathProblems = new ArrayList<>();

	            for (char character : newMathProblem.getDescription().toCharArray()) {
	                String characterString = String.valueOf(character);
	                ChineseCharacter chineseCharacter = characterWholeMap.get(characterString);
	                
	                if (chineseCharacter != null) {
	                    // Create a new MathProblem object
	                    MathProblem mathProblem = new MathProblem(newMathProblem);
	                    mathProblem.setDescription(characterString);
	                    mathProblem.setSolution(chineseCharacter.toString());
	                    mathProblem.setMathSubCategory(subcategory);

	                    // Add the new MathProblem object to the list
	                    mathProblems.add(mathProblem);
	                }
	            }
	            mathProblemService.saveAll(mathProblems);

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
						return "redirect:/math";
		}
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
					if ("练习".equals(newMathProblem.getCategory())) {
						if (mathProblemService.findByDescriptionAndCategory(
							mathProblem.getDescription(), mathProblem.getCategory())){
							log.info("existing");
							continue;
						}else if(mathProblemService.findByDescriptionAndCategory(
								mathProblem.getDescription(), "短句")) {
							log.info("existing in 短句");
							continue;
							
						}
						else {
							log.info("new");
						}
					}
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
    
    @PostMapping("/addAll")
	@ResponseBody
	public ResponseEntity<String> handleFormSubmission(@RequestBody String jsonData) {
		try {
			// MyClass myInstance = objectMapper.readValue(jsonData, MyClass.class);
			List<MathProblem> problems = objectMapper.readValue(jsonData, new TypeReference<List<MathProblem>>() {
			});
			log.info("starting to saveAll");
			mathProblemService.saveAll(problems);
		    // Return a success message
			String message = "Form submitted successfully";
			return ResponseEntity.status(HttpStatus.OK).body(message);
		} catch (Exception e) {
			log.info("exception in addAll handling");
			
			// Handle any exceptions that occur during JSON decoding
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing JSON data");
		}
	}
    
    @PutMapping("/update-all")
    @ResponseBody
    public ResponseEntity<String> updateAll(@RequestBody List<MathProblem> updatedProblems) {
        try {
          	mathProblemService.saveAll(updatedProblems);
            return ResponseEntity.ok().body("Problems updated successfully.");
         } catch (Exception e) {
                return ResponseEntity.status(500).body("Error updating problems: " + e.getMessage());
         }
    }

}
