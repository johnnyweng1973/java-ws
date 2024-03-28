package com.example.mvcmathtest.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import com.example.mvcmathtest.service.RestService;
import com.example.mvcmathtest.service.TestProblemService;
import com.example.mvcmathtest.util.ExcludeListGenerator;
import com.example.mvcmathtest.util.TestSubjectType;
import com.example.radical.ChineseCharacter;
import com.example.radical.HanziRadicalCollection;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;



@Controller
public class TestController {
	private static final Logger log = LoggerFactory.getLogger(TestController.class);

	@Autowired
	private TestProblemService testProblemService;

	@Autowired
	private RestService restService;

	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@GetMapping("/test-success")
	public String handleTestSuccess() {
		return "test_success";
	}

	@GetMapping("/test")
	public String home() {
//		try {
//            Map<String, ChineseCharacter> characterMap = HanziRadicalCollection.getCharacterMap();
//            System.out.println(characterMap);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
		
		return "test2";
	}

	@GetMapping("/test_subject")
	public String getATest(Model model, 
			               @RequestParam TestSubjectType subject,
			               @RequestParam(name = "category", required = false)String category,
			               @RequestParam(name = "subcategory", required = false)String subcategory,
			               @RequestParam(name = "noSpelling", defaultValue = "false")boolean noSpelling) {
		// get all test problems
		List<TestProblem> oldTestProblems = testProblemService.getBySubject(subject);
		// convert list to map and serialize map to json string, if map is empty, return
		// {}
		String excludeListString = ExcludeListGenerator.generateExcludeList(oldTestProblems);
		// Make a POST request to the math problem service
		List<TestProblem> testProblems = restService.fetchMathProblems(subject, category, subcategory, excludeListString);
		if(noSpelling) {
			testProblems.forEach(testProblem -> testProblem.setSolution(null));
		}
		if (subject == TestSubjectType.chinese) {
			log.info("categor is {}", category);
			if ("短句".equals(category)) {
				model.addAttribute("sub", subject.toString());
				model.addAttribute("problems", testProblems);
				return "chinese_test_paper";
			}
			else {
				List<TestProblem> newTestProblems = TestProblem.cloneAndModify(testProblems);
				model.addAttribute("sub", subject.toString());
				model.addAttribute("problems", newTestProblems);
				return "chinese_test_paper_character";
			}
		}
		else {
			model.addAttribute("sub", subject.toString());
			model.addAttribute("problems", testProblems);
			return "test_paper";
		}
			
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
	
	
}
