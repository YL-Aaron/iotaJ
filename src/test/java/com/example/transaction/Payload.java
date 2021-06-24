package com.example.transaction;



import com.example.common.ReadStream;
import com.example.common.WriteStream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author YL
 * @date 17:43 2021/6/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payload {

    private Integer type;

    private String index;

    private String data;

    public static final int TRANSACTION_PAYLOAD_TYPE = 0;

    public static final int MILESTONE_PAYLOAD_TYPE = 1;

    public static final int INDEXATION_PAYLOAD_TYPE = 2;

    public static final int RECEIPT_PAYLOAD_TYPE = 3;

    public static final int TREASURY_TRANSACTION_PAYLOAD_TYPE = 4;

    public static final int UINT32_SIZE = 4;

    public static final int MIN_INDEXATION_KEY_LENGTH = 1;

    public static final int MAX_INDEXATION_KEY_LENGTH = 64;

    public static void serializePayload(WriteStream writeStream, Payload payload) {
        final int payloadLengthWriteIndex = writeStream.getWriteIndex();
        writeStream.writeUInt32(0);
        if (null == payload) {

        } else {
            switch (payload.getType()) {
                case TRANSACTION_PAYLOAD_TYPE:
                    //TODO
                    break;
                case MILESTONE_PAYLOAD_TYPE:
                    //TODO
                    break;
                case INDEXATION_PAYLOAD_TYPE:
                    serializeIndexationPayload(writeStream, payload);
                    break;
                case RECEIPT_PAYLOAD_TYPE:
                    //TODO
                    break;
                case TREASURY_TRANSACTION_PAYLOAD_TYPE:
                    //TODO
                    break;
                default:
                    throw new RuntimeException("Unrecognized transaction type ");
            }
            final int endOfPayloadWriteIndex = writeStream.getWriteIndex();
            writeStream.setWriteIndex(payloadLengthWriteIndex);
            writeStream.writeUInt32(endOfPayloadWriteIndex - payloadLengthWriteIndex - UINT32_SIZE);
            writeStream.setWriteIndex(endOfPayloadWriteIndex);
        }
    }

    public static void serializeIndexationPayload(WriteStream writeStream, Payload payload) {
        if (payload.getIndex().length() < MIN_INDEXATION_KEY_LENGTH) {
            throw new RuntimeException("The indexation key length is ${object.index.length}, which is below the minimum size of ${MIN_INDEXATION_KEY_LENGTH}");
        }
        if (payload.getIndex().length() / 2 > MAX_INDEXATION_KEY_LENGTH) {
            throw new RuntimeException("The indexation key length is ${object.index.length / 2}, which exceeds the maximum size of ${MAX_INDEXATION_KEY_LENGTH}");
        }
        writeStream.writeUInt32(payload.getType());
        writeStream.writeUInt16(payload.getIndex().length() / 2);
        writeStream.writeFixedHex(payload.getIndex().length() / 2, payload.getIndex());
        if (null != payload.getData()) {
            writeStream.writeUInt32(payload.getData().length() / 2);
            writeStream.writeFixedHex(payload.getData().length() / 2, payload.getData());
        } else {
            writeStream.writeUInt32(32);
        }
    }

    public static Payload deserializePayload(ReadStream readStream) {
        int payloadLength = readStream.readUInt32();
        if (!readStream.hasRemaining(payloadLength)) {
            throw new RuntimeException("Payload length ${payloadLength} exceeds the remaining data ${readStream.unused()}");
        }
        Payload payload = null;
        if (payloadLength > 0) {
            int payloadType = readStream.readUInt32(false);
            switch (payloadType) {
                case TRANSACTION_PAYLOAD_TYPE:
                    break;
                case MILESTONE_PAYLOAD_TYPE:
                    break;
                case INDEXATION_PAYLOAD_TYPE:
                    break;
                case RECEIPT_PAYLOAD_TYPE:
                    break;
                case TREASURY_TRANSACTION_PAYLOAD_TYPE:
                    break;
                default:
                    throw new RuntimeException("Unrecognized payload type");
            }
        }
        return payload;
    }
}
