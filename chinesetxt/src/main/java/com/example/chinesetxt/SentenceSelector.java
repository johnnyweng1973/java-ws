package com.example.chinesetxt;

import java.io.*;
import java.util.*;

public class SentenceSelector {

    public static void main(String[] args) throws IOException {
    	 String outputSelecFilePath = "delta-main-ranking-18.txt"; // Replace with your desired output file path
//         addSentencesRequests(outputSelecFilePath1);
//         String outputSelecFilePath = "epubfolder-selection-15.txt"; // Replace with your desired output file path
         addSentencesRequests(outputSelecFilePath);
//        
        // initSelection(16);

    	//nextSelection(15);
    	//globalSelection(15);
        }
    
       
    public static void initSelection(int level) throws IOException{
    	// File paths
        String inputFilePath = "delta-main-ranking-" + Integer.toString(level) + ".txt"; // Replace with your input file path
        String outputFilePath = "main-selection-" + Integer.toString(level) + ".txt"; // Replace with your desired output file path
        int numOfSentences = 240;
        
        // the range of characters to search in the candidate sentences, initially all. 
        int endKey = Integer.MAX_VALUE;
        
        // new characters in a sentences appear too much, drop this sentence
        int leastAppearanceThreshold = 20;
        
        String result = PracticeCoverage.requestChinese("生字",Integer.toString(level));
        System.out.println("Concatenated Descriptions: " + result);
        Map<Integer, String> map = PracticeCoverage.countCharactersInFile(result, inputFilePath);
        System.out.println("orignal frequency map:");
        map.forEach((count, characters) -> System.out.println(count + ": " + characters + " (" + characters.length() + " characters)"));
        Map<Integer, String> map2 = 
        	selectSentences(inputFilePath, result,map, outputFilePath, numOfSentences, endKey, leastAppearanceThreshold); 
        map2.forEach((count, characters) -> System.out.println(count + ": " + characters + " (" + characters.length() + " characters)"));
    }
    
    public static void nextSelection(int level) throws IOException{
    	// File paths
        String inputFilePath = "delta-epubfolder-ranking-" + Integer.toString(level) + ".txt"; // Replace with your input file path
        String origSelectionFilePath = "main-selection-" + Integer.toString(level) + ".txt"; // Replace with your desired output file path
        String outputSelecFilePath = "epubfolder-selection-" + Integer.toString(level) + ".txt"; // Replace with your desired output file path
        int numOfSentences = 80;
        // the range of characters to search in the candidate sentences, initially all. 
        int endKey = 10;
        // new characters in a sentences appear too much, drop this sentence
        int leastAppearanceThreshold = 5;
        
        String result = PracticeCoverage.requestChinese("生字",Integer.toString(level));
        System.out.println("Concatenated Descriptions: " + result);
        Map<Integer, String> map = PracticeCoverage.countCharactersInFile(result, origSelectionFilePath);
        System.out.println("orignal frequency map:");
        map.forEach((count, characters) -> System.out.println(count + ": " + characters + " (" + characters.length() + " characters)"));
        Map<Integer, String> map2 = 
        	selectSentences(inputFilePath, result,map, outputSelecFilePath, numOfSentences, endKey, leastAppearanceThreshold); 
        map2.forEach((count, characters) -> System.out.println(count + ": " + characters + " (" + characters.length() + " characters)"));
    }

