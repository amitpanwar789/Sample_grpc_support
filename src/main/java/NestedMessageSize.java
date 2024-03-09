import java.util.Arrays;
import java.util.List;

import com.google.protobuf.CodedOutputStream;

public class NestedMessageSize {
    private List<String> inputString;

    public int computeSize(String nestedMessage) {
        int size = 0;

        if (!buildInputString(nestedMessage)) {
            return size;
        }
        ;
        size += getSerializedSize();
        return size;
    }

    private boolean buildInputString(String nestedMessage) {
        nestedMessage = EncoderUtils.removeBraces(nestedMessage);
        if (nestedMessage.length() == 0) {
            return false;
        }

        String[] nestedMessageFields = nestedMessage.split("\n");
        if (!EncoderUtils.removeExtraChar(nestedMessageFields)) {
            return false;
        }

        inputString = Arrays.asList(nestedMessageFields);
        return true;

    }


    private int getSerializedSize() {
        int size = 0;
        for (String input : inputString) {
            String[] inputArray = input.split("::", 2);
            String[] fieldNumWireType = inputArray[0].split(",",2);
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
            switch (wiretype) {
                case 0:
                    size += CodedOutputStream.computeInt64Size(fieldNumber, Long.parseLong(inputArray[1]));
                    break;
                case 1:
                    if (extraCheck == 'D') {
                        size += CodedOutputStream.computeDoubleSize(fieldNumber, Double.parseDouble(inputArray[1]));
                    } else {
                        size += CodedOutputStream.computeInt64Size(fieldNumber, Long.parseLong(inputArray[1]));
                    }
                    break;
                case 2:
                    if (extraCheck == 'B') {
                        byte[] byteArray = DecoderUtils.hexStringtoByteArray(inputArray[1]);
                        size += CodedOutputStream.computeByteArraySize(fieldNumber, byteArray);
                    } else if (extraCheck == 'n') {
                        // human readable messasge
                        size += CodedOutputStream.computeStringSize(fieldNumber, inputArray[1]);
                    } else {
                        // nested message
                        int nestedMessageSize = computeNestedMessageSize(fieldNumber, inputArray[1]);
                        // if failed to get size of nested message
                        if (nestedMessageSize == 0) {
                            size += CodedOutputStream.computeStringSize(fieldNumber, inputArray[1]);
                        } else {
                            size += nestedMessageSize;
                        }
                    }
                    break;
                case 5:
                    if (extraCheck == 'F') {
                        size += CodedOutputStream.computeFloatSize(fieldNumber, Float.parseFloat(inputArray[1]));
                    } else {
                        size += CodedOutputStream.computeInt32Size(fieldNumber, Integer.parseInt(inputArray[1]));
                    }
                    break;
            }
           
        }
        return size;
    }

    private int computeNestedMessageSize(int fieldNumber, String nestedMessage) {
        int size = CodedOutputStream.computeTagSize(fieldNumber);
        NestedMessageSize nestedMessageSize = new NestedMessageSize();
        int nesMessageSize = nestedMessageSize.computeSize(nestedMessage);
        return size + nesMessageSize + CodedOutputStream.computeUInt32SizeNoTag(nesMessageSize);
    }

}
