package radical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Radical {
    private final String name;
    private final Map<Integer, List<ChineseCharacter>> characterListMap;

    Radical(String name, Map<Integer, List<ChineseCharacter>> characterListMap) {
        this.name = name;
        this.characterListMap = characterListMap;
    }

    public void addCharacter(int strokeCount, ChineseCharacter character) {
    	characterListMap.computeIfAbsent(strokeCount, k -> new ArrayList<>()).add(character);
    }

    public void addListOfCharacterByStrokeNum(int strokeNumber, List<ChineseCharacter> characterList) {
    	characterListMap.put(strokeNumber, characterList); 
    }

    public String getName() {
        return name;
    }

    public List<String> getCharacters() {
        List<String> characters = new ArrayList<>();
        for (List<ChineseCharacter> characterList : characterListMap.values()) {
            for (ChineseCharacter character : characterList) {
                characters.add(character.getCharacter());
            }
        }
        return characters;
    }

    public int getStrokeCount() {
        return characterListMap.keySet().stream().findFirst().orElse(0);
    }
}
