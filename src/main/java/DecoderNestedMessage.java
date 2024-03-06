import com.google.protobuf.CodedInputStream;
import java.io.IOException;

public class DecoderNestedMessage {
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
                output += "\"" + validField + "\", \n";
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

    public String decodeField() {
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
        String decodedValue = (tag >> 3) + "::";
        try {
            switch (tag & 0x7) {
                case 0: // Varint
                    long varintValue = inputStream.readRawVarint64();
                    decodedValue += Long.toString(varintValue);
                    break;

                case 1: // 64-bit
                    long longValue = inputStream.readRawLittleEndian64();
                    decodedValue += ProtocolDecoder.isDouble(longValue)
                            ? Double.toString(Double.longBitsToDouble(longValue))
                            : Long.toString(longValue);

                    break;

                case 5: // 32-bit
                    int intValue = inputStream.readRawLittleEndian32();
                    decodedValue += ProtocolDecoder.isFloat(intValue) ? Float.toString(Float.intBitsToFloat(intValue))
                            : Integer.toString(intValue);

                    break;

                case 2: // Length-Prefixed string
                    String decoded = inputStream.readStringRequireUtf8();
                    byte[] stringBytes = decoded.getBytes();
                    // assume wire type 2 as Nested Message
                    // child nested message
                    String validMessage = checkNestedMessage(stringBytes);
                    if (validMessage.length() == 0) {
                        decodedValue += decoded;
                    } else
                        decodedValue += validMessage;

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
        DecoderNestedMessage decoderNestedMessage = new DecoderNestedMessage();
        return decoderNestedMessage.startDecoding(stringBytes);
    }

}
