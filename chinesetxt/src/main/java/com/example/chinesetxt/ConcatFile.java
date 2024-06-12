package com.example.chinesetxt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConcatFile {

    public static void main(String[] args) {
        String[] sentencesFiles = {"jy-sentences.txt", "zyj-sentences.txt", "pfsj-sentences.txt"};
        String outputFile = "chinese-sentences.txt";

        try {
            concatenateFiles(sentencesFiles, outputFile);
            System.out.println("Files have been concatenated and written to " + outputFile);
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void concatenateFiles(String[] sentencesFiles, String outputFile) throws IOException {
        StringBuilder combinedSentences = new StringBuilder();
        for (String file : sentencesFiles) {
            combinedSentences.append(new String(Files.readAllBytes(Paths.get(file))));
            combinedSentences.append(System.lineSeparator());
        }
        Files.write(Paths.get(outputFile), combinedSentences.toString().getBytes());
    }
}
