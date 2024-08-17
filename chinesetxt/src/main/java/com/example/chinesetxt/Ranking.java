package com.example.chinesetxt;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Ranking {

    public static void main(String[] args) {
    	createPercentageFile(17);
    	createSecondPercentageFile(17);
    }
    public static void createPercentageFile(int level) {
        String[] sentencesFiles = { "pfsj-sentences.txt", "zyj-sentences.txt"};
        String[] outputFiles = {
            "pfsj-percentage-ranking-" + level + ".txt",
            "zyj-percentage-ranking-" + level + ".txt"
        };
        String[] outputRankingFiles = {
            "pfsj-ranking-" + level + ".txt",
            "zyj-ranking-" + level + ".txt"
        };
        
        List<String> filenames = List.of(outputRankingFiles);
        String outputMainRankingFile = "main-ranking-" + level + ".txt";
        String prevOutputMainRankingFile = "main-ranking-" + (level - 1) + ".txt";
        String outputMainDeltaFile = "delta-main-ranking-" + level + ".txt";
        
        String chineseCharacters = PracticeCoverage.requestChinese("生字", "全部");
        Map<Integer, List<String>> percentageMap;

        try {
            for (int i = 0; i < sentencesFiles.length; i++) {
                percentageMap = calculateCharacterPercentages(sentencesFiles[i], chineseCharacters);
                writePercentageRanking(percentageMap, outputFiles[i]);
                System.out.println("Character percentage ranking has been written to " + outputFiles[i]);
                PracticeCoverage.processLinesBetweenPercentages(outputFiles[i], outputRankingFiles[i], "");
            }
            
            PracticeCoverage.combineFiles(filenames, outputMainRankingFile);
            PracticeCoverage.createDeltaFile(outputMainDeltaFile, outputMainRankingFile, prevOutputMainRankingFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void createSecondPercentageFile(int level) {
        String sentencesFile1 = "epubfolder-sentences.txt";
        String outputFile1 = "epubfolder-percentage-ranking-" + level + ".txt";
        String outputRankingFile1 = "epubfolder-ranking-" + level + ".txt";
        String outputMainRankingFile = "epubfolder-ranking-" + level + ".txt";
        String prevOutputMainRankingFile = "epubfolder-ranking-" + (level - 1) + ".txt";
        String outputMainDeltaFile = "delta-epubfolder-ranking-" + level + ".txt";

        String chineseCharacters = PracticeCoverage.requestChinese("生字", "全部");
        Map<Integer, List<String>> percentageMap;

        try {
            percentageMap = calculateCharacterPercentages(sentencesFile1, chineseCharacters);
            writePercentageRanking(percentageMap, outputFile1);
            System.out.println("Character percentage ranking has been written to " + outputFile1);

            PracticeCoverage.processLinesBetweenPercentages(outputFile1, outputRankingFile1, "");
            PracticeCoverage.createDeltaFile(outputMainDeltaFile, outputMainRankingFile, prevOutputMainRankingFile);
        } catch (IOException e) {
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

    public static void sendPostRequest(String urlString, String subject, String category, String subcategoryName, String description, File imageFile) {
        try {
            // Encode the parameters
            String encodedSubject = URLEncoder.encode(subject, StandardCharsets.UTF_8.toString());
            String encodedCategory = URLEncoder.encode(category, StandardCharsets.UTF_8.toString());
            String encodedSubcategoryName = URLEncoder.encode(subcategoryName, StandardCharsets.UTF_8.toString());
            String encodedDescription = URLEncoder.encode(description, StandardCharsets.UTF_8.toString());

            StringBuilder formData = new StringBuilder();
            formData.append("subject=").append(encodedSubject)
                    .append("&category=").append(encodedCategory)
                    .append("&mathSubCategory.name=").append(encodedSubcategoryName)
                    .append("&description=").append(encodedDescription)
                    .append("&solution=&answer=&multipleAnswers=false");

            // Convert image to Base64 if it's not null
            if (imageFile != null) {
                try (FileInputStream imageInFile = new FileInputStream(imageFile)) {
                    byte[] imageData = new byte[(int) imageFile.length()];
                    imageInFile.read(imageData);
                    String encodedImage = Base64.getEncoder().encodeToString(imageData);
                    formData.append("&image=").append(URLEncoder.encode(encodedImage, StandardCharsets.UTF_8.toString()));
                    System.out.println("encode len " + encodedImage.length());
                    System.out.println("original " + imageData.length);
                }
            }

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            // Send the request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = formData.toString().getBytes("utf-8");
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
