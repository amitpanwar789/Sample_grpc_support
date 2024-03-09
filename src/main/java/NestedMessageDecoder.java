import com.google.protobuf.CodedInputStream;
import java.io.IOException;

public class NestedMessageDecoder {
    private CodedInputStream inputStream;

    public String startDecoding(byte[] inputData) {
        this.inputStream = CodedInputStream.newInstance(inputData);
        boolean check = true;
        String output = "{";
        while (check) {
            String validField = decodeField();
            if (validField.length() == 0)
                return validField;
            else
                output += "\"" + validField + "\"\n";
            try {
                if (inputStream.isAtEnd()) {
                    check = false;
                }
            } catch (IOException e) {
                return "";
            }
        }
        output += "}";
        return output;
    }

    private String decodeField() {
        int tag;
        try {
            tag = inputStream.readTag();
        } catch (IOException e) {
            return "";
        }

        // field number 0 is reserved for error
        if (tag >> 3 == 0) {
            return "";
        }

        // extract field number
        String decodedValue = (tag>>3) + ",";
        int wireType = (tag & 0x7);
        try {
            switch (wireType) {
                case 0: // Varint
                    long varintValue = inputStream.readRawVarint64();
                    decodedValue += Integer.toString(wireType) + "::" + Long.toString(varintValue);
                    break;

                case 1: // 64-bit
                    long longValue = inputStream.readRawLittleEndian64();
                    decodedValue += Integer.toString(wireType);
                    if (DecoderUtils.isDouble(longValue)) {
                        decodedValue += "D::" + Double.toString(Double.longBitsToDouble(longValue));
                    } else {
                        decodedValue += "::" + Long.toString(longValue);
                    }
                    break;

                case 5: // 32-bit
                    decodedValue += Integer.toString(wireType);
                    int intValue = inputStream.readRawLittleEndian32();
                    if (DecoderUtils.isFloat(intValue)) {
                        decodedValue += "F::" + Float.toString(Float.intBitsToFloat(intValue));
                    } else {
                        decodedValue += "::" + Integer.toString(intValue);
                    }
                    break;

                case 2: // Length-Prefixed string
                    decodedValue += Integer.toString(wireType);
                    String decoded = inputStream.readStringRequireUtf8();
                    byte[] stringBytes = decoded.getBytes();
                    // assume wire type 2 as Nested Message
                    // child nested message
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
                        // decode it as hex values
                        // assume not a human readable string
                        if ((double) unprintable / runes > 0.3) {
                            decodedValue += "B::" +  DecoderUtils.toHexString(stringBytes);
                        } else {
                            decodedValue += "::" +  decoded;
                        }

                    } else
                        decodedValue += "N::" + validMessage;

                    break;

                default:
                    return "";

            }
        } catch (IOException e) {
            return "";
        }
        return decodedValue;
    }

    private String checkNestedMessage(byte[] stringBytes) {
        NestedMessageDecoder nestedMessageDecoder = new NestedMessageDecoder();
        return nestedMessageDecoder.startDecoding(stringBytes);
    }

}
