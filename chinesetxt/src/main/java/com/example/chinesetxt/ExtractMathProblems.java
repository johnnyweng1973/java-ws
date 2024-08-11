package com.example.chinesetxt;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractMathProblems {

	public static void main(String[] args) {
        String pdfDirPath = "math/pascal/pdf";
        String outputDir = "math/pascal/problems";

        File pdfDir = new File(pdfDirPath);

        if (pdfDir.isDirectory()) {
            File[] pdfFiles = pdfDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
            if (pdfFiles != null) {
                for (File pdfFile : pdfFiles) {
                    try {
                        extractMathProblems(pdfFile.getAbsolutePath(), outputDir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                System.out.println("No PDF files found in the directory.");
            }
        } else {
            System.out.println("The specified path is not a directory.");
        }
    }
   

    public static void extractMathProblems(String pdfPath, String outputDir) throws IOException {
        File file = new File(pdfPath);
        String fileName = file.getName();
        // Extract the year from the filename
        String year = fileName.replaceAll("[^0-9]", "");
        if (year.length() != 4) {
            System.out.println("Invalid filename format: " + fileName);
            return;
        }

        File specificOutputDir = new File(outputDir, year);
        if (!specificOutputDir.exists()) {
            specificOutputDir.mkdirs();
        }

        
        try (PDDocument document = Loader.loadPDF(file)) {
            // Remove the first page
            if (document.getNumberOfPages() > 1) {
                document.removePage(0);
            }

            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int problemIndex = 1;
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                int pageIndex = i + 1; // Adjust for the removed first page

                BoundingBoxFinder finder = new BoundingBoxFinder();
                finder.setStartPage(pageIndex);
                finder.setEndPage(pageIndex);
                finder.getText(document); // This triggers the text extraction and bounding box calculation

                List<Rectangle> boundingBoxes = finder.getBoundingBoxes();

                BufferedImage pageImage = pdfRenderer.renderImageWithDPI(i, 300);

                for (Rectangle bbox : boundingBoxes) {
                    int dpi = 300;
                    float scalingFactor = dpi / 72.0f;

                    int x = (int) (bbox.x * scalingFactor);
                    int y = (int) (bbox.y * scalingFactor) -30;
                    int width = (int) (bbox.width * scalingFactor) + 30;
                    int height = (int) (bbox.height * scalingFactor)+30;

                    x = Math.max(x, 0);
                    y = Math.max(y, 0);
                    width = Math.min(width, pageImage.getWidth() - x);
                    height = Math.min(height, pageImage.getHeight() - y);

                    BufferedImage croppedImage = pageImage.getSubimage(x, y, width, height);
                    saveImage(specificOutputDir.getAbsolutePath(), pageIndex, problemIndex, croppedImage);
                    problemIndex++;
                }
            }
        }
    }

    private static void saveImage(String outputDir, int pageIndex, int problemIndex, BufferedImage image) throws IOException {
        Files.createDirectories(Path.of(outputDir));
        String fileName = String.format("%s/%d.png", outputDir, problemIndex);
        File outputfile = new File(fileName);
        ImageIO.write(image, "png", outputfile);
        System.out.println("Saved image for page " + pageIndex + " problem " + problemIndex + " at " + outputfile.getAbsolutePath());
    }

    private static class BoundingBoxFinder extends PDFTextStripper {
        private final Pattern problemPattern = Pattern.compile("\\d\\.");
        private final List<Rectangle> boundingBoxes = new ArrayList<>();
        private float currentY = -1;
        private float maxY = -1;
        private boolean isFirstProblem = true;
        private boolean isFirstLine = false;
        private float currentFirstLineX = -1;
        private static final boolean DEBUG = false;

        public BoundingBoxFinder() throws IOException {
            super();
        }

        @Override
        protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
            if (textPositions.isEmpty()) return;
            TextPosition firstPosition = textPositions.get(0);
            
            if (DEBUG && firstPosition.getXDirAdj() < 75) {
                logTextPositionDetails(text, textPositions);
            }
            
            if (firstPosition.getXDirAdj() <= 75 && problemPattern.matcher(text).find()) {
            	float minY = getMinY(textPositions);
                System.out.println("found " + text);
            	if (!isFirstProblem) {
                    boundingBoxes.add(new Rectangle(
                    		72, 
                    		(int) currentY -10,
                    		450,
                    		(int) (minY - currentY - 10)));
                }
                currentY = minY;
                isFirstProblem = false;
                isFirstLine = true;
            }
            
            if (isFirstLine) {
                TextPosition lastElement = textPositions.get(textPositions.size() - 1);
            	if (currentFirstLineX == -1) {
            		currentFirstLineX = lastElement.getXDirAdj();	
            	}
            	else {
            		if (currentFirstLineX < lastElement.getXDirAdj()) {
            			//calculate the MinY for the first line of the problem
            			currentFirstLineX = lastElement.getXDirAdj();
            			currentY = Math.min(currentY, getMinY(textPositions));
            		}
            		else {
            			isFirstLine = false;
            			currentFirstLineX = -1;
            		}
            	}
            }
            
            maxY = Math.max(maxY,firstPosition.getYDirAdj());
        }

        public List<Rectangle> getBoundingBoxes() {
            if (!isFirstProblem) {
                boundingBoxes.add(new Rectangle(72, (int) currentY - 10, 450, (int) (maxY - currentY + 30)));
            }
            return boundingBoxes;
        }
        
        private float getMinY(List<TextPosition> textPositions) {
        	float minY = Float.MAX_VALUE; 
        	for (TextPosition textPosition : textPositions) {
        		minY = Math.min(minY, textPosition.getYDirAdj());
        	}
        	return minY;
        }
        private void logTextPositionDetails(String text, List<TextPosition> textPositions) {
            System.out.println("Text: " + text);
            for (TextPosition position : textPositions) {
                System.out.println("TextPosition: x=" + position.getXDirAdj() + ", y=" + position.getYDirAdj()
                    + ", width=" + position.getWidthDirAdj() + ", height=" + position.getHeightDir() + ", font="
                    + position.getFont().getName() + ", fontSize=" + position.getFontSize());
            }
        }

    }

}
