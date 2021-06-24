package com.example.common;



import java.math.BigInteger;

/**
 * @author YL
 * @date 18:19 2021/6/2
 */
public class WriteStream {
    private static final int CHUNK_SIZE = 4096;

    private byte[] storage;

    private int writeIndex;

    public WriteStream() {
        this.writeIndex = 0;
        this.storage = new byte[CHUNK_SIZE];
    }

    public int length() {
        return this.storage.length;
    }

    public int unUsed() {
        return this.storage.length - this.writeIndex;
    }

    public byte[] finalBytes() {
        byte[] finalByte = new byte[this.writeIndex];
        System.arraycopy(this.storage, 0, finalByte, 0, this.writeIndex);
        return finalByte;
    }

    public String finalHex() {
        return Converter.bytes2hexStr(finalBytes());
    }

    public int getWriteIndex() {
        return this.writeIndex;
    }

    public void setWriteIndex(int writeIndex) {
        if (writeIndex >= this.length()) {
            throw new RuntimeException("you cannot set the writeIndex as the");
        }
        this.writeIndex = writeIndex;
    }

    public void writeFixedHex(int length, String value) {
        if (!Numeric.isHex(value)) {
            throw new RuntimeException("The value should be in hex format");
        }
        if (length * 2 != value.length()) {
            throw new RuntimeException("value length does not match expected length");
        }

        byte[] bytes = Converter.hexStr2bytes(value);
        setBytes(length, bytes);
    }

    public void writeBytes(int length, byte[] value) {
        setBytes(length, value);
    }

    public void writeByte(int value) {
        value = value & 0xff;
        byte[] bytes = Numeric.toBytes(BigInteger.valueOf(value));
        //byte[] bytes = Convert.intToBytes(value);
        setBytes(1, bytes);
    }

    public void writeUInt16(int value) {
        short n = (short) value;
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (n & 0xff);
        bytes[1] = (byte) (n >>> 8 & 0xff);
        setBytes(2, bytes);
    }

    public void writeUInt32(int value) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (value & 0xff);
        bytes[1] = (byte) (value >>> 8 & 0xff);
        bytes[2] = (byte) (value >>> 16 & 0xff);
        bytes[3] = (byte) (value >>> 24 & 0xff);
        setBytes(4, bytes);
    }

    public void writeUInt64(long value) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (value & 0xff);
        bytes[1] = (byte) (value >>> 8 & 0xff);
        bytes[2] = (byte) (value >>> 16 & 0xff);
        bytes[3] = (byte) (value >>> 24 & 0xff);
        bytes[4] = (byte) (value >>> 32 & 0xff);
        bytes[5] = (byte) (value >>> 40 & 0xff);
        bytes[6] = (byte) (value >>> 48 & 0xff);
        bytes[7] = (byte) (value >>> 56 & 0xff);
        setBytes(8, bytes);
    }

    public void writeBoolean(Boolean val) {
        //setBytes(1, Convert.intToBytes(val ? 1 : 0));
        setBytes(1, Numeric.toBytes(BigInteger.valueOf(val ? 1 : 0)));
    }

    private void setBytes(int length, byte[] value) {
        this.expand(length);
        System.arraycopy(value, 0, this.storage, this.writeIndex, value.length);
        this.writeIndex += length;
    }

    private void expand(int additional) {
        if (this.writeIndex + additional > this.storage.length) {
            int newLength = (int) Math.ceil(additional / WriteStream.CHUNK_SIZE) * WriteStream.CHUNK_SIZE;
            byte[] newBytes = new byte[newLength];
            System.arraycopy(this.storage, 0, newBytes, 0, this.storage.length);
            this.storage = newBytes;
        }
    }
}
