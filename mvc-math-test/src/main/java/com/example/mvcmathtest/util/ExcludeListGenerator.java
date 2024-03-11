package com.example.mvcmathtest.util;

import com.example.mvcmathtest.model.TestProblem;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcludeListGenerator {
    public static String generateExcludeList(List<TestProblem> testProblems) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<Long, List<Long>> excludeList = new HashMap<>();

        // Iterate through the list of problems
        for (TestProblem problem : testProblems) {
            long subcategoryId = problem.getSubcategoryId();
            long problemId = problem.getProblemId();

            // Add the problem ID to the list of excluded problem IDs for the subcategory
            excludeList.computeIfAbsent(subcategoryId, k -> new ArrayList<>()).add(problemId);
        }

        try {
            // Convert the exclude list to JSON format
            return objectMapper.writeValueAsString(excludeList);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
