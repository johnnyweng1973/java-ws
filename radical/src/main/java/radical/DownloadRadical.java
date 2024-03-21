package radical;

import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.*;

public class DownloadRadical {

    public static void main(String[] args) {
    	 // URL of the HTML page
        String url = "https://mzd.diyifanwen.com/zidian/bs/";

    	HanziRadicalCollection collection = new HanziRadicalCollection();
         downloadCollection(collection, url);
         
    }
    public static Map<Integer, List<ChineseCharacter>> downloadCharacterMap(String strokeUrl) {
        Map<Integer, List<ChineseCharacter>> characterMap = new HashMap<>();

        try {
            Document doc = Jsoup.connect("https:" + strokeUrl).get();
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
                        characterList.add(new ChineseCharacter(character, href));
                    }
                    nextSibling = nextSibling.nextElementSibling();
                }
                System.out.println("   " + "笔画 " + strokeNumber + ": " + characterList.toString());
                characterMap.put(strokeNumber, characterList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return characterMap;
    }

    private static void downloadCollection( HanziRadicalCollection collection, String url) {
       
        try {
            // Connect to the URL and parse the HTML document
            Document doc = Jsoup.connect(url).get();

            // Select all <dl> elements
            Elements dlElements = doc.select("dl");

           // Iterate over each <dl> element
            for (Element dl : dlElements) {
                // Find the stroke number within the <dt> element
                Element dt = dl.selectFirst("dt");
                int strokeNumber = ChineseNumberConverter.convert(dt.text(), "部首笔画数");
                System.out.println("stroke number " + strokeNumber );
                
                // Find all <dd> elements containing radicals
                Elements ddElements = dl.select("dd");

                // Initialize a list to store radicals for this stroke number
                List<Radical> radicalList = new ArrayList<>();

                // Iterate over each <dd> element to extract radicals
                for (Element dd : ddElements) {
                    // Extract the radical from the <a> tag
                    String radicalName = dd.selectFirst("a").text();
                    // Extract the URL from the <a> tag
                    String radicalUrl = dd.selectFirst("a").attr("href");
                    System.out.println(radicalName);
                    Map<Integer, List<ChineseCharacter>> map = downloadCharacterMap(radicalUrl);
                    Radical radical = new Radical(radicalName, map);
                    radicalList.add(radical);
                }

                // Add the list of radicals to the map
                collection.addListOfRadicalByStrokeNum(strokeNumber, radicalList);
            }

            System.out.println("map " + collection.toString());
           
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
