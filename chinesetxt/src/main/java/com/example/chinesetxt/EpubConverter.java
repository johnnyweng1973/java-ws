package com.example.chinesetxt;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class EpubConverter {

    public static void convertEpubToTxt(String epubFilePath, String txtFilePath) {
        try {
            // Read EPUB file
            FileInputStream epubInputStream = new FileInputStream(epubFilePath);

            // Parse EPUB
            Book book = (new EpubReader()).readEpub(epubInputStream);

            // Extract text content
            StringBuilder textContent = new StringBuilder();
            book.getContents().forEach(content -> {
                try {
                    InputStream inputStream = content.getInputStream();
                    InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                    char[] buffer = new char[1024];
                    int length;
                    while ((length = reader.read(buffer)) != -1) {
                        textContent.append(buffer, 0, length);
                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // Write text content to a TXT file
            FileWriter writer = new FileWriter(txtFilePath);
            writer.write(textContent.toString());
            writer.close();

            System.out.println("Conversion successful. Output saved to " + txtFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        
        String epubFilePath = "zyj.epub";
        String txtFilePath = "zyj.txt";

        convertEpubToTxt(epubFilePath, txtFilePath);
    }
}
