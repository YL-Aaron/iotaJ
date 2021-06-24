package com.example.transaction;



import com.example.common.ReadStream;
import com.example.common.WriteStream;

import java.util.List;

/**
 * @author YL
 * @date 14:13 2021/6/3
 */
public class Transaction {

    private static final int SIG_LOCKED_SINGLE_OUTPUT_TYPE = 0;

    private static final int SIG_LOCKED_DUST_ALLOWANCE_OUTPUT_TYPE = 1;

    private static final int TRANSACTION_ESSENCE_TYPE = 0;

    public void serializeTransactionEssence(WriteStream writeStream, Essence essence) {
        writeStream.writeByte(essence.getType());
        //校验输入
        for (Input input : essence.getInputs()) {
            if (!input.getType().equals(Input.UTXO_INPUT_TYPE)) {
                throw new RuntimeException("Transaction essence can only contain UTXO Inputs");
            }
        }
        //序列化输入
        Input.serializeInputs(writeStream, essence.getInputs());

        //校验输出
        for (Output output : essence.getOutputs()) {
            if (!output.getType().equals(SIG_LOCKED_SINGLE_OUTPUT_TYPE) && !output.getType().equals(SIG_LOCKED_DUST_ALLOWANCE_OUTPUT_TYPE)) {
                throw new RuntimeException("Transaction essence can only contain sig locked single input or sig locked dust allowance outputs");
            }
        }

        //序列化输出
        Output.serializeOutputs(writeStream, essence.getOutputs());

        //序列化Payload
        Payload.serializePayload(writeStream, essence.getPayload());
    }

    public Essence deserializeTransactionEssence(ReadStream readStream) {
        if (!readStream.hasRemaining(ReadStream.MIN_TRANSACTION_ESSENCE_LENGTH)) {
            throw new RuntimeException("Transaction essence data Too short");
        }
        int type = readStream.readByte();
        if (TRANSACTION_ESSENCE_TYPE != type) {
            throw new RuntimeException("Type mismatch in transactionEssence type");
        }
        List<Input> inputs = Input.deserializeInputs(readStream);
        List<Output> outputs = Output.deserializeOutputs(readStream);
        Payload payload = Payload.deserializePayload(readStream);
        if (null != payload && payload.getType() != Payload.INDEXATION_PAYLOAD_TYPE) {
            throw new RuntimeException("Transaction essence can only contain embedded Indexation Payload");
        }
        for (Input input : inputs) {
            if (Input.UTXO_INPUT_TYPE != input.getType()) {
                throw new RuntimeException("Transaction essence can only contain UTXO Inputs");
            }
        }
        for (Output output : outputs) {
            if (SIG_LOCKED_SINGLE_OUTPUT_TYPE != output.getType() && SIG_LOCKED_DUST_ALLOWANCE_OUTPUT_TYPE != output.getType()) {
                throw new RuntimeException("Transaction essence can only contain sig locked single input or sig locked dust allowance outputs");
            }
        }

        return new Essence(TRANSACTION_ESSENCE_TYPE, inputs, outputs, payload);
    }
}
