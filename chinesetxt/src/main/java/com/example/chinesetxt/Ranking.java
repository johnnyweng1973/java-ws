package com.example.chinesetxt;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Ranking {

    public static void main(String[] args) {
        String charactersFile = "chinese-character.txt";
        String sentencesFile = "sh-sentences.txt";
        String outputFile = "sh-percentage-ranking-13.txt";

        try {
            String chineseCharacters = readChineseCharacters(charactersFile);
            Map<Integer, List<String>> percentageMap = calculateCharacterPercentages(sentencesFile, chineseCharacters);
            writePercentageRanking(percentageMap, outputFile);
            int subcategoryDigit = extractDigitFromFileName(outputFile);
            sendPostRequests(percentageMap, "http://localhost:8080/math/add", subcategoryDigit);
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

    public static int extractDigitFromFileName(String filename) {
        // Use regular expression to find the digits between an underscore and a dot
        String digitStr = filename.replaceAll(".*-(\\d+)\\..*", "$1");

        // If no digits are found, return 0
        if (digitStr.isEmpty()) {
            return 0;
        }
        
        // Parse the extracted string of digits into an integer
        return Integer.parseInt(digitStr);
    }

    public static void sendPostRequests(Map<Integer, List<String>> percentageMap, String urlString, int subcategoryDigit) {
        List<String> sentences = percentageMap.get(100); // Get sentences with 100% percentage
        if (sentences != null) {
            for (String sentence : sentences) {
                // Extract description from the sentence
                String[] parts = sentence.split(" \\(");
                String description = parts[0].trim(); // Extracting the description from the sentence
                // Check if the description has four or more characters
                if (description.length() < 4) {
                    System.out.println("Skipping sentence: " + description + " as it contains less than 4 characters");
                    continue; // Skip sentences with less than 4 characters
                }
                // Log the sentence
                System.out.println("Sending POST request for sentence: " + description);
              
                // Send POST request
                String subcategoryName = numberToChinese(subcategoryDigit);
                sendPostRequest(urlString, subcategoryName, description);
            }
        }
    }
    public static void sendPostRequest(String urlString, String subcategoryName, String description) {
        try {
            // Constant category
            String category = "练习";

            // Encode the parameters
            String encodedCategory = URLEncoder.encode(category, StandardCharsets.UTF_8.toString());
            String encodedSubcategoryName = URLEncoder.encode(subcategoryName, StandardCharsets.UTF_8.toString());
            String encodedDescription = URLEncoder.encode(description, StandardCharsets.UTF_8.toString());

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            // Form data with customized fields
            String formData = "subject=chinese&category=" + encodedCategory + "&mathSubCategory.name=" + encodedSubcategoryName +
                              "&description=" + encodedDescription + "&solution=&answer=&multipleAnswers=false";

            // Send the request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = formData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code for description [" + description + "]: " + responseCode);

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    public static String numberToChinese(int number) {
        final String[] UNITS = {"", "十", "百", "千", "万"};
        final String[] DIGITS = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};

    	if (number < 0 || number >= 100) {
            throw new IllegalArgumentException("Number must be between 0 and 99");
        }

        if (number == 0) {
            return "零级";
        }

        StringBuilder chineseNumber = new StringBuilder();

        if (number >= 10) {
            int tens = number / 10;
            if (tens > 1) {
                chineseNumber.append(DIGITS[tens]);
            }
            chineseNumber.append("十");
        }

        int ones = number % 10;
        if (ones > 0) {
            chineseNumber.append(DIGITS[ones]);
        }

        chineseNumber.append("级");
        return chineseNumber.toString();
    }

}
