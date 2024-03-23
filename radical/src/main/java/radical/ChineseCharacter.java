package radical;

public class ChineseCharacter {
    private final String character;
    private final String href;
    private final String hexAscii;
    private final String rawBytes;
    
    ChineseCharacter(String character, String href) {
        this.character = character;
        this.href = href;
        this.hexAscii = "0x" + toHexAscii(character);
        this.rawBytes = getRawBytes(character);
    }
    
    public String getCharacter() {
        return character;
    }
    
    @Override
    public String toString() {
    	return character + " " + hexAscii + " " + rawBytes;
    }
    
    // Method to convert Unicode character to ASCII representation in hexadecimal format
    private String toHexAscii(String character) {
        StringBuilder sb = new StringBuilder();
        for (char c : character.toCharArray()) {
            sb.append(String.format("%04X", (int) c)); // Convert each character to its Unicode code point in hexadecimal
        }
        return sb.toString();
    }
    
    // Method to get the raw bytes of the character in the platform's default encoding
    private String getRawBytes(String character) {
        byte[] bytes = character.getBytes();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b)); // Convert each byte to its hexadecimal representation
        }
        return sb.toString();
    }
}
