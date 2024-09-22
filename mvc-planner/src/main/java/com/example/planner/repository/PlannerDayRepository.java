package com.example.planner.repository;

import com.example.planner.model.PlannerDay;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlannerDayRepository extends JpaRepository<PlannerDay, LocalDate> {
    @Query("SELECT d FROM PlannerDay d WHERE d.date BETWEEN :startDate AND :endDate")
    List<PlannerDay> findDaysInRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}