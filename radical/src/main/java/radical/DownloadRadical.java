package radical;

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

public class DownloadRadical {
	private static final StringBuilder stringBuilder = new StringBuilder();
	private static final String fileName = "radical.txt";
	private static int numOfCharacters = 0;

	public static void saveStringToFile() {
		Path path = Paths.get(fileName);

		// Try block to check for exceptions
		try {
			// Now calling Files.writeString() method
			// with path , content & standard charsets
			Files.writeString(path, stringBuilder.toString(), StandardCharsets.UTF_8);
		}

		// Catch block to handle the exception
		catch (IOException ex) {
			// Print messqage exception occurred as
			// invalid. directory local path is passed
			System.out.print("Invalid Path");
		}
	}

	public static void main(String[] args) {
		// URL of the HTML page
		String url = "https://mzd.diyifanwen.com/zidian/bs/";

		HanziRadicalCollection collection = new HanziRadicalCollection();
		downloadCollection(collection, url);

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
						String href = aElement.attr("href");
						String rawHtml = aElement.outerHtml();
						numOfCharacters++;
						characterList.add(new ChineseCharacter(character, href));
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
			for (Element dl : dlElements) {
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
			

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
