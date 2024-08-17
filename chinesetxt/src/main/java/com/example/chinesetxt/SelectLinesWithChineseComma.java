package com.example.chinesetxt;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SelectLinesWithChineseComma {

    public static void main(String[] args) {
        String inputFilePath = "delta-main-ranking-17.txt"; // Replace with your input file path
        String outputFilePath = "selection-17-comma.txt"; // Replace with your output file path

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("，")) { // Check if the line contains the Chinese comma
                    writer.write(line); // Write the line to the output file
                    writer.newLine(); // Add a new line after each line
                }
            }

            System.out.println("Lines containing '，' have been written to output.txt");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