    public static void globalSelection(int level) throws IOException{
    	// File paths
        String inputFilePath = "candidate-selection-" + Integer.toString(level) + ".txt"; // Replace with your input file path
        String outputSelecFilePath = "global-selection-" + Integer.toString(level) + ".txt"; // Replace with your desired output file path
        int numOfSentences = 150;
        // the range of characters to search in the candidate sentences, initially all. 
        int endKey = 10;
        // new characters in a sentences appear too much, drop this sentence
        int leastAppearanceThreshold = 8;
        
        String chars = PracticeCoverage.requestChinese("生字","全部");
        System.out.println("Concatenated Descriptions: " + chars);
        String sentences = PracticeCoverage.requestChinese("练习","全部");
        System.out.println("Concatenated Descriptions: " + sentences);
        Map<Integer, String> map = PracticeCoverage.countCharactersInString(chars, sentences);
        System.out.println("orignal frequency map:");
        map.forEach((count, characters) -> System.out.println(count + ": " + characters + " (" + characters.length() + " characters)"));
        Map<Integer, String> map2 = 
        	selectSentences(inputFilePath, chars, map, outputSelecFilePath, numOfSentences, endKey, leastAppearanceThreshold); 
        map2.forEach((count, characters) -> System.out.println(count + ": " + characters + " (" + characters.length() + " characters)"));
    }

    
    public static void addSentencesRequests(String sentencFileName) {
        String urlString = "http://localhost:8080/math/add";
        int subcategoryDigit = Ranking.extractDigitFromFileName(sentencFileName);
        String subcategoryName = Ranking.numberToChinese(subcategoryDigit);

        List<String> sentences = new ArrayList<>();
        
        // Read sentences from the input file
        try (BufferedReader br = new BufferedReader(new FileReader(sentencFileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                sentences.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
            return; // Exit the function if there's an error reading the file
        }
        
        // Randomize the list of sentences
        Collections.shuffle(sentences);
        
        // Send POST requests for each sentence
        if (!sentences.isEmpty()) {
            for (String sentence : sentences) {
                String description = sentence; // Assuming the entire sentence is the description
                Ranking.sendPostRequest(urlString, "chinese", "练习",subcategoryName, description, null);
            }
        }
    }
    
    private static Map<Integer, String> selectSentences(
    		String inputRankingFileName, 
    		String newCharacters,
    		Map<Integer, String> charFrequencyMap, 
    		String outputSelectionFileName,
    		int numberOfSentences, 
    		int endKey, 
    		int leastAppearanceThreshold) {
        List<String> sentences = readSentencesFromFile(inputRankingFileName);
        if (sentences == null) {
            System.err.println("Failed to read sentences from the file.");
            return new HashMap<>();
        }

        // Map to track character appearance in final list
        Map<String, Integer> finalCharCount = new HashMap<>();
        List<String> finalSentences = new ArrayList<>();

        // Iterate through the map in ascending order of frequency
        List<Integer> frequencies = new ArrayList<>(charFrequencyMap.keySet());
        Collections.sort(frequencies);

        outerLoop:
        for (Integer frequency : frequencies) {
        	if (frequency > endKey) {
        		break;
        	}
            String characters = charFrequencyMap.get(frequency);
            for (char newChar : characters.toCharArray()) {
                if (finalSentences.size() >= numberOfSentences) {
                    break outerLoop;
                }

                // Use an iterator to safely remove elements from the sentences list
                Iterator<String> iterator = sentences.iterator();
                while (iterator.hasNext()) {
                    String sentence = iterator.next();
                    if (sentence.contains(String.valueOf(newChar)) ) {
                        // Find the least frequency of characters in this sentence in finalCharCount
                        int leastFrequency = Integer.MAX_VALUE;
                        StringBuilder localNewCharacters = new StringBuilder();
                        for (char c : sentence.toCharArray()) {
                            if (newCharacters.indexOf(c) >= 0) {
                                int count = finalCharCount.getOrDefault(String.valueOf(c), 0);
                                if (count < leastFrequency) {
                                    leastFrequency = count;
                                }
                                localNewCharacters.append(c);
                            }
                        }
//                        System.out.println(sentence);
//                        System.out.println("least" + leastFrequency);
//                        System.out.println(localNewCharacters);

                        // Decide to add this sentence based on the least frequency
                        if (leastFrequency < leastAppearanceThreshold) {
                            finalSentences.add(sentence);
                            iterator.remove(); // Remove the sentence from the list
                            for (char c : localNewCharacters.toString().toCharArray()) {
                                finalCharCount.put(String.valueOf(c), finalCharCount.getOrDefault(String.valueOf(c), 0) + 1);
                            }
                        }
                    }
                    if (finalSentences.size() >= numberOfSentences) {
                        break outerLoop;
                    }
                }
            }
        }

        // Write the final selected sentences to the output file
        writeSentencesToFile(finalSentences, outputSelectionFileName);
//        for (Map.Entry<String, Integer> entry : finalCharCount.entrySet()) {
//            System.out.println("Character: " + entry.getKey() + ", Count: " + entry.getValue());
//        }
        return convertMap(finalCharCount);
    }
    
    private static List<String> readSentencesFromFile(String filePath) {
        List<String> sentences = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sentences.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return sentences;
    }

    private static void writeSentencesToFile(List<String> sentences, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String sentence : sentences) {
                writer.write(sentence);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Map<Integer, String> convertMap(Map<String, Integer> originalMap) {
        Map<Integer, String> newMap = new TreeMap<>();

        for (Map.Entry<String, Integer> entry : originalMap.entrySet()) {
            int count = entry.getValue();
            String character = entry.getKey();

         // Compute the current string associated with this count, if it exists, otherwise use an empty string
            String currentString = newMap.getOrDefault(count, "");
            
            // Concatenate the character to the current string
            currentString += character;
            
            // Put the updated string back into the map
            newMap.put(count, currentString);
        }

        return newMap;
    }
}
