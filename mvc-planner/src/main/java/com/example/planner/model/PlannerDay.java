package com.example.planner.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data

@Table(name = "planner_day")
public class PlannerDay {
    @Id
    private LocalDate date;

    private String breakfast; // Store dish names as a comma-separated string
    private String lunch;     // Store dish names as a comma-separated string
    private String dinner;    // Store dish names as a comma-separated string

    // Getters and Setters
}
