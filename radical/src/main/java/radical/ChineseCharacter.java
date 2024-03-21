package radical;

public class ChineseCharacter {
    private final String character;
    private final String href;

    ChineseCharacter(String character, String href) {
        this.character = character;
        this.href = href;
    }

    public String getCharacter() {
        return character;
    }
    
    @Override
    public String toString() {
    	return character;
    }
}
