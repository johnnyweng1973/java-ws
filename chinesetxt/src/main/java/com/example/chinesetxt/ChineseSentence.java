package com.example.chinesetxt;
import java.io.*;
import java.util.*;

public class ChineseSentence {

    public static void main(String[] args) {
        String inputFile = "zyj-clean.txt";
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
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> sentences = extractSentencesFromLine(line);
                for (String sentence : sentences) {
                    int length = sentence.length();
                    if (!isSentenceDuplicate(sentenceMap, length, sentence)) {
                        sentenceMap.computeIfAbsent(length, k -> new ArrayList<>()).add(sentence);
                    }
                }
            }
        }
        return sentenceMap;
    }

    public static List<String> extractSentencesFromLine(String line) {
        List<String> sentences = new ArrayList<>();
        StringBuilder sentenceBuilder = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == '\u3000' || character == ' ' || character == '“' || character == '”') // Skip Chinese space, standard space, and quotation marks
                continue;
            if (character == '。' || character == '？' || character == '！') { // Chinese period marks the end of a sentence
                sentences.add(sentenceBuilder.toString());
                sentenceBuilder.setLength(0); // Clear the StringBuilder for the next sentence
            } else {
                sentenceBuilder.append(character);
            }
        }
        if (sentenceBuilder.length() > 0) { // Add the remaining text as a sentence if any
            sentences.add(sentenceBuilder.toString());
        }
        return sentences;
    }

    public static boolean isSentenceDuplicate(Map<Integer, List<String>> sentenceMap, int length, String sentence) {
        List<String> existingSentences = sentenceMap.get(length);
        if (existingSentences != null) {
            for (String existingSentence : existingSentences) {
                if (existingSentence.equals(sentence)) {
                    return true; // Sentence already exists
                }
            }
        }
        return false; // Sentence is not a duplicate
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
