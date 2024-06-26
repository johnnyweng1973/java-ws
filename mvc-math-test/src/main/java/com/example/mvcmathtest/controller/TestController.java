package com.example.mvcmathtest.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.atn.SemanticContext.AND;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.example.mvcmathtest.dto.MathProblemDTO;
import com.example.mvcmathtest.model.TestProblem;
import com.example.mvcmathtest.model.TimeStampedModel;
import com.example.mvcmathtest.service.RestService;
import com.example.mvcmathtest.service.TestProblemService;
import com.example.mvcmathtest.service.TimeStampedService;
import com.example.mvcmathtest.util.ExcludeListGenerator;
import com.example.mvcmathtest.util.TestSubjectType;
import com.example.radical.ChineseCharacter;
import com.example.radical.HanziRadicalCollection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;



@Controller
public class TestController {
	private static final Logger log = LoggerFactory.getLogger(TestController.class);

	@Autowired
	private TestProblemService testProblemService;

	@Autowired
	private TimeStampedService timeStampedService;
	
	@Autowired
	private RestService restService;

	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@GetMapping("/test-success")
	public String handleTestSuccess() {
		return "test_success";
	}

	@GetMapping("/trial")
	public String handleTest() {
		return "trial";
	}
	
	@GetMapping("/test")
	public String home() {
		return "test2";
	}

	@GetMapping("/test_subject")
	public String getATest(Model model, 
			               @RequestParam TestSubjectType subject,
			               @RequestParam(name = "category", required = false)String category,
			               @RequestParam(name = "subcategory", required = false)String subcategory,
			               @RequestParam(name = "para", required = false) String para) {
		String contentString = "1";
	    if (subject != TestSubjectType.chinese) {
	    	// get all test problems
			List<TestProblem> oldTestProblems = testProblemService.getBySubject(subject);
			contentString = ExcludeListGenerator.generateExcludeList(oldTestProblems);
	    }
	    else {
	        // Create contentString in JSON format for Chinese subject
	        ObjectMapper objectMapper = new ObjectMapper();
	        try {
	            // Create a map with only 'para' field
	            Map<String, String> jsonMap = new HashMap<>();
	            jsonMap.put("para", para);

	            // Serialize map to JSON string
	            contentString = objectMapper.writeValueAsString(jsonMap);
	        } catch (JsonProcessingException e) {
	            e.printStackTrace();
	        }
	    }
		
	    // Make a POST request to the math problem service
		List<TestProblem> testProblems = restService.fetchMathProblems(subject, category, subcategory, contentString);
		if (subject == TestSubjectType.chinese) {
			log.info("categor is {}", category);
		
			model.addAttribute("sub", subject.toString());
			model.addAttribute("problems", testProblems);
			if ("练习".equals(category) || "生字".equals(category)) {
				return "chinese_practice";
			}
			else {
			    return "chinese_test_paper";
			}
		}
		else {
			model.addAttribute("sub", subject.toString());
			model.addAttribute("problems", testProblems);
			return "test_paper";
		}
			
	}

	@GetMapping("/problems")
	public ResponseEntity<List<TestProblem>> getProblems(
			@RequestParam TestSubjectType subject,
			@RequestParam(name = "category", required = false)String category,
			@RequestParam(name = "subcategory", required = false)String subcategory) {
		String excludeListString = "1";
	   
		// Make a POST request to the math problem service
		List<TestProblem> problems = restService.fetchMathProblems(subject, category, subcategory, excludeListString);
		return ResponseEntity.ok(problems);
	}

	@PostMapping("/test")
	@ResponseBody
	public ResponseEntity<String> handleFormSubmission(@RequestBody String jsonData) {
		List<TestProblem> testProblems = new ArrayList<>();
		try {
			// MyClass myInstance = objectMapper.readValue(jsonData, MyClass.class);
			List<TestProblem> problems = objectMapper.readValue(jsonData, new TypeReference<List<TestProblem>>() {
			});
		    
			// Setting the date with current system date after deserialization
			problems.forEach(problem -> problem.setTimestamp(LocalDateTime.now()));

			// Save all entities to the database as a batch operation
			testProblemService.saveAll(problems);

			// Return a success message
			String message = "Form submitted successfully";
			return ResponseEntity.status(HttpStatus.OK).body(message);
		} catch (Exception e) {
			// Handle any exceptions that occur during JSON decoding
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing JSON data");
		}
	}
	
	 @GetMapping("/latestChineseObjective")
	 @ResponseBody
	 public ResponseEntity<String> getLatesChineseObjective() {
		 TimeStampedModel data = timeStampedService.getLatestRecord();
		 log.info("record is {}", data.getData());
		 return ResponseEntity.ok(data.getData());
	 }
	
	
}
