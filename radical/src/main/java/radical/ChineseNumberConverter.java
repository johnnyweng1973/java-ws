package radical;
import java.util.HashMap;
import java.util.Map;
public class ChineseNumberConverter {
	public static int convert(String chineseNumber, String prefix) {
        if (chineseNumber == null || chineseNumber.isEmpty()) {
            throw new IllegalArgumentException("Input Chinese number is null or empty");
        }
        // Remove the prefix "笔画数" if it exists
        if (chineseNumber.startsWith(prefix)) {
            chineseNumber = chineseNumber.substring(prefix.length());
        }

        int result = 0;
        int temp = 0; // Temporary variable to store the value of the current character
        boolean isMidTen = false; // '十' in the middle

        for (int i = 0; i < chineseNumber.length(); i++) {
            char c = chineseNumber.charAt(i);
            switch (c) {
                case '一':
                    temp = 1;
                    break;
                case '二':
                    temp = 2;
                    break;
                case '三':
                    temp = 3;
                    break;
                case '四':
                    temp = 4;
                    break;
                case '五':
                    temp = 5;
                    break;
                case '六':
                    temp = 6;
                    break;
                case '七':
                    temp = 7;
                    break;
                case '八':
                    temp = 8;
                    break;
                case '九':
                    temp = 9;
                    break;
                case '十':
                    if (i == 0) {
                        temp = 10; // If '十' is the first character, treat it as 10
                    } else {
                        isMidTen = true;
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Invalid Chinese numeral character: " + c);
            }

            // Add the value of the current character to the result
            if (!isMidTen) {
                result += temp;
            } else {
                // If the previous character was '十', add the current value to the previous result
                result *= 10;
                isMidTen = false; // Reset the flag
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String chineseNumber = "十五"; // Example Chinese number
        System.out.println("Converted Chinese number to integer: " );
    }
}
