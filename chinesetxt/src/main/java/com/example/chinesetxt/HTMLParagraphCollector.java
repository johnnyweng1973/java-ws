package com.example.chinesetxt;
import java.io.*;

public class HTMLParagraphCollector {
    
    public static void main(String[] args) {
        String inputFile = "pfsj.txt";
        String outputFile = "pfsj-clean.txt";
        
        try {
            collectParagraphs(inputFile, outputFile);
            System.out.println("Paragraphs collected and written to output file successfully.");
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void collectParagraphs(String inputFile, String outputFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        
        StringBuilder htmlContent = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            htmlContent.append(line);
        }
        
        String[] paragraphs = getParagraphsFromHTML(htmlContent.toString());
        for (String paragraph : paragraphs) {
            writer.write(paragraph.trim());
            writer.newLine(); // Separate paragraphs with a new line
        }
        
        reader.close();
        writer.close();
    }
    
    public static String[] getParagraphsFromHTML(String htmlContent) {
        // Use regular expression to extract content within <p> tags
        String[] paragraphs = htmlContent.split("<p[^>]*>");
        String[] result = new String[paragraphs.length - 1]; // Skip first empty element
        for (int i = 1; i < paragraphs.length; i++) {
            result[i - 1] = getTextWithinTags(paragraphs[i]);
        }
        return result;
    }
    
    public static String getTextWithinTags(String line) {
        return line.replaceAll("<[^>]*>", ""); // Remove HTML tags
    }
}
