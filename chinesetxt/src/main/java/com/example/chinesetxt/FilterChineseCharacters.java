package com.example.chinesetxt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class FilterChineseCharacters {
    public static void main(String[] args) {
        String chineseCharacters = PracticeCoverage.requestChinese("生字", "全部");

        // Convert the chineseCharacters to a Set for faster lookup
        Set<Character> chineseSet = new HashSet<>();
        for (char ch : chineseCharacters.toCharArray()) {
            chineseSet.add(ch);
        }
        
        // Set to store the unique Chinese characters not in chineseCharacters
        Set<Character> uniqueCharacters = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("明天会更好.txt"))) {
            String line;
            System.out.println("Characters not in chineseCharacters:");

            while ((line = reader.readLine()) != null) {
                for (char ch : line.toCharArray()) {
                    if (Character.isIdeographic(ch) && !chineseSet.contains(ch)) {
                    	uniqueCharacters.add(ch);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        for (Character c: uniqueCharacters) {
        	System.out.println(c);
        }
    }
}
