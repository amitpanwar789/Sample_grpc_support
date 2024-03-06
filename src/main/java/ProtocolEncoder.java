import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import com.google.protobuf.CodedOutputStream;
import java.nio.ByteBuffer;



// currently only handle including int32, int64, string and fixed32, fixed64

public class ProtocolEncoder {
    private static final int HEADER_LENGTH = 5;
    private final List<String> inputString;
    private final ByteArrayOutputStream outputStream;
    private final CodedOutputStream codedOutputStream;
    private byte[] outputEncodedMessage;
    private ByteBuffer headerScratch;
    private int totalEncodedMessageSize;

    public ProtocolEncoder(List<String> inputString) {
        this.inputString = inputString;
        this.outputStream = new ByteArrayOutputStream();
        this.codedOutputStream = CodedOutputStream.newInstance(outputStream);
        headerScratch = ByteBuffer.allocate(HEADER_LENGTH);
        final int bufferSize = getSerializedSize();
        totalEncodedMessageSize = bufferSize + 5;
    }

    public String start() {
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
        int fieldNumber = 1;
        for (String input : inputString) {
            String[] inputArray = input.split("::");
            int varint = Integer.parseInt(inputArray[0]);
            switch (varint) {
                case 0:
                    int value = Integer.parseInt(inputArray[1]);
                    try {
                        codedOutputStream.writeInt32(fieldNumber, value);
                    } catch (IOException e) {
                        // Handle or log the exception
                    }
                    break;
                case 1:
                    long value64 = Long.parseLong(inputArray[1]);
                    try {
                        codedOutputStream.writeInt64(fieldNumber, value64);
                    } catch (IOException e) {
                        // Handle or log the exception
                    }
                    break;
                case 2:
                    String val = inputArray[1];
                    try {
                        codedOutputStream.writeString(fieldNumber, val);
                    } catch (IOException e) {
                        // Handle or log the exception
                    }
                    break;
                case 5:
                    int value32 = Integer.parseInt(inputArray[1]);
                    try {
                        codedOutputStream.writeInt32(fieldNumber, value32);
                    } catch (IOException e) {
                        // Handle or log the exception
                    }
                    break;
            }
            fieldNumber++;
        }
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
            String[] inputArray = input.split("::");
            int varint = Integer.parseInt(inputArray[0]);
            int fieldNumber = 1;
            switch (varint) {
                case 0:
                    size += CodedOutputStream.computeInt32Size(fieldNumber, Integer.parseInt(inputArray[1]));
                    break;
                case 1:
                    size += CodedOutputStream.computeInt64Size(fieldNumber, Long.parseLong(inputArray[1]));
                    break;
                case 2:
                    size += CodedOutputStream.computeStringSize(fieldNumber, inputArray[1]);
                    break;
                case 5:
                    size += CodedOutputStream.computeInt32Size(fieldNumber, Integer.parseInt(inputArray[1]));
                    break;
            }
        }
        return size;
    }
}
