package com.example.chinesetxt;

import java.io.*;

public class FileSplitter {

    public static void splitFile(String inputFile, int linesPerFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            int count = 0;
            int fileIndex = 1;
            BufferedWriter writer = null;

            String baseName = inputFile.substring(0, inputFile.lastIndexOf('.'));
            String extension = inputFile.substring(inputFile.lastIndexOf('.'));

            while ((line = reader.readLine()) != null) {
                if (count % linesPerFile == 0) {
                    if (writer != null) {
                        writer.close();
                    }
                    String outputFileName = baseName + "_" + fileIndex + extension;
                    writer = new BufferedWriter(new FileWriter(outputFileName));
                    fileIndex++;
                }
                writer.write(line);
                writer.newLine();
                count++;
            }
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String inputFile = "jy-clean.txt";
        int linesPerFile = 22705;  // Specify the number of lines per file
        splitFile(inputFile, linesPerFile);
    }
}
