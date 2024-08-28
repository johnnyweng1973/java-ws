package com.example.planner.repository;

import com.example.planner.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlannerRepository extends JpaRepository<Dish, Long> {
    // Additional query methods can be defined here if needed
}