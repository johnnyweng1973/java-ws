package com.example.mvc.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
	private static String csvFileName = new String("problem.csv");

	@Autowired
	private MathProblemService mathProblemService;

	@Autowired
	private MathSubCategoryService mathSubcategoryService;

	@GetMapping
	public String manageMathProblems(Model model) {
		//List<MathProblem> mathProblems = mathProblemService.findAll();
	
		// Logging the number of elements in the 'memos' list
	   //	log.info("Number of elements in the 'problems' list: {}", mathProblems.size());

		//model.addAttribute("mathProblems", mathProblems);
        model.addAttribute("subjects", TestSubjectType.values());
		model.addAttribute("newMathProblem", new MathProblem());
		return "manage_mathproblem";
	}

	@GetMapping("/tabs")
	public String list(Model model) {
		// JPA will return an empty list if empty
		List<MathProblem> mathProblems = mathProblemService.findAll();
		
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
			return "query_chinese";
	}

	@GetMapping("/general")
	public String getGeneralPage()
	{
        return "general_table";
	}
	
	@GetMapping("/choose")
	public String getChoosePage()
	{
        return "choose_practice";
	}

	
	@GetMapping("/general/data")
	public ResponseEntity<List<MathProblem>> general(
	    @RequestParam(name = "table") String tableName, 
	    @RequestParam(name = "subject") TestSubjectType subject,
	    @RequestParam(name = "category", required = false) String category,
	    @RequestParam(name = "subcategory", required = false) String subCategory)
	{
	    if ("math_problem".equals(tableName)) {
			if (category != null && category.equals("pascal9") && subCategory != null && !subCategory.isEmpty()) {
				List<MathProblem> mathProblems = mathProblemService.findBySubCategory(subCategory);

				for (MathProblem mathProblem : mathProblems) {
					if (mathProblem.getImage() != null) {
						int imageSize = mathProblem.getImage().length; // Get the size directly
					} else {
						System.out.println("No image for MathProblem with ID " + mathProblem.getId());
					}
				}
				return ResponseEntity.ok(mathProblems);
			} else if (category == null || category.isEmpty()) {
	            return ResponseEntity.ok(mathProblemService.findBySubject(subject));
	        } else {
	        	log.info("/general/data category " + category);
	            return ResponseEntity.ok(mathProblemService.findBySubjectAndCategory(subject, category));
	        }
	    } else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	    }
	}

	private void handleAddDuanJu(
	        MathProblem problem,
	        List<MathProblem> problemList,
	        Map<String, ChineseCharacter> map) 
	{
	    StringBuilder appendedSolution = new StringBuilder();
	    
	    for (char character : problem.getDescription().toCharArray()) {
	        String characterString = String.valueOf(character);
	        ChineseCharacter chineseCharacter = map.get(characterString);
	        
	        if (chineseCharacter != null) {
	            appendedSolution.append(chineseCharacter.getPinyin()).append(" ");
	        }
	        
	        if (!mathProblemService.findByDescriptionAndCategory(characterString, "生字")) {
	            MathProblem newCharacterProblem = new MathProblem(problem);
	            newCharacterProblem.setDescription(characterString);
	            newCharacterProblem.setCategory("生字");
	            
	            if (chineseCharacter != null) {
	                newCharacterProblem.setSolution(chineseCharacter.toString());
	            } else {
	                // Handle case where solution is not found in the map
	                // Set solution to null or an empty string, depending on your requirements
	                newCharacterProblem.setSolution(""); // Or null, depending on your use case
	            }
	            // Add the new MathProblem to the list or database, depending on your setup
	            problemList.add(newCharacterProblem);
	        }
	    }
	    
	    // Append the accumulated pinyin to the solution of the incoming problem
	    problem.setSolution(problem.getSolution() + " " + appendedSolution.toString().trim());
	}

	// Load the file and parse its contents into the characterWholeMap
	private Map<String, ChineseCharacter> loadCharacterMap() {
	    try {
	        // Read JSON file and convert to Map
	        ObjectMapper objectMapper = new ObjectMapper();
	        // Read the file from the classpath
	        InputStream inputStream = MathProblemsController.class.getResourceAsStream("/中文字库.txt");
	        
	        return  objectMapper.readValue(
	                    inputStream, 
	                    new TypeReference<Map<String, ChineseCharacter>>() {}
	        );
	    } catch (IOException e) {
	        e.printStackTrace();
	        return Collections.emptyMap(); // Return an empty map in case of an exception
	    }
	}
	
	private String handleAddShengZhi(
			    MathProblem newMathProblem,
			    Map<String, ChineseCharacter> characterWholeMap) {
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
		        
		        // Add the new MathProblem object to the list
		        mathProblems.add(mathProblem);
		    }
		}
        mathProblemService.saveAll(mathProblems);
		return "redirect:/math";
	}
   	
	@PostMapping("/add")
	public String addMathProblem(@ModelAttribute("newMathProblem") MathProblem newMathProblem) {
		MathSubCategory subcategory = 
		    mathSubcategoryService.findOrCreateSubcategory(newMathProblem.getMathSubCategory().getName());
		// Set the subcategory back to the newMathProblem object
	    newMathProblem.setMathSubCategory(subcategory);

		Map<String, ChineseCharacter> characterWholeMap = null;
		if (newMathProblem.getSubject() == TestSubjectType.chinese &&
		    (newMathProblem.getCategory().equals("短句") || 
			 newMathProblem.getCategory().equals("生字")))
		{
			characterWholeMap = loadCharacterMap();
		}
		
		if (newMathProblem.getSubject() == TestSubjectType.chinese &&
				"生字".equals(newMathProblem.getCategory()))
		{
           return handleAddShengZhi(newMathProblem, characterWholeMap);	
		}
		
		String[] problemsArray = newMathProblem.getDescription().split("Problem:");
		List<MathProblem> problemList = new ArrayList<>();
		if(problemsArray.length > 1) {
			for(String problem: problemsArray) {
				problem = problem.trim();
				if(!problem.isEmpty()) {
					MathProblem mathProblem = new MathProblem(newMathProblem);
					// Edge case: ensure "Solution:" exists in the string
			        if (problem.contains("Solution:")) {
			            String[] problemSolution = problem.split("Solution:");

			            mathProblem.setDescription(problemSolution[0].trim());
			            mathProblem.setSolution(problemSolution[1].trim());
			        } else {
			        	mathProblem.setDescription(problem);
			        }
					mathProblem.setMathSubCategory(subcategory);
					if (mathProblemService.findByDescriptionAndCategory(
							mathProblem.getDescription(),
							mathProblem.getCategory()))
					{
						log.info("existing");
						continue;
					}
					else 
					{
						// Check if the subject is Chinese and the category is "duanju"
			            if (mathProblem.getSubject() == TestSubjectType.chinese &&
			            		mathProblem.getCategory().equals("短句"))
			            {
	                        handleAddDuanJu(mathProblem, problemList, characterWholeMap);
			            }
			            log.info("create a new problem {}", mathProblem.toString());
					}
					problemList.add(mathProblem);		
				}
			}
		}
		else {
			if (newMathProblem.getSubject() == TestSubjectType.chinese) {
				if (mathProblemService.findByDescriptionAndCategory(
						newMathProblem.getDescription(),
						newMathProblem.getCategory()))
				{
					log.info("existing");
					return "redirect:/math";
				}
				
				if (newMathProblem.getCategory().equals("短句")){
					handleAddDuanJu(newMathProblem, problemList, characterWholeMap);	
				}
            }
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
	    List<MathProblem> mathProblems;

	    if (subject != TestSubjectType.chinese) {
	        try {
	            // Convert the JSON string from the request body into a map
	            Map<Long, List<Long>> requestBodyMap = objectMapper.readValue(
	                    requestBody,
	                    new TypeReference<Map<Long, List<Long>>>() {}
	            );
	            // Handle the map as needed (e.g., perform database queries)
	            mathProblems = mathProblemService.findMathProblems(subject, category, subcategory, requestBodyMap);
	            // Return the map in the response
	            return ResponseEntity.ok(mathProblems);
	        } catch (JsonParseException | JsonMappingException e) {
	            // Handle JSON parsing or mapping exceptions
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	        } catch (IOException e) {
	            // Handle IO exceptions (including any other unexpected IO issues)
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        }
	    } else {
	        if ("练习".equals(category) && (subcategory == null || subcategory.isEmpty())) {
	            // Handle the specific case for Chinese subject with 练习 category
	            try {
	                // Convert the JSON string from the request body into a map
	                Map<String, String> map = objectMapper.readValue(
	                        requestBody,
	                        new TypeReference<Map<String, String>>() {}
	                );
	                // Handle specific logic for Chinese subject with 练习 category
	                String paraValue = map.get("para");
	                if (paraValue != null) {
	                    // Parse the range
	                    String[] rangeParts = paraValue.split("-");
	                    if (rangeParts.length == 2) {
	                        int start = Integer.parseInt(rangeParts[0].trim());
	                        int end = Integer.parseInt(rangeParts[1].trim());
	                        int pageSize = end - start + 1;
	                        int pageNumber = start / pageSize;

	                        // Fetch math problems from repository within the specified range using pageable
	                        Pageable pageable = PageRequest.of(pageNumber, pageSize);
	                        Page<MathProblem> page = mathProblemService.findByCategoryWithPageable(category, pageable);
	                        mathProblems = page.getContent();
	                        log.info("start {} end {}", start, end);
//	                        for (MathProblem problem: mathProblems) {
//	                        	log.info("problem: {}", problem.toString());
//	                        }

	                        // Return the page in the response
	                        return ResponseEntity.ok(mathProblems);
	                    } else {
	                        // Handle invalid range format
	                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	                    }
	                }
	                // Since no math problems are retrieved here, assume an empty list
	                mathProblems = Collections.emptyList();
	                // Return an empty list in the response
	                return ResponseEntity.ok(mathProblems);
	            }catch (JsonParseException | JsonMappingException e) {
		            // Handle JSON parsing or mapping exceptions
		            e.printStackTrace();
		            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		        }catch (IOException e) {
	                // Handle IO exceptions (including any other unexpected IO issues)
	                e.printStackTrace();
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	            }
	        } else {
	            // Handle other cases for Chinese subject where category is not 练习
	            mathProblems = mathProblemService.findMathProblems(subject, category, subcategory, null);
	            // Return the map in the response
	            return ResponseEntity.ok(mathProblems);
	        }
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
//    
//    @GetMapping("/archive")
//	@ResponseBody
//	public ResponseEntity<String> archiveMathProblem() {
//    	List<MathSubCategory> subCategories = mathSubcategoryService.findAll();
//    	
//    	List<MathProblem> mathProblems = mathProblemService.findAll();
//    	if (writeProblemsToCSV(mathProblems, csvFileName)) {
//			
//			return ResponseEntity.ok("Memo list archived successfully");
//		} else {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//					.body("Failed to retrieve memo list from Memo Service");
//		}
//    	
//    }
// 
    
   

}
