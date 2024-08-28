package com.example.planner.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.planner.model.Dish;
import com.example.planner.service.PlannerService;

@Controller
public class PlannerController{
	@Autowired
	private PlannerService plannerService;
	  
	@GetMapping("/")
    public String index(Model model) {
        List<Dish> dishes = plannerService.getAllDishes();
        // Extract the unique categories from the dishes
        List<String> categories = dishes.stream()
                .map(Dish::getCategory)
                .distinct()
                .collect(Collectors.toList());

        // Add dishes and categories as separate attributes
        model.addAttribute("dishes", dishes);
        model.addAttribute("categories", categories);
        return "index";  // This returns the "index.html" template
    }
}