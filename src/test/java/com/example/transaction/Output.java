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
 * @date 17:36 2021/6/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Output {

    private Integer type;

    private Address address;

    private Long amount;

    public static final int MIN_OUTPUT_COUNT = 1;

    public static final int MAX_OUTPUT_COUNT = 127;

    public static final int SIG_LOCKED_SINGLE_OUTPUT_TYPE = 0;

    public static final int SIG_LOCKED_DUST_ALLOWANCE_OUTPUT_TYPE = 1;

    public static final int TREASURY_OUTPUT_TYPE = 2;

    public static final int MIN_OUTPUT_LENGTH = 1;

    public static final int MIN_SIG_LOCKED_SINGLE_OUTPUT_LENGTH = MIN_OUTPUT_LENGTH + Address.MIN_ADDRESS_LENGTH + Address.MIN_ED25519_ADDRESS_LENGTH;

    public static void serializeOutputs(WriteStream writeStream, List<Output> outputs) {
        if (null == outputs || outputs.isEmpty()) {
            throw new RuntimeException("Output can not be empty");
        }
        if (outputs.size() > MAX_OUTPUT_COUNT) {
            throw new RuntimeException("input maximum number is 127");
        }

        writeStream.writeUInt16(outputs.size());

        for (Output output : outputs) {
            serializeOutput(writeStream, output);
        }
    }

    public static void serializeOutput(WriteStream writeStream, Output output) {
        switch (output.getType()) {
            case SIG_LOCKED_SINGLE_OUTPUT_TYPE:
                serializeSigLockedSingleOutput(writeStream, output);
                break;
            case SIG_LOCKED_DUST_ALLOWANCE_OUTPUT_TYPE:
                //TODO
                break;
            case TREASURY_OUTPUT_TYPE:
                //TODO
                break;
            default:
                throw new RuntimeException("Unrecognized output type");
        }
    }

    public static void serializeSigLockedSingleOutput(WriteStream writeStream, Output output) {
        writeStream.writeByte(output.getType());
        Address.serializeAddress(writeStream, output.getAddress());
        writeStream.writeUInt64(output.getAmount());
    }

    public static List<Output> deserializeOutputs(ReadStream readStream) {
        int numOutputs = readStream.readUInt16();
        List<Output> outputs = new ArrayList<>();
        for (int i = 0; i < numOutputs; i++) {
            outputs.add(deserializeOutput(readStream));
        }
        return outputs;
    }

    public static Output deserializeOutput(ReadStream readStream) {
        if (!readStream.hasRemaining(MIN_OUTPUT_LENGTH)) {
            throw new RuntimeException("Output data is ${readStream.length()} in length which is less than the minimimum size required of ${MIN_OUTPUT_LENGTH}");
        }
        int type = readStream.readByte(false);

        Output output = null;
        switch (type) {
            case SIG_LOCKED_SINGLE_OUTPUT_TYPE:
                output = deserializeSigLockedSingleOutput(readStream);
                break;
            case SIG_LOCKED_DUST_ALLOWANCE_OUTPUT_TYPE:
                //TODO
                break;
            case TREASURY_OUTPUT_TYPE:
                //TODO
                break;
            default:
                throw new RuntimeException("Unrecognized output type");
        }
        return output;
    }

    public static Output deserializeSigLockedSingleOutput(ReadStream readStream) {
        if (!readStream.hasRemaining(MIN_SIG_LOCKED_SINGLE_OUTPUT_LENGTH)) {
            throw new RuntimeException("Signature Locked Single Output data is ${readStream.length()} in length which is less than the minimimum size required of ${MIN_SIG_LOCKED_SINGLE_OUTPUT_LENGTH}");
        }
        int type = readStream.readByte();

        if (SIG_LOCKED_SINGLE_OUTPUT_TYPE != type) {
            throw new RuntimeException("Type mismatch in sigLockedSingleOutput ${type}");
        }
        Address address = Address.deserializeAddress(readStream);
        long amount = readStream.readUint64();
        return new Output(SIG_LOCKED_SINGLE_OUTPUT_TYPE, address, amount);
    }

}
