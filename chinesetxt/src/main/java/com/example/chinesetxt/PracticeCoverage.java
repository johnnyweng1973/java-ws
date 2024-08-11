package com.example.chinesetxt;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PracticeCoverage {

	private static Map<Character, Integer> characterCount = new HashMap<>();
    
	public static String requestChinese(String category, String subcategory) {
		String urlString; 
        String subject = "chinese";
        String requestBody = "hello";
      	urlString = "http://localhost:8080/math/test-rest";
      
        if (subcategory.matches("\\d+")) {
            subcategory = Ranking.numberToChinese(Integer.parseInt(subcategory));
        }
        return sendPostRequest(urlString, category, subcategory, subject, requestBody);
	}
	
	public static String sendPostRequest(String urlString, 
			                             String category, 
			                             String subcategory,
			                             String subject, 
			                             String requestBody) {
	        
		    StringBuilder descriptions = new StringBuilder();
	        try {
	            // Encode the parameters
	            String encodedCategory = URLEncoder.encode(category, StandardCharsets.UTF_8.toString());
	            String encodedSubcategory = URLEncoder.encode(subcategory, StandardCharsets.UTF_8.toString());
	            String encodedSubject = URLEncoder.encode(subject, StandardCharsets.UTF_8.toString());

	            // Build the complete URL with encoded parameters
	            String completeUrlString = urlString + "?subject=" + encodedSubject;
	            
	            if (!category.isEmpty()) {
	            	completeUrlString +=   "&category=" + encodedCategory;	
	            }
	            
	            if (!subcategory.isEmpty()) {
	            	completeUrlString +=   "&subcategory=" + encodedSubcategory;	
	            }
	            
	            URL url = new URL(completeUrlString);

	            // Open connection
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("POST");
	            conn.setRequestProperty("Content-Type", "application/json; utf-8");
	            conn.setDoOutput(true);

	            // Write request body
	            try (OutputStream os = conn.getOutputStream()) {
	                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
	                os.write(input, 0, input.length);
	            }

	            // Get response code
	            int responseCode = conn.getResponseCode();
	            System.out.println("Response Code: " + responseCode);

	            // Read response
	            if (responseCode == HttpURLConnection.HTTP_OK) { // success
	                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	                String inputLine;
	                StringBuilder response = new StringBuilder();

	                while ((inputLine = in.readLine()) != null) {
	                    response.append(inputLine);
	                }
	                in.close();

	                // Parse JSON response and extract descriptions using Jackson
	                ObjectMapper objectMapper = new ObjectMapper();
	                JsonNode jsonArray = objectMapper.readTree(response.toString());

	                for (JsonNode jsonObject : jsonArray) {
	                    if (jsonObject.has("description")) {
	                        descriptions.append(jsonObject.get("description").asText());
	                        if (category.equals("练习")) {
	                        	descriptions.append("\n");
	                        }
	                    }
	                }
	            } else {
	                System.out.println("GET request not worked");
	            }

	            conn.disconnect();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return descriptions.toString();
	    }
	
	 public static Map<Integer, String> countCharactersInFile(String concatenatedString, String filePath) throws IOException {
	        // Read lines from file until an empty line after "100%"
	        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                    // Count characters from the concatenated string
	                    for (char c : concatenatedString.toCharArray()) {
	                        if (isChineseCharacter(c)) {
	                            int count = countOccurrences(line, c);
	                            characterCount.put(c, characterCount.getOrDefault(c, 0) + count);
	                        }
	                    }
	            }
	        }

	        // Create a new map<Integer, String> for counts to characters
	        Map<Integer, String> countToCharacters = new TreeMap<>();
	        characterCount.forEach((character, count) -> {
	            String currentChars = countToCharacters.getOrDefault(count, "");
	            currentChars += character;
	            countToCharacters.put(count, currentChars);
	        });

	        // Print the new map
	        System.out.println("\nCharacters grouped by count:");
	        countToCharacters.forEach((count, characters) -> System.out.println(count + ": " + characters + " (" + characters.length() + " characters)"));
	    
	        return countToCharacters;
	 }
	 public static Map<Integer, String> countCharactersInString(String concatenatedString, String inputString){
	        // Map to hold the character counts
	        Map<Character, Integer> characterCount = new HashMap<>();

	        // Split the input string into lines
	        String[] lines = inputString.split("\n");

	        // Process each line
	        for (String line : lines) {
	            // Count characters from the concatenated string
	            for (char c : concatenatedString.toCharArray()) {
	                if (isChineseCharacter(c)) {
	                    int count = countOccurrences(line, c);
	                    characterCount.put(c, characterCount.getOrDefault(c, 0) + count);
	                }
	            }
	        }

	        // Create a new map<Integer, String> for counts to characters
	        Map<Integer, String> countToCharacters = new TreeMap<>();
	        characterCount.forEach((character, count) -> {
	            String currentChars = countToCharacters.getOrDefault(count, "");
	            currentChars += character;
	            countToCharacters.put(count, currentChars);
	        });

	        // Print the new map
	        System.out.println("\nCharacters grouped by count:");
	        countToCharacters.forEach((count, characters) -> System.out.println(count + ": " + characters + " (" + characters.length() + " characters)"));

	        return countToCharacters;
	    }

	    // Helper method to count occurrences of a character in a string
	    private static int countOccurrences(String line, char character) {
	        int count = 0;
	        for (int i = 0; i < line.length(); i++) {
	            if (line.charAt(i) == character) {
	                count++;
	            }
	        }
	        return count;
	    }

	    // Helper method to check if a character is a Chinese character
	    private static boolean isChineseCharacter(char c) {
	        return Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN;
	    }

	    public static void processLinesBetweenPercentages(String inputFilePath, String outputFilePath, String prevOutputFile) throws IOException {
	        boolean startProcessing = false;
	        StringBuilder linesToSave = new StringBuilder();
	        Set<String> previousLines = new HashSet<>();

	        // Check if prevOutputFile exists and is not empty
	        File prevFile = new File(prevOutputFile);
	        if (prevFile.exists() && prevFile.length() > 0) {
	            // Read previous output file and store lines in a set
	            try (BufferedReader prevReader = new BufferedReader(new FileReader(prevOutputFile))) {
	                String line;
	                while ((line = prevReader.readLine()) != null) {
	                    previousLines.add(line);
	                }
	            }
	        }

	        // Read input file and process lines
	        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                if (line.startsWith("100%:")) {
	                    startProcessing = true;
	                    continue; // Skip the line with "100%:" itself
	                }

	                if (startProcessing) {
	                    if (line.trim().isEmpty() || line.matches("\\d+%")) {
	                        // Stop processing at the next percentage or empty line after starting to process
	                        break;
	                    }

	                    // Remove parentheses
	                    line = line.replaceAll("\\(|\\)", "");

	                    // Check if line has at least 4 Chinese characters
	                    if (countChineseCharacters(line) >= 4 && !previousLines.contains(line)) {
	                        // Append line to StringBuilder if not in previous output file
	                        linesToSave.append(line).append("\n");
	                    }
	                }
	            }
	        }

	        // Save filtered lines to output file
	        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath))) {
	            writer.print(linesToSave.toString());
	        }
	    }


	    // Helper method to count Chinese characters in a string
	    private static int countChineseCharacters(String line) {
	        int count = 0;
	        for (char c : line.toCharArray()) {
	            if (isChineseCharacter(c)) {
	                count++;
	            }
	        }
	        return count;
	    }
	    
	    // Helper method to print lines containing characters from countToCharacters map
	    private static void printLinesContainingCharacters(String filePath, Map<Integer, String> countToCharacters) throws IOException {
	        // Iterate over each entry in the map
	        for (Map.Entry<Integer, String> entry : countToCharacters.entrySet()) {
	            String characters = entry.getValue();

	            // Iterate over each character in the string
	            for (char c : characters.toCharArray()) {
	            	System.out.println("count:" + entry.getKey() + " character:" + c);
	                // Open the file for each character search
	                try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
	                    String line;
	                    // Read the file line by line
	                    while ((line = br.readLine()) != null) {
	                        // Check if the line contains the character
	                        if (line.indexOf(c) != -1) {
	                            // Print the line if it contains the character
	                            System.out.println(line);
	                        }
	                    }
	                }
	            }
	        }
	    }
	    
	    public static void combineFiles(List<String> filenames, String outputFilename) {
	        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename))) {
	            for (String filename : filenames) {
	                try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
	                    String line;
	                    while ((line = reader.readLine()) != null) {
	                        writer.write(line);
	                        writer.newLine();
	                    }
	                } catch (IOException e) {
	                    System.err.println("Error reading file: " + filename);
	                    e.printStackTrace();
	                }
	            }
	        } catch (IOException e) {
	            System.err.println("Error writing to output file: " + outputFilename);
	            e.printStackTrace();
	        }
	    }
	    
	    public static void filtering() {
	         String outputFilename = "pfsj-sentences.txt";
	         String newOutputFilename = "pfsj-sentences-new.txt";
	         String filterFilename = "filter.txt";
	         filterFileContent(outputFilename, filterFilename, newOutputFilename);
	         
	    }
	    
	    public static void checkCoverage(String level){
	    	 String newChars = requestChinese("生字",level);
	         System.out.println("Concatenated Descriptions: " + newChars);
	         String result = requestChinese("练习",level);
	         System.out.println("Concatenated Descriptions: " + result);
	         countCharactersInString(newChars, result);
	    }

	    public static Map<Integer,String> getCurrentCoverage(){
	    	 String newChars = requestChinese("生字","全部");
	         System.out.println("Concatenated Descriptions: " + newChars);
	         String result = requestChinese("练习","全部");
	         System.out.println("Concatenated Descriptions: " + result);
	         return countCharactersInString(newChars, result);
	    }
	    public static void checkPossibleCoverage(int level) throws IOException{
	    	 String file1 = "epubfolder-ranking-" + level + ".txt";
	    	 String file2 = "main-ranking-" + level + ".txt";
	    	 String outputFile = "candidate-selection-"+ level + ".txt";
	    	 
	    	 String newChars = requestChinese("生字","全部");
	         System.out.println("Concatenated Descriptions: " + newChars);
	         String result = requestChinese("练习","全部");
	         
	         System.out.println("Concatenated Descriptions: " + result);
	         result = removeLinesFromConcatenatedFiles(result, file1, file2);
	         countCharactersInString(newChars, result);
	         
	         Path path = Paths.get(outputFile);
	         Files.writeString(path, result);
	    }
	    
	    public static String removeLinesFromConcatenatedFiles(String existingString, String file1, String file2) throws IOException {
	        // Read files to lists of strings
	        List<String> linesFile1 = Files.readAllLines(Paths.get(file1));
	        List<String> linesFile2 = Files.readAllLines(Paths.get(file2));

	        // Combine file contents into a set to remove duplicates
	        Set<String> combinedLines = new HashSet<>(linesFile1);
	        combinedLines.addAll(linesFile2);

	        // Split existingString into a set of lines
	        Set<String> existingLines = new HashSet<>(List.of(existingString.split("\\r?\\n")));

	        // Remove lines from combinedLines that are present in existingLines
	        combinedLines.removeAll(existingLines);

	        // Join the result lines back into a single string
	        return String.join(System.lineSeparator(), combinedLines);
	    }


	    
	    public static void filterFileContent(String inputFile, String filterFile, String outputFile) {
	        Set<String> filterLines = new HashSet<>();
	        
	        // Read filterFile lines into a Set
	        try (BufferedReader filterReader = new BufferedReader(new FileReader(filterFile))) {
	            String line;
	            while ((line = filterReader.readLine()) != null) {
	                filterLines.add(line.trim());
	            }
	        } catch (IOException e) {
	            e.printStackTrace(); // Handle the exception appropriately
	            return; // Exit the function if there's an error reading the filter file
	        }
	        
	        // Read inputFile, filter lines, and write to outputFile
	        try (BufferedReader inputReader = new BufferedReader(new FileReader(inputFile));
	             BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFile))) {
	            String inputLine;
	            while ((inputLine = inputReader.readLine()) != null) {
	                boolean containsFilterLine = false;
	                for (String filterLine : filterLines) {
	                    if (inputLine.contains(filterLine)) {
	                        containsFilterLine = true;
	                        break;
	                    }
	                }
	                if (!containsFilterLine) {
	                    outputWriter.write(inputLine);
	                    outputWriter.newLine();
	                }
	            }
	        } catch (IOException e) {	            e.printStackTrace(); // Handle the exception appropriately
	            return; // Exit the function if there's an error reading/writing the file
	        }
	    }
	  
	    public static void createDeltaFile(String deltaFilePath, String file1Path, String file2Path) {
	        try {
	            // Read lines from the second file
	            Set<String> file1Lines = new HashSet<>(Files.readAllLines(Paths.get(file1Path)));

	            // Read lines from the third file
	            Set<String> file2Lines = new HashSet<>(Files.readAllLines(Paths.get(file2Path)));

	            // Create a set for the delta lines
	            Set<String> deltaLines = new HashSet<>(file1Lines);
	            
	            // Remove all lines that appear in the third file
	            deltaLines.removeAll(file2Lines);

	            // Write the delta lines to the first file
	            Files.write(Paths.get(deltaFilePath), deltaLines);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    public static void createMainDeltaFile() throws IOException {
          processLinesBetweenPercentages("pfsj-percentage-ranking-14.txt", "pfsj-ranking-14.txt","");
          processLinesBetweenPercentages("jy1-percentage-ranking-14.txt", "jy1-ranking-14.txt","");
          processLinesBetweenPercentages("zyj-percentage-ranking-14.txt", "zyj-ranking-14.txt","");

          List<String> filenames = List.of("pfsj-ranking-14.txt", "jy1-ranking-14.txt", "zyj-ranking-14.txt");
	      String outputFilename = "main-ranking-14.txt";
          combineFiles(filenames, outputFilename);
          createDeltaFile("delta-main-ranking-14.txt","main-ranking-14.txt","main-ranking-13.txt");
	    }

	    public static void createSecondDeltaFile() throws IOException {
	          processLinesBetweenPercentages("epubfolder-percentage-ranking-14.txt", "epubfolder-ranking-14.txt","");
	          createDeltaFile("delta-epubfolder-ranking-14.txt","epubfolder-ranking-14.txt","epubfolder-ranking-13.txt");
		    }
    
	    public static String sendGetRequest(String urlString, String subject, String category, String subcategory) {
	        StringBuilder descriptions = new StringBuilder();
	        try {
	            String encodedCategory = URLEncoder.encode(category, StandardCharsets.UTF_8.toString());
	            String encodedSubcategory = URLEncoder.encode(subcategory, StandardCharsets.UTF_8.toString());
	            String encodedSubject = URLEncoder.encode(subject, StandardCharsets.UTF_8.toString());

	            String completeUrlString = urlString + "?table=math_problem&subject=" + encodedSubject;

	            if (!category.isEmpty()) {
	                completeUrlString += "&category=" + encodedCategory;
	            }

	            if (!subcategory.isEmpty()) {
	                completeUrlString += "&subcategory=" + encodedSubcategory;
	            }

	            URL url = new URL(completeUrlString);

	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("GET");
	            conn.setRequestProperty("Accept", "application/json");

	            int responseCode = conn.getResponseCode();
	            System.out.println("Response Code: " + responseCode);
	            
	            // Create the "saved" directory if it doesn't exist
	            File savedDir = new File("saved");
	            if (!savedDir.exists()) {
	                savedDir.mkdir();
	            }

	            if (responseCode == HttpURLConnection.HTTP_OK) { // success
	                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	                String inputLine;
	                StringBuilder response = new StringBuilder();

	                while ((inputLine = in.readLine()) != null) {
	                    response.append(inputLine);
	                }
	                in.close();

	                ObjectMapper objectMapper = new ObjectMapper();
	                JsonNode jsonArray = objectMapper.readTree(response.toString());

	                for (JsonNode jsonObject : jsonArray) {

	                    if (jsonObject.has("image")) {
	                        String imageBase64 = jsonObject.get("image").asText();
	                        byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
	                     // Perform a second decode
	                        byte[] doubleDecodedBytes = Base64.getDecoder().decode(imageBytes);


	                        // Save the image in the "saved" directory
	                        String imageName = jsonObject.get("description").asText() + ".png";
	                        File imageFile = new File(savedDir, imageName);
	                        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imageFile))) {
	                            bos.write(doubleDecodedBytes);
	                            System.out.println("Saved image as: " + imageFile.getAbsolutePath());
	                        }
	                        System.out.println("original len " + imageBase64.length());
	                        System.out.println("decode len " + imageBytes.length);
	                        System.out.println("double decode len " + doubleDecodedBytes.length);
	                    }
	                }
	            } else {
	                System.out.println("GET request not worked");
	            }

	            conn.disconnect();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return descriptions.toString();
	    }    

	    public static void main(String[] args) throws IOException {
        // Example usage
//        List<String> filenames = List.of("pfsj-ranking-14.txt", "jy1-ranking-14.txt", "zyj-ranking-14.txt");
//	      String outputFilename = "main-ranking-14.txt";
//          combineFiles(filenames, outputFilename);
//
//        String result = requestChinese("生字","十三级");
//        System.out.println("Concatenated Descriptions: " + result);
//        countCharactersInFile(result, "ranking-13.txt");
//        //filtering();
//        processLinesBetweenPercentages("epubfolder-percentage-ranking-13.txt", "epubfolder-ranking-13.txt","");
//          processLinesBetweenPercentages("pfsj-percentage-ranking-14.txt", "pfsj-ranking-14.txt","");
//          processLinesBetweenPercentages("jy1-percentage-ranking-14.txt", "jy1-ranking-14.txt","");
//          processLinesBetweenPercentages("zyj-percentage-ranking-14.txt", "zyj-ranking-14.txt","");

	    	//processLinesBetweenPercentages("percentage-ranking-14.txt", "ranking-14.txt","");
            //createDeltaFile("delta-main-ranking-14.txt","main-ranking-14.txt","main-ranking-13.txt");
	    	//checkCoverage("14");
	    	//createSecondDeltaFile();
	    	checkCoverage("全部");
	    	checkPossibleCoverage(15);
    }
}
