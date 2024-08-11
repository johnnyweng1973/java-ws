package com.example.chinesetxt;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CharacterFrequencyAnalyzer {

    private static final Map<String, Integer> frequencyMap = new HashMap<>();
    private static final Map<String, List<String>> sentenceMap = new HashMap<>();

    public static void main(String[] args) {
        // Replace with your actual file names
    	 String[] filenames = {
                 "pfsj-percentage-ranking-16.txt",
                 "zyj-percentage-ranking-16.txt"
             };

        for (String filename : filenames) {
            analyzeFile(filename);
        }

        printResults();
    }

    private static void analyzeFile(String filename) {
        boolean foundFirstEmptyLine = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!foundFirstEmptyLine) {
                    if (line.trim().isEmpty()) {
                        foundFirstEmptyLine = true;
                    }
                    continue;
                }

                // Check if line contains parentheses
                if (!line.contains("(") || !line.contains(")")) {
                    continue; // Skip lines without parentheses
                }

                // Find all pairs of parentheses
                List<Integer> openIndices = new ArrayList<>();
                List<Integer> closeIndices = new ArrayList<>();

                for (int i = 0; i < line.length(); i++) {
                    if (line.charAt(i) == '(') {
                        openIndices.add(i);
                    } else if (line.charAt(i) == ')') {
                        closeIndices.add(i);
                    }
                }

                if (openIndices.size() >= 2 && closeIndices.size() >= 2) {
                    // Extract characters from the inner pair of parentheses
                    int innerOpenIndex = openIndices.get(1);
                    int innerCloseIndex = closeIndices.get(1);
                    if (innerCloseIndex > innerOpenIndex) {
                        String character = line.substring(innerOpenIndex + 1, innerCloseIndex).trim();

                        // Update frequency map
                        frequencyMap.put(character, frequencyMap.getOrDefault(character, 0) + 1);

                        // Update sentence map
                        sentenceMap.putIfAbsent(character, new ArrayList<>());
                        sentenceMap.get(character).add(line.trim());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filename);
            e.printStackTrace();
        }
    }

    private static void printResults() {
        // Create a list from the frequency map and sort it by frequency in descending order
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(frequencyMap.entrySet());
        sortedEntries.sort((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()));

        // Print the results
        System.out.println("Global Results:");
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            String character = entry.getKey();
            int frequency = entry.getValue();
            if (frequency <5) 
            	continue;
            List<String> sentences = sentenceMap.get(character);

            System.out.println("Character: " + character + " Frequency: " + frequency);
            for (String sentence : sentences) {
                System.out.println("  Sentence: " + sentence);
            }
        }
    }
}
