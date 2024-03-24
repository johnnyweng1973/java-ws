package com.example.radical;
public class ChineseCharacter {
    private String radical;
    private String name;

    public ChineseCharacter() {
        // Default constructor
    }

    public ChineseCharacter(String name, String radical) {
        this.name = name;
        this.radical = radical;
    }

    public String getRadical() {
        return radical;
    }

    public void setRadical(String radical) {
        this.radical = radical;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name + " " + radical;
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
