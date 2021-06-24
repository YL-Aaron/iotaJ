package com.example.transaction;



import com.example.common.ReadStream;
import com.example.common.WriteStream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YL
 * @date 16:39 2021/6/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Input {

    private Integer type;

    private String transactionId;

    private Integer transactionOutputIndex;

    public static final int MIN_INPUT_COUNT = 1;

    public static final int UTXO_INPUT_TYPE = 0;

    public static final int MAX_INPUT_COUNT = 127;

    public static final int TREASURY_INPUT_TYPE = 1;

    public static final int TRANSACTION_ID_LENGTH = 32;

    public static final int MIN_INPUT_LENGTH = 1;

    public static final int MIN_UTXO_INPUT_LENGTH = MIN_INPUT_LENGTH + TRANSACTION_ID_LENGTH + ReadStream.UINT16_SIZE;

    public static void serializeInputs(WriteStream writeStream, List<Input> inputs) {
        if (null == inputs || inputs.isEmpty()) {
            throw new RuntimeException("Input can not be empty");
        }
        if (inputs.size() > MAX_INPUT_COUNT) {
            throw new RuntimeException("input maximum number is 127");
        }
        writeStream.writeUInt16(inputs.size());
        for (Input input : inputs) {
            serializeInput(writeStream, input);
        }
    }

    public static void serializeInput(WriteStream writeStream, Input input) {
        if (input.getType().equals(UTXO_INPUT_TYPE)) {
            serializeUTXOInput(writeStream, input);
        } else if (input.getType().equals(TREASURY_INPUT_TYPE)) {
            //TODO
        } else {
            throw new RuntimeException("Unrecognized input type");
        }
    }

    public static void serializeUTXOInput(WriteStream writeStream, Input input) {
        writeStream.writeByte(input.getType());
        writeStream.writeFixedHex(TRANSACTION_ID_LENGTH, input.getTransactionId());
        writeStream.writeUInt16(input.getTransactionOutputIndex());
    }

    public static List<Input> deserializeInputs(ReadStream readStream) {
        int numInputs = readStream.readUInt16();
        List<Input> inputs = new ArrayList<>();
        for (int i = 0; i < numInputs; i++) {
            inputs.add(deserializeInput(readStream));
        }
        return inputs;
    }

    public static Input deserializeInput(ReadStream readStream) {
        if (!readStream.hasRemaining(MIN_INPUT_LENGTH)) {
            throw new RuntimeException("Input data is ${readStream.length()} in length which is less than the minimimum size required of ${MIN_INPUT_LENGTH}");
        }
        int type = readStream.readByte(false);

        Input input = null;

        if (UTXO_INPUT_TYPE == type) {
            input = deserializeUTXOInput(readStream);
        } else if (TREASURY_INPUT_TYPE == type) {
            //TODO
        } else {
            throw new RuntimeException("Unrecognized input type");
        }
        return input;
    }

    public static Input deserializeUTXOInput(ReadStream readStream) {
        if (!readStream.hasRemaining(MIN_UTXO_INPUT_LENGTH)) {
            throw new RuntimeException("UTXO Input data is ${readStream.length()} in length which is less than the minimimum size required of ${MIN_UTXO_INPUT_LENGTH}");
        }
        int type = readStream.readByte();
        if (UTXO_INPUT_TYPE != type) {
            throw new RuntimeException("Type mismatch in utxoInput ${type}");
        }

        String transactionId = readStream.readFixeHex(TRANSACTION_ID_LENGTH);
        int transactionOutputIndex = readStream.readUInt16();
        return new Input(UTXO_INPUT_TYPE, transactionId, transactionOutputIndex);
    }
}
