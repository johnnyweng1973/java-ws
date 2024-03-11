package com.example.mvcmathtest.controller;

import java.io.IOException;
import java.util.List;

import org.antlr.v4.runtime.atn.SemanticContext.AND;
import org.json.JSONException;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.example.mvcmathtest.model.TestProblem;
import com.example.mvcmathtest.service.RestService;
import com.example.mvcmathtest.service.TestProblemService;
import com.example.mvcmathtest.util.ExcludeListGenerator;


@Controller
public class TestController {
	private static final Logger log = LoggerFactory.getLogger(TestController.class);
	
	@Autowired
	private TestProblemService testProblemService;

	@Autowired
	private RestService restService;
	
	@GetMapping("/test")
	public String getATest(Model model) {
        // get all test problems
		List<TestProblem> oldTestProblems = testProblemService.getAll();
        // convert list to map and serialize map to json string, if map is empty, return {}
		String excludeListString = ExcludeListGenerator.generateExcludeList(oldTestProblems);
		// Make a POST request to the math problem service
        List<TestProblem> testProblems = restService.fetchMathProblems(excludeListString);
        model.addAttribute("problems", testProblems);
		return "test_paper";
	}
}
