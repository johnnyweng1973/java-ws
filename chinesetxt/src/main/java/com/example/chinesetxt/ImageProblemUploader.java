package com.example.chinesetxt;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class ImageProblemUploader {
    public static void main(String[] args) {
        //download();
        upload();
    }

    public static void download() {
        String url = "http://localhost:8080/math/general/data";
        String subject = "math";
        String category = "pascal9";
        String subcategory = "2005A";

        // Send the post request
        PracticeCoverage.sendGetRequest(url, subject, category, subcategory);
    }

    public static void upload() {
        String url = "http://localhost:8080/math/add";
        String subject = "math";
        String category = "pascal9";
        String basePath = "math/pascal/problems";

        File baseDir = new File(basePath);
        if (baseDir.exists() && baseDir.isDirectory()) {
            File[] yearDirs = baseDir.listFiles(File::isDirectory);
            if (yearDirs != null) {
                for (File yearDir : yearDirs) {
                    String subcategoryName = yearDir.getName();
                    File[] imageFiles = yearDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
                    if (imageFiles != null) {
                        // Sort the imageFiles array based on the integer value extracted from the file names
                        Arrays.sort(imageFiles, Comparator.comparingInt(file -> {
                            String fileName = file.getName().replaceAll("[^0-9]", "");
                            return fileName.isEmpty() ? 0 : Integer.parseInt(fileName);
                        }));

                        for (File imageFile : imageFiles) {
                            // Extract the number from the image file name and use it as the description
                            String fileName = imageFile.getName();
                            String description = fileName.replaceAll("[^0-9]", "");

                            // Append A, B, or C based on the value of description
                            int descValue = description.isEmpty() ? 0 : Integer.parseInt(description);
                            String subcategorySuffix;
                            if (descValue <= 10) {
                                subcategorySuffix = "A";
                            } else if (descValue <= 20) {
                                subcategorySuffix = "B";
                            } else {
                                subcategorySuffix = "C";
                            }
                            String finalSubcategoryName = subcategoryName + subcategorySuffix;

                            // Print the parameters in one line (excluding URL and image path)
                            System.out.println("Subject: " + subject + ", Category: " + category + ", Subcategory: " + finalSubcategoryName + ", Description: " + description);

                            // Send the post request
                            Ranking.sendPostRequest(url, subject, category, finalSubcategoryName, description, imageFile);
                        }
                    }
                }
            }
        } else {
            System.err.println("Base path does not exist or is not a directory: " + basePath);
        }
    }
}
