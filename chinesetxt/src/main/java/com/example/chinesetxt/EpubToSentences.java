package com.example.chinesetxt;
import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.Collectors;

public class EpubToSentences {

    public static void main(String[] args) {
        // Get the path to the resources directory
        String epubDirectory = "epub";
        System.out.println("All sentences have been processed and written to" + epubDirectory);

        // Temporary files for intermediate steps
        String tmpFilePath = "tmp.txt";
        String tmpFilePath2 = "tmp2.txt";

        // Output file in resources directory
        String outputFilePath = "sentences.txt";


        if (epubDirectory == null) {
            System.err.println("Failed to locate resources. Check if the paths are correct.");
            return;
        }

        try {
            // Get all EPUB files in the specified directory
            List<Path> epubFiles = getEpubFiles(epubDirectory);

            // Writer for the final combined output file
            BufferedWriter finalWriter = new BufferedWriter(new FileWriter(outputFilePath));
            Map<Integer, List<String>> sentenceMap = new TreeMap<>();

            for (Path epubFilePath : epubFiles) {
                // Convert EPUB to text
                EpubConverter.convertEpubToTxt(epubFilePath.toString(), tmpFilePath);
                // Collect paragraphs
                HTMLParagraphCollector.collectParagraphs(tmpFilePath, tmpFilePath2);
                // Extract and store sentences by length
                ChineseSentence.extractAndStoreSentences(tmpFilePath2, sentenceMap);
            }

            // Write sentences to the final output file
            writeSentencesToFinalOutput(sentenceMap, finalWriter);

            finalWriter.close();
            System.out.println("All sentences have been processed and written to " + outputFilePath);
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getResourcePath(String relativePath) {
        URL resourceUrl = EpubToSentences.class.getClassLoader().getResource(relativePath);
        if (resourceUrl != null) {
            return resourceUrl.getPath();
        } else {
            return null;
        }
    }

    private static List<Path> getEpubFiles(String directoryPath) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            return paths.filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".epub"))
                        .collect(Collectors.toList());
        }
    }

    private static void writeSentencesToFinalOutput(Map<Integer, List<String>> sentenceMap, BufferedWriter finalWriter) throws IOException {
        for (Map.Entry<Integer, List<String>> entry : sentenceMap.entrySet()) {
            for (String sentence : entry.getValue()) {
                finalWriter.write(sentence);
                finalWriter.newLine();
            }
        }
    }
}
