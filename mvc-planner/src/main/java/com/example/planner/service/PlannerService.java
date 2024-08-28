package com.example.planner.service;

import com.example.planner.model.Dish;
import com.example.planner.repository.PlannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlannerService {

 @Autowired
 private PlannerRepository dishesRepository;

 public List<Dish> getAllDishes() {
     return dishesRepository.findAll();
 }

 // Additional service methods can be added here
}
