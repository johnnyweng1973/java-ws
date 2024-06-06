package com.example.chinesetxt;

import java.io.*;
import java.util.*;

public class Ranking {

    public static void main(String[] args) {
        String charactersFile = "chinese-character.txt";
        String sentencesFile = "pfsj-sentences.txt";
        String outputFile = "pfsj-percentage-ranking-9.txt";

        try {
            String chineseCharacters = readChineseCharacters(charactersFile);
            Map<Integer, List<String>> percentageMap = calculateCharacterPercentages(sentencesFile, chineseCharacters);
            writePercentageRanking(percentageMap, outputFile);
            System.out.println("Character percentage ranking has been written to " + outputFile);
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String readChineseCharacters(String filename) throws IOException {
        StringBuilder chineseCharacters = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                chineseCharacters.append(line.trim());
            }
        }
        return chineseCharacters.toString();
    }

    public static Map<Integer, List<String>> calculateCharacterPercentages(String filename, String chineseCharacters) throws IOException {
        Map<Integer, List<String>> percentageMap = new TreeMap<>(Collections.reverseOrder());
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() > 1 && line.length() <= 10) {
                    Map.Entry<Integer, String> result = calculatePercentageAndMissedCharacters(line.trim(), chineseCharacters);
                    int percentage = result.getKey();
                    String missedCharacters = result.getValue();
                    if (percentage < 70 || missedCharacters.length() >= 2) {
                    	continue;
                    }
                    percentageMap.computeIfAbsent(percentage, k -> new ArrayList<>()).add(line.trim() + " (" + missedCharacters + ")");
                } else if (line.length() > 10) {
                    break; // Stop reading when the length exceeds 10
                }
            }
        }
        return percentageMap;
    }

    public static Map.Entry<Integer, String> calculatePercentageAndMissedCharacters(String sentence, String chineseCharacters) {
        StringBuilder missedCharacters = new StringBuilder();
        char[] characters = sentence.toCharArray();
        int totalCharacters = characters.length;
        int foundCharacters = 0;
        for (char character : characters) {
            if (chineseCharacters.contains(String.valueOf(character))) {
                foundCharacters++;
            } else {
                missedCharacters.append(character);
            }
        }
        int percentage = foundCharacters * 100 / totalCharacters;
        return new AbstractMap.SimpleEntry<>(percentage, missedCharacters.toString());
    }

    public static void writePercentageRanking(Map<Integer, List<String>> percentageMap, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Map.Entry<Integer, List<String>> entry : percentageMap.entrySet()) {
                Integer percentage = entry.getKey();
                List<String> sentences = entry.getValue();
                writer.write(percentage + "%:");
                writer.newLine();
                for (String sentence : sentences) {
                    writer.write("(" + sentence + ")");
                    writer.newLine();
                }
                writer.newLine();
            }
        }
    }
}
