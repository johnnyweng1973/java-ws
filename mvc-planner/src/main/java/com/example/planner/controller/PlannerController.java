package com.example.planner.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.planner.model.Dish;
import com.example.planner.model.PlannerDay;
import com.example.planner.service.DishService;
import com.example.planner.service.PlannerDayService;

@Controller
public class PlannerController{
	@Autowired
	private DishService dishService;
	
	@Autowired
	private PlannerDayService plannerDayService;
	  
	@GetMapping("/")
    public String index(Model model) {
        List<Dish> dishes = dishService.getAllDishes();
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
	
	@PostMapping("/save-week")
	public ResponseEntity<String> saveWeekDays(@RequestBody List<PlannerDay> weekDays) {
	    try {
	        plannerDayService.saveWeekDays(weekDays);
	        return ResponseEntity.ok("Meals saved successfully!");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save meals.");
	    }
	}

	@GetMapping("/current-week")
    @ResponseBody
    public List<PlannerDay> getMeals(
    		  @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
              @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return plannerDayService.getPlannerDayBetweenDates(startDate, endDate);
    }
}