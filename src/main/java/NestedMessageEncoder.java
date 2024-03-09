import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.protobuf.CodedOutputStream;

public class NestedMessageEncoder {
    private ByteArrayOutputStream outputStream;
    private CodedOutputStream codedOutputStream;
    private List<String> inputString;

    public ByteArrayOutputStream start(String nestedMessage) {
        this.outputStream = new ByteArrayOutputStream();
        this.codedOutputStream = CodedOutputStream.newInstance(outputStream);
        nestedMessage = EncoderUtils.removeBraces(nestedMessage);
        if (nestedMessage.length() == 0) {
            return outputStream;
        }

        String[] nestedMessageFields = nestedMessage.split("\n");
        if (!EncoderUtils.removeExtraChar(nestedMessageFields)) {
            return outputStream;
        }

        inputString = Arrays.asList(nestedMessageFields);
        writeFields();
        try {
            codedOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream;
    }

    private void writeFields() {

        for (String input : inputString) {
            String[] inputArray = input.split("::", 2);
            String[] fieldNumWireType = inputArray[0].split(",", 2);
            String tag = fieldNumWireType[1];
            int wiretype = 0;
            char extraCheck = 'n';
            if (tag.length() > 1) {
                wiretype = fieldNumWireType[1].charAt(0) - '0';
                extraCheck = fieldNumWireType[1].charAt(1);
            } else {
                wiretype = Integer.parseInt(fieldNumWireType[1]);
            }
            int fieldNumber = Integer.parseInt(fieldNumWireType[0]);
            try {
                switch (wiretype) {
                    case 0:
                        Long value = Long.parseLong(inputArray[1]);
                        codedOutputStream.writeInt64(fieldNumber, value);
                        break;
                    case 1:
                        // long value64 = Double.doubleToLongBits();
                        if (extraCheck == 'D') {
                            codedOutputStream.writeDouble(fieldNumber, Double.parseDouble(inputArray[1]));
                        } else {
                            codedOutputStream.writeFixed64(fieldNumber, Long.parseLong(inputArray[1]));
                        }
                        break;
                    case 2:
                        String val = inputArray[1];
                        if (extraCheck == 'B') {
                            byte[] byteArray = DecoderUtils.hexStringtoByteArray(inputArray[1]);
                            codedOutputStream.writeByteArray(fieldNumber, byteArray);
                        } else if (extraCheck == 'n') {
                            // human readable string
                            codedOutputStream.writeString(fieldNumber, val);
                        } else {
                            // nested message
                            byte[] byteArray = getNestedMessageEncodedValue(val);

                            if (byteArray.length == 0) {
                                codedOutputStream.writeString(fieldNumber, val);
                            } else {
                                // nested message
                            }
                        }

                        break;
                    case 5:

                        if (extraCheck == 'F') {
                            codedOutputStream.writeFloat(fieldNumber, Float.parseFloat(inputArray[1]));
                        } else {
                            codedOutputStream.writeFixed32(fieldNumber, Integer.parseInt(inputArray[1]));
                        }

                        break;
                }
            } catch (IOException e) {
                // Handle or log the exception
            }

        }
    }

    private byte[] getNestedMessageEncodedValue(String nestedMessage) {
        NestedMessageEncoder nestedMessageEncoder = new NestedMessageEncoder();
        byte[] byteArray = nestedMessageEncoder.start(nestedMessage).toByteArray();
        return byteArray;
    }

}
