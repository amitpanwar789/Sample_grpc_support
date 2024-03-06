

import java.util.*;

public class Demo {
    public Demo() {
    }

    public static String[] splitGrpcLengthPrefix(String hexInput) {
        // Note: Assuming ASCII encoded data
        byte[] decodedInput = hexStringToByteArray(hexInput);

        // Extract length prefix (first 5 bytes assume big-endian encoding)
        int lengthPrefix = ((decodedInput[0] & 0xFF) << 24) |
                ((decodedInput[1] & 0xFF) << 16) |
                ((decodedInput[2] & 0xFF) << 8) |
                ((decodedInput[3] & 0xFF)) |
                ((decodedInput[4] & 0xFF) >> 24); // Handle potential sign extension

        // Extract payload
        byte[] payload = Arrays.copyOfRange(decodedInput, 5, decodedInput.length);

        // Convert back to hex strings (optional)
            String hexPayload = byteArrayToHex(payload);

        return new String[] { String.valueOf(lengthPrefix), hexPayload };
    }

    // Helper function to convert hex string to byte array
    private static byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    // Helper function to convert byte array to hex string
    private static String byteArrayToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


    public static void main(String[] args) throws Exception {
        //String codedWithBase64 = "AAAAAEkKI3sibmFtZSI6IkpvaG4iLCJsYXN0bmFtZSI6Ik1pbGxlciJ9EB4aIDEyMzQgTWFpbiBTdC4gQW55dG93biwgVVNBIDEyMzQ1";
        //String onlyName = "AAAAADEKC2pvaG4gTWlsbGVyEB4aIDEyMzQgTWFpbiBTdC4gQW55dG93biwgVVNBIDEyMzQ1";
        //String mix = "AAAAAHwJzczMzMzcXkAVrseHQhi5YCDqrcDlJCixqAMwgYjis8oBOGNA/Y/fwEpNECcAAFHMexx6BQAAAF04////Ycips/r/////aAFyEEhlbGxvLCBQcm90b2J1ZiGAAQGKARwKCzEyMyBNYWluIFN0EgZNeUNpdHkaBTEyMzQ1";
        String mixWithRepeated = "AAAAAKIJzczMzMzcXkAVrseHQhi5YCDqrcDlJCixqAMwgYjis8oBOGNA/Y/fwEpN6AMAAFHMexx6BQAAAF04////Ycips/r/////aAFyEEhlbGxvLCBQcm90b2J1ZiGAAQGKARwKCzEyMyBNYWluIFN0EgZNeUNpdHkaBTEyMzQ1kgEMCgFhEgFiGgFjIgFkmgEBYZoBAWKaAQFjmgEBZKIBBAECAwQ=";
        byte[] decodedBytes = Base64.getDecoder().decode(mixWithRepeated);
        String hexString = byteArrayToHex(decodedBytes);
        String lengthAndPayload[] = splitGrpcLengthPrefix(hexString);
        ProtocolDecoder decoder = new ProtocolDecoder();
        decoder.startDecoding(hexStringToByteArray(lengthAndPayload[1]));
        System.out.println(decoder.getDecodedOuput());
    }
}

