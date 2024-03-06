package com.example.mvc.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.mvc.model.MathProblem;
import com.example.mvc.service.MathProblemService;

@Controller
@RequestMapping("/math")
public class MathProblemsController {
	private static final Logger log = LoggerFactory.getLogger(MathProblemsController.class);

	@Autowired
	private MathProblemService mathProblemService;

	@GetMapping
	public String manageMathProblems(Model model) {
		List<MathProblem> mathProblems;
		try {
			mathProblems = mathProblemService.getAll();
		} catch (Exception e) {
			mathProblems = new ArrayList<>();
		}
		// Logging the number of elements in the 'memos' list
		log.info("Number of elements in the 'problems' list: {}", mathProblems.size());

		model.addAttribute("mathProblems", mathProblems);
		model.addAttribute("newMathProblem", new MathProblem());
		return "manage_mathproblem";
	}

	@PostMapping("/add")
	public String addMathProblem(@ModelAttribute("newMathProblem") MathProblem newMathProblem) {
		String[] problemsArray = newMathProblem.getDescription().split("Problem:");
		//newMathProblem.setId(1); 
		if(problemsArray.length > 1) {
			for(String problem: problemsArray) {
				if(problem.trim().length() > 0) {
					// Logging the number of elements in the 'memos' list
					MathProblem mathProblem = new MathProblem(newMathProblem);
					mathProblem.setDescription(problem);
					log.info("create a new problem {}", mathProblem.toString());
					mathProblemService.add(mathProblem);		
				}
			}
		}
		else {
			mathProblemService.add(newMathProblem);
		}
		return "redirect:/math";
	}

}
