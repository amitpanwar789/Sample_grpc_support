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
                    decodedValue += DecoderUtils.isDouble(longValue)
                            ? Double.toString(Double.longBitsToDouble(longValue))
                            : Long.toString(longValue);

                    break;

                case 5: // 32-bit
                    int intValue = inputStream.readRawLittleEndian32();
                    decodedValue += DecoderUtils.isFloat(intValue) ? Float.toString(Float.intBitsToFloat(intValue))
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
                    if (validMessage.length() == 0) {
                        // not a nested message check for printable characters
                        int unprintable = 0;
                        int runes = stringBytes.length;
                        for (byte stringByte : stringBytes) {
                            if (!DecoderUtils.isGraphic(stringByte)) {
                                unprintable++;
                            }
                        }

                        // assume not a human readable string
                        // decode it as hex values
                        if ((double) unprintable / runes > 0.3) {
                            decodedValue += DecoderUtils.toHexString(stringBytes);
                        } else {
                            decodedValue += decoded;
                        }
                    } else
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

    public String getDecodedOuput() {
        return decodedOutput.toString();
    }
}
