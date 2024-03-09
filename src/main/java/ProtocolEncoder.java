import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import com.google.protobuf.CodedOutputStream;

import java.nio.ByteBuffer;


// This encoder can only process messages that have been decoded by the decoder in this package.
// This is due to the strict input message pattern it adheres to.
public class ProtocolEncoder {
    private static final int HEADER_LENGTH = 5;
    private List<String> inputString;
    private ByteArrayOutputStream outputStream;
    private CodedOutputStream codedOutputStream;
    private byte[] outputEncodedMessage;
    private ByteBuffer headerScratch;
    private int totalEncodedMessageSize;

    public ProtocolEncoder() {

    }

    public String start(List<String> inputString) {
        this.inputString = inputString;
        this.outputStream = new ByteArrayOutputStream();
        this.codedOutputStream = CodedOutputStream.newInstance(outputStream);
        headerScratch = ByteBuffer.allocate(HEADER_LENGTH);
        final int bufferSize = getSerializedSize();
        totalEncodedMessageSize = bufferSize + 5;
        writeHeader();
        writeFields();
        try {
            codedOutputStream.flush();
            setOutputEncodedMessage();
        } catch (IOException e) {

        }

        return ""; // Return output string or appropriate result
    }

    private void writeHeader() {
        headerScratch.clear();
        headerScratch.put((byte) 0).putInt(totalEncodedMessageSize - HEADER_LENGTH);
        outputStream.write(headerScratch.array(), 0, headerScratch.position());
    }

    private void writeFields() {
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
            try {
                switch (wiretype) {
                    case 0:
                        Long value = Long.parseLong(inputArray[1]);
                        codedOutputStream.writeInt64(fieldNumber, value);
                        break;
                    case 1:
                        //long value64 = Double.doubleToLongBits();
                        if (extraCheck == 'D') {
                            codedOutputStream.writeDouble(fieldNumber,Double.parseDouble(inputArray[1]));
                        }
                        else {
                            codedOutputStream.writeFixed64(fieldNumber, Long.parseLong(inputArray[1]));
                        }
                        break;
                    case 2:
                        String val = inputArray[1];
                        if (extraCheck == 'B'){
                            byte[] byteArray = DecoderUtils.hexStringtoByteArray(inputArray[1]);
                            codedOutputStream.writeByteArray(fieldNumber,byteArray);
                        }
                        else if(extraCheck == 'n'){
                            // human readable string
                            codedOutputStream.writeString(fieldNumber, val);
                        }
                        else {
                            // nested message
                            byte[] byteArray = getNestedMessageEncodedValue(val);
                            
                            if(byteArray.length == 0){
                                codedOutputStream.writeString(fieldNumber, val);
                            }else{
                                // nested message
                                
                                codedOutputStream.writeByteArray(fieldNumber,byteArray);
                            }
                        }

                        break;
                    case 5:
                        
                        
                        if (extraCheck == 'F') {
                            codedOutputStream.writeFloat(fieldNumber,Float.parseFloat(inputArray[1]));
                        }
                        else {
                            codedOutputStream.writeFixed32(fieldNumber, Integer.parseInt(inputArray[1]));
                        }

                        break;
                }
            } catch (IOException e) {
                // Handle or log the exception
            }
            
        }
    }

    public byte[] getNestedMessageEncodedValue(String nestedMessage){
        NestedMessageEncoder  nestedMessageEncoder = new NestedMessageEncoder();
        byte[] byteArray = nestedMessageEncoder.start(nestedMessage).toByteArray();
        return byteArray;
    }


    public byte[] getOutputEncodedMessage() {
        return outputEncodedMessage;
    }

    private void setOutputEncodedMessage() {
        byte[] outputStreamBytes = outputStream.toByteArray();
        outputEncodedMessage = new byte[totalEncodedMessageSize];
        for (int i = 0; i < totalEncodedMessageSize; i++) {
            outputEncodedMessage[i] = outputStreamBytes[i];
        }
    }

    private int getSerializedSize() {
        int size = 0;
        
        for (String input : inputString) {
            String[] inputArray = input.split("::", 2);
            // split field and wiretype
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
                        size += CodedOutputStream.computeFixed64Size(fieldNumber, Long.parseLong(inputArray[1]));
                    }
                    break;
                case 2:
                    if (extraCheck == 'B') {
                        byte[] byteArray = DecoderUtils.hexStringtoByteArray(inputArray[1]);
                        size += CodedOutputStream.computeByteArraySize(fieldNumber, byteArray);
                    } else if(extraCheck == 'n'){
                        // human readable messasge
                        size += CodedOutputStream.computeStringSize(fieldNumber, inputArray[1]);
                    }
                    else {   
                        // nested message                     
                        int nestedMessageSize = computeNestedMessageSize(fieldNumber,inputArray[1]);
                        // if failed to get size
                        if(nestedMessageSize == 0){
                            size += CodedOutputStream.computeStringSize(fieldNumber, inputArray[1]);
                        }
                        else {
                            size += nestedMessageSize;
                        }
                    }
                    break;
                case 5:
                    if (extraCheck == 'F') {
                        size += CodedOutputStream.computeFloatSize(fieldNumber, Float.parseFloat(inputArray[1]));
                    } else {
                        size += CodedOutputStream.computeFixed32Size(fieldNumber, Integer.parseInt(inputArray[1]));
                    }
                    break;
            }
            
        }
        return size;
    }
    private int computeNestedMessageSize(int fieldNumber, String nestedMessage){
        int size = CodedOutputStream.computeTagSize(fieldNumber);
        NestedMessageSize nestedMessageSize = new NestedMessageSize();
        int nesMessageSize = nestedMessageSize.computeSize(nestedMessage);
        return size + nesMessageSize + CodedOutputStream.computeUInt32SizeNoTag(nesMessageSize);
    }
}
