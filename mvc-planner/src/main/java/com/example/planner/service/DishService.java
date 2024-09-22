package com.example.planner.service;

import com.example.planner.model.Dish;
import com.example.planner.repository.DishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishService {

 @Autowired
 private DishRepository dishRepository;

 public List<Dish> getAllDishes() {
     return dishRepository.findAll();
 }

 // Additional service methods can be added here
}
