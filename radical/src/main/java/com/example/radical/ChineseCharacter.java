package com.example.radical;
public class ChineseCharacter {
    private String radical;
    private String name;
    private String pinyin;
    private int strokeNum;
    

    public ChineseCharacter() {
        // Default constructor
    }

    // Getter methods
    public String getRadical() {
        return radical;
    }

    public String getName() {
        return name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public int getStrokeNum() {
        return strokeNum;
    }

    // Setter methods
    public void setRadical(String radical) {
        this.radical = radical;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public void setStrokeNum(int strokeNum) {
        this.strokeNum = strokeNum;
    }
    
    @Override
    public String toString() {
        return name + " " + radical + " " + pinyin + " " + strokeNum + "画";
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
