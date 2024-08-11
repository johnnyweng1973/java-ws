package com.example.chinesetxt;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PDFSplitter {
    public static void main(String[] args) {
        String inputPdfPath = "PascalCombinedContest.pdf";
        String outputDirectory = "pascal/pdf/";

        // Ensure the output directory exists
        new File(outputDirectory).mkdirs();

        try (PDDocument document = Loader.loadPDF(new File(inputPdfPath))) {
            int totalNumberOfPages = document.getNumberOfPages();
            int fileIndex = 2024;

            PDFTextStripper textStripper = new PDFTextStripper();
            Pattern pascalPattern = Pattern.compile("\\bPascal Contest\\b", Pattern.CASE_INSENSITIVE);
            Pattern gradePattern = Pattern.compile("\\bGrade 9\\b", Pattern.CASE_INSENSITIVE);

            int startPage = 0;

            for (int i = 0; i < totalNumberOfPages; i++) {
                // Extract text from the current page
                textStripper.setStartPage(i + 1);
                textStripper.setEndPage(i + 1);
                String pageText = textStripper.getText(document);

                // Use regex to find the exact phrases "Pascal Contest" and "Grade 9"
                Matcher pascalMatcher = pascalPattern.matcher(pageText);
                Matcher gradeMatcher = gradePattern.matcher(pageText);
                
                if (pascalMatcher.find() && gradeMatcher.find()) {
                    if (i != startPage) {
                        try (PDDocument newDocument = new PDDocument()) {
                            for (int j = startPage; j < i; j++) {
                                newDocument.addPage(document.getPage(j));
                            }
                            String outputFilePath = outputDirectory + fileIndex + "PascalContest.pdf";
                            newDocument.save(outputFilePath);
                            System.out.println("Saved " + outputFilePath + " with pages from " + (startPage + 1) + " to " + i);
                        }
                        fileIndex--;
                    }
                    startPage = i;
                }
            }

            // Save the last section
            try (PDDocument newDocument = new PDDocument()) {
                for (int j = startPage; j < totalNumberOfPages; j++) {
                    newDocument.addPage(document.getPage(j));
                }
                String outputFilePath = outputDirectory + fileIndex + "PascalContest.pdf";
                newDocument.save(outputFilePath);
                System.out.println("Saved " + outputFilePath + " with pages from " + (startPage + 1) + " to " + totalNumberOfPages);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
