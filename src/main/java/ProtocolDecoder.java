import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.CodedInputStream;

public class ProtocolDecoder {

    private byte[] inputData;
    private List<String> decodedValues;
    private StringBuilder decodedOutput;
    private CodedInputStream inputStream;

    public ProtocolDecoder() {
        this.decodedValues = new ArrayList<>();
        this.decodedOutput = new StringBuilder();

    }

    public void startDecoding(byte[] inputData) {
        this.inputData = inputData;
        this.inputStream = CodedInputStream.newInstance(inputData);
        decodedValues.clear();
        boolean check = true;
        while (check) {
            decodeField();
            try {
                if (inputStream.isAtEnd()) {
                    check = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void decodeField() {
        int tag;
        try {
            tag = inputStream.readTag();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String decodedValue = (tag >> 3) + "::";
        try {
            switch (tag & 0x7) {
                case 0: // Varint
                    long varintValue = inputStream.readRawVarint64();
                    decodedValue += Long.toString(varintValue);
                    break;

                case 1: // 64-bit
                    long longValue = inputStream.readRawLittleEndian64();
                    decodedValue += isDouble(longValue) ? Double.toString(Double.longBitsToDouble(longValue))
                            : Long.toString(longValue);

                    break;

                case 5: // 32-bit
                    int intValue = inputStream.readRawLittleEndian32();
                    decodedValue += isFloat(intValue) ? Float.toString(Float.intBitsToFloat(intValue))
                            : Integer.toString(intValue);

                    break;

                case 2: // Length-Prefixed string
                    String decoded = inputStream.readStringRequireUtf8();
                    byte[] stringBytes = decoded.getBytes();
                    // assume wire type 2 as Nested Message
                    // child nested message , recursively check each nestedMessage field
                    // if not able to successfully decode as NestedMessage field, then consider it
                    // as string
                    // still need to check for packed repeated fields
                    String validMessage = checkNestedMessage(stringBytes);
                    if (validMessage.length() == 0)
                        decodedValue += decoded;
                    else
                        decodedValue += validMessage;

                    break;

                default:
                    return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        decodedValues.add(decodedValue);
        decodedOutput.append(decodedValue).append("\n");
    }

    private String checkNestedMessage(byte[] stringBytes) {
        DecoderNestedMessage decoderNestedMessage = new DecoderNestedMessage();
        return decoderNestedMessage.startDecoding(stringBytes);
    }

    static boolean isFloat(int value) {
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

    static boolean isDouble(long value) {
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

    public String getDecodedOuput() {
        return decodedOutput.toString();
    }
}
