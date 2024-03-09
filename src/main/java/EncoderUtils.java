public class EncoderUtils {
    public static boolean removeExtraChar(String[] nestedMessageFields) {
        for (int i = 0; i < nestedMessageFields.length; i++) {
            String field = nestedMessageFields[i]; // Accessible by index

            if (field.length() >= 2 && field.charAt(0) == '"' && field.charAt(field.length() - 1) == '"') {
                nestedMessageFields[i] = field.substring(1, field.length() - 1); // Modify original
            } else {
                return false;
            }
        }
        return true;
    }

    public static String removeBraces(String text) {
        // Check if the string starts with '{' and ends with '}' for nested message
        if (text != null && text.length() >= 2 && text.charAt(0) == '{' && text.charAt(text.length() - 1) == '}') {
            // Remove the first and last characters using substring
            return text.substring(1, text.length() - 1);
        } else {
            // Return the empty string if it doesn't match the criteria
            return "";
        }
    }

}
