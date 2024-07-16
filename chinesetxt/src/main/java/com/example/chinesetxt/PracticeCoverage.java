package com.example.chinesetxt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PracticeCoverage {

	private static Map<Character, Integer> characterCount = new HashMap<>();
    
	public static String sendPostRequest(String urlString, String category, String subcategory, String subject, String requestBody) {
	        StringBuilder descriptions = new StringBuilder();
	        try {
	            // Encode the parameters
	            String encodedCategory = URLEncoder.encode(category, StandardCharsets.UTF_8.toString());
	            String encodedSubcategory = URLEncoder.encode(subcategory, StandardCharsets.UTF_8.toString());
	            String encodedSubject = URLEncoder.encode(subject, StandardCharsets.UTF_8.toString());

	            // Build the complete URL with encoded parameters
	            String completeUrlString = urlString + "?category=" + encodedCategory + "&subcategory=" + encodedSubcategory + "&subject=" + encodedSubject;
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
	
	 public static void countCharactersInFile(String concatenatedString, String filePath) throws IOException {
	        boolean startCounting = false;

	        // Read lines from file until an empty line after "100%"
	        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                if (line.contains("100%")) {
	                    startCounting = true;
	                    continue; // Skip the line with "100%" itself
	                }
	                
	                if (startCounting && line.trim().isEmpty()) {
	                    // Stop counting at the first empty line after starting to count
	                    break;
	                }

	                if (startCounting) {
	                    // Count characters from the concatenated string
	                    for (char c : concatenatedString.toCharArray()) {
	                        if (isChineseCharacter(c)) {
	                            int count = countOccurrences(line, c);
	                            characterCount.put(c, characterCount.getOrDefault(c, 0) + count);
	                        }
	                    }
	                }
	            }
	        }

	        // Print character counts
//	        System.out.println("Character counts in relevant lines of the file:");
//	        characterCount.entrySet().stream()
//            .sorted(Map.Entry.<Character, Integer>comparingByValue().reversed())
//            .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
//	        
	        // Create a new map<Integer, String> for counts to characters
	        Map<Integer, String> countToCharacters = new HashMap<>();
	        characterCount.forEach((character, count) -> {
	            String currentChars = countToCharacters.getOrDefault(count, "");
	            currentChars += character;
	            countToCharacters.put(count, currentChars);
	        });

	        // Print the new map
	        System.out.println("\nCharacters grouped by count:");
	        countToCharacters.forEach((count, characters) -> System.out.println(count + ": " + characters + " (" + characters.length() + " characters)"));
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

    public static void main(String[] args) throws IOException {
        // Example usage
        String urlString = "http://localhost:8080/math/test-rest";
        String category = "生字";
        String subcategory = "全部";
        String subject="chinese";
        String requestBody="hello";

        String result = sendPostRequest(urlString, category, subcategory, subject, requestBody);
        System.out.println("Concatenated Descriptions: " + result);
        countCharactersInFile(result,"zyj-percentage-ranking-13.txt");
        countCharactersInFile(result,"pfsj-percentage-ranking-13.txt");
        countCharactersInFile(result,"jy-percentage-ranking-13.txt");
        countCharactersInFile(result,"jy2-percentage-ranking-13.txt");
        countCharactersInFile(result,"jy3-percentage-ranking-13.txt");
        countCharactersInFile(result,"jy4-percentage-ranking-13.txt");
        countCharactersInFile(result,"percentage-ranking-13.txt");
    }
}
