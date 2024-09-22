package com.example.planner.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.planner.model.PlannerDay;
import com.example.planner.repository.PlannerDayRepository;

@Service
public class PlannerDayService {
	@Autowired
    private PlannerDayRepository plannerRepository;
	
	 // Method to get the start date of the current week
    public LocalDate getStartOfWeek() {
        return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
    }

    // Method to get the end date of the current week
    public LocalDate getEndOfWeek() {
        return LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
    }

	public void saveWeekDays(List<PlannerDay> weekDays) {
		plannerRepository.saveAll(weekDays);
    }
	
	public List<PlannerDay> getCurrentWeekDays() {
        LocalDate startOfWeek = getStartOfWeek();
        LocalDate endOfWeek = getEndOfWeek();
        return plannerRepository.findDaysInRange(startOfWeek, endOfWeek);
    }

	public List<PlannerDay> getPlannerDayBetweenDates(LocalDate startDate, LocalDate endDate) {
		return plannerRepository.findDaysInRange(startDate, endDate);
	}
}
