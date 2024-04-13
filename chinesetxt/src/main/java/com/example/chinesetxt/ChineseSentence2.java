package com.example.chinesetxt;

import java.io.*;
import java.util.*;

public class ChineseSentence2 {

    public static void main(String[] args) {
        String inputFile = "pfsj-clean.txt";
        String outputFile = "sorted_separated_sentences.txt";

        try {
            Map<Integer, List<String>> sentenceMap = extractAndStoreSentences(inputFile);
            writeSeparatedSentences(sentenceMap, outputFile);
            System.out.println("Sentences have been sorted by length and written to " + outputFile);
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Map<Integer, List<String>> extractAndStoreSentences(String inputFile) throws IOException {
        Map<Integer, List<String>> sentenceMap = new TreeMap<>();
        try (FileReader fileReader = new FileReader(inputFile)) {
            StringBuilder sentenceBuilder = new StringBuilder();
            int c;
            while ((c = fileReader.read()) != -1) {
                char character = (char) c;
                if (c == '\u3000' || c == ' ' || c == '“' || c == '”') // Skip Chinese space, standard space, and quotation marks
                    continue;
                sentenceBuilder.append(character);
                if (character == '。' || character == '？' || character == '！') { // Chinese period marks the end of a sentence
                    String sentence = sentenceBuilder.toString();
                    int length = sentence.length();
                    sentenceMap.computeIfAbsent(length, k -> new ArrayList<>()).add(sentence);
                    sentenceBuilder.setLength(0); // Clear the StringBuilder for the next sentence
                }
            }
        }
        return sentenceMap;
    }

    public static void writeSeparatedSentences(Map<Integer, List<String>> sentenceMap, String outputFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (Map.Entry<Integer, List<String>> entry : sentenceMap.entrySet()) {
                Integer length = entry.getKey();
                List<String> sentences = entry.getValue();
                writer.write(length.toString()); // Write length of sentences
                writer.newLine();
                for (String sentence : sentences) {
                    writer.write(sentence.trim()); // Remove leading and trailing whitespace
                    writer.newLine();
                }
            }
        }
    }
}
