public class DecoderUtils {

    static boolean isGraphic(byte ch) {
        // Check if the character is printable
        // Printable characters have unicode values greater than 32 (excluding control
        // characters) and aren't whitespace
        return (ch > 32 && ch != 127 && ch <= 255 && !Character.isWhitespace(ch));
    }

    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xFF));
        }
        return sb.toString();
    }

    public static boolean isFloat(int value) {
        int bitLen = 32;
        int expLen = 8;
        int mantLen = bitLen - expLen - 1;
        int exp = (value >> mantLen) & ((1 << expLen) - 1);
        exp -= (1 << (expLen - 1)) - 1;
        if (exp < 0) {
            exp = -exp;
        }
        int bigExp = (1 << (expLen - 1)) - 1;
        return exp < bigExp;
    }

    public static boolean isDouble(long value) {
        int bitLen = 64;
        int expLen = 11;
        int mantLen = bitLen - expLen - 1;
        long exp = (value >> mantLen) & ((1 << expLen) - 1);
        exp -= (1 << (expLen - 1)) - 1;
        if (exp < 0) {
            exp = -exp;
        }
        int bigExp = (1 << (expLen - 1)) - 1;
        return exp < bigExp;
    }
}
