package com.example.radical;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class DownloadRadical {
	private static final Map<String, ChineseCharacter> characterWholeMap = new HashMap<>();
    private static final StringBuilder stringBuilder = new StringBuilder();
	private static final String jsonFile = "中文字库.txt";
	private static int numOfCharacters = 0;
	
	public static void writeToJsonFile() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            objectMapper.writeValue(new File(jsonFile), characterWholeMap);
            System.out.println("Data has been written to JSON file successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public static Map<Integer, List<ChineseCharacter>> downloadCharacterMap(String strokeUrl) {
		Map<Integer, List<ChineseCharacter>> characterMap = new HashMap<>();

		try {
			  byte[] body = Jsoup.connect(strokeUrl).execute().bodyAsBytes();

	 		    ByteArrayInputStream in = new ByteArrayInputStream(body);
	 
				Document doc = Jsoup.parse(in, "GBK", strokeUrl);

//			Connection conn = Jsoup.connect(strokeUrl);
//			byte[] body = conn.execute().bodyAsBytes();
//		    ByteArrayInputStream in = new ByteArrayInputStream(body);
//        	Document doc = Jsoup.parse(in, "GBK", strokeUrl);
		            
			Elements dtElements = doc.select("dt.bsbhTitle");

			for (int i = 0; i < dtElements.size(); i++) {
			 	Element dtElement = dtElements.get(i);
				int strokeNumber = ChineseNumberConverter.convert(dtElement.text(), "笔画数");
             
				List<ChineseCharacter> characterList = new ArrayList<>();
				Element nextSibling = dtElement.nextElementSibling();
				while (nextSibling != null && nextSibling.tagName().equals("dd")) {
					Element aElement = nextSibling.selectFirst("a");
					if (aElement != null) {
						String character = aElement.text();
						String title = aElement.attr("title");
					        
				        // Extract 拼音, 部首, and 笔画 from the title
				        String pinyin = extractValue(title, "拼音：", ",\n");
				        String radical = extractValue(title, "部首：", "\n");
				        int strokeNum = Integer.parseInt(extractValue(title, "笔画：", ""));
				        
				        // Create a ChineseCharacter instance and set the values
				        ChineseCharacter characterInstance = new ChineseCharacter();
				        characterInstance.setName(character);
				        characterInstance.setPinyin(pinyin);
				        characterInstance.setRadical(radical);
				        characterInstance.setStrokeNum(strokeNum);
				        // Print the values to verify
				        System.out.println("拼音: " + characterInstance.getPinyin());
				        System.out.println("部首: " + characterInstance.getRadical());
				        System.out.println("笔画: " + characterInstance.getStrokeNum());
				  
				        
					
						characterList.add(characterInstance);
						characterWholeMap.put(character, characterInstance);
					}
					nextSibling = nextSibling.nextElementSibling();
				}
				String str = "   " + "笔画 " + strokeNumber + ": " + characterList.toString() + "\r\n";
				System.out.println(str);
				stringBuilder.append(str);
				characterMap.put(strokeNumber, characterList);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return characterMap;
	}
	private static String extractValue(String text, String prefix, String suffix) {
        int startIndex = text.indexOf(prefix);
        if (startIndex == -1) {
            return "";
        }
        startIndex += prefix.length();
        
        int endIndex = suffix.isEmpty() ? text.length() : text.indexOf(suffix, startIndex);
        if (endIndex == -1) {
            endIndex = text.length();
        }
        
        return text.substring(startIndex, endIndex).trim();
    }

	private static void downloadCollection(HanziRadicalCollection collection, String url) {

		try {
			// Connect to the URL and parse the HTML document
			//Document doc = Jsoup.connect(url).charset("GBK").get();
//			Connection conn = Jsoup.connect(url);
//			byte[] body = conn.execute().bodyAsBytes();
//		    ByteArrayInputStream in = new ByteArrayInputStream(body);
			 // Connect to the URL and retrieve the HTML content as a byte array
            byte[] body = Jsoup.connect(url).execute().bodyAsBytes();

 		    ByteArrayInputStream in = new ByteArrayInputStream(body);
 
			Document doc = Jsoup.parse(in, "GBK", url);
		
			// Select all <dl> elements
			Elements dlElements = doc.select("dl");
        
			// Iterate over each <dl> element
			for (int i = 0; i < dlElements.size(); i++) {
			    Element dl = dlElements.get(i);
			    // Find the stroke number within the <dt> element
				Element dt = dl.selectFirst("dt");
				int strokeNumber = ChineseNumberConverter.convert(dt.text(), "部首笔画数");
				String str = "stroke number " + strokeNumber + "\r\n";
				System.out.println(str);
				stringBuilder.append(str);

				// Find all <dd> elements containing radicals
				Elements ddElements = dl.select("dd");

				// Initialize a list to store radicals for this stroke number
				List<Radical> radicalList = new ArrayList<>();

				// Iterate over each <dd> element to extract radicals
				for (Element dd : ddElements) {
					// Extract the radical from the <a> tag
					String radicalName = dd.selectFirst("a").text();
					// Extract the URL from the <a> tag
					String radicalUrl = "https:" + dd.selectFirst("a").attr("href");
					System.out.println(radicalName);
					stringBuilder.append(radicalName);
					Map<Integer, List<ChineseCharacter>> map = downloadCharacterMap(radicalUrl);
					Radical radical = new Radical(radicalName, map);
					radicalList.add(radical);
				}

				// Add the list of radicals to the map
				collection.addListOfRadicalByStrokeNum(strokeNumber, radicalList);
			}
            
			System.out.println("total num " + numOfCharacters);
			saveStringToFile();
			writeToJsonFile();
			

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public static void main(String[] args) {
		// URL of the HTML page
		String url = "https://mzd.diyifanwen.com/zidian/bs/";

		HanziRadicalCollection collection = new HanziRadicalCollection();
		downloadCollection(collection, url);

	}


}
