package com.example.common;


/**
 * @author YL
 * @date 18:42 2021/6/3
 */
public class ReadStream {

    private final byte[] storage;

    private int readIndex;

    public static final int MIN_TRANSACTION_ESSENCE_LENGTH = 1 + (2 * 2) + 4;

    public static final int UINT16_SIZE = 2;

    public ReadStream(byte[] storage) {
        this(storage, 0);
    }

    public ReadStream(byte[] storage, int readStartIndex) {
        this.storage = storage;
        this.readIndex = readStartIndex;
    }

    public int length() {
        return this.storage.length;
    }

    public Boolean hasRemaining(int remaining) {
        return this.readIndex + remaining <= this.storage.length;
    }

    public int unUsed() {
        return this.storage.length - this.readIndex;
    }

    public int getReadIndex() {
        return this.readIndex;
    }

    public void setReadIndex(int readIndex) {
        if (readIndex >= this.storage.length) {
            throw new RuntimeException("you cannot set the writeIndex as the");
        }
        this.readIndex = readIndex;
    }

    public String readFixeHex(int length) {
        return readFixeHex(length, true);
    }

    public String readFixeHex(int length, boolean moveIndex) {

        if (!this.hasRemaining(length)) {
            throw new RuntimeException("length exceeds the remaining data unUsed()");
        }
        byte[] temp = new byte[length];
        System.arraycopy(this.storage, this.readIndex, temp, 0, length);
        String hex = Numeric.toHexString(temp);
        if (moveIndex) {
            this.readIndex += length;
        }
        return hex;
    }

    public byte[] readBytes() {
        return readBytes(1);
    }

    public byte[] readBytes(int length) {
        return readBytes(length, true);
    }

    public byte[] readBytes(int length, boolean moveIndex) {
        if (!this.hasRemaining(length)) {
            throw new RuntimeException("length exceeds the remaining data unUsed()");
        }
        byte[] temp = new byte[length];
        System.arraycopy(this.storage, this.readIndex, temp, 0, length);
        if (moveIndex) {
            this.readIndex += length;
        }
        return temp;
    }

    public int readByte() {
        return readByte(true);
    }

    public int readByte(boolean moveIndex) {
        if (!this.hasRemaining(1)) {
            throw new RuntimeException("length 1 exceeds the remaining data unUsed()");
        }
        int n = this.storage[this.readIndex];
        if (moveIndex) {
            this.readIndex += 1;
        }
        return n;
    }

    public int readUInt16() {
        return readUInt16(true);
    }

    public int readUInt16(boolean moveIndex) {
        if (!this.hasRemaining(2)) {
            throw new RuntimeException("length 2 exceeds the remaining data unUsed()");
        }
        int byte0 = this.storage[this.readIndex] & 0xff;
        int byte1 = this.storage[this.readIndex + 1] & 0xff;
        int n = byte0 | byte1 << 8;
        if (moveIndex) {
            this.readIndex += 2;
        }
        return n;
    }

    public int readUInt32() {
        return readUInt32(true);
    }

    public int readUInt32(boolean moveIndex) {
        if (!this.hasRemaining(4)) {
            throw new RuntimeException("length 4 exceeds the remaining data unUsed()");
        }

        int byte0 = this.storage[this.readIndex] & 0xff;
        int byte1 = this.storage[this.readIndex + 1] & 0xff;
        int byte2 = this.storage[this.readIndex + 2] & 0xff;
        int byte3 = this.storage[this.readIndex + 3] & 0xff;
        int n = byte0 | byte1 << 8 | byte2 << 16 | byte3 << 24;
        if (moveIndex) {
            this.readIndex += 4;
        }
        return n;
    }


    public long readUint64() {
        return readUint64(true);
    }

    public long readUint64(boolean moveIndex) {
        if (!this.hasRemaining(8)) {
            throw new RuntimeException("length 8 exceeds the remaining data unUsed()");
        }

        int byte0 = this.storage[this.readIndex] & 0xff;
        int byte1 = this.storage[this.readIndex + 1] & 0xff;
        int byte2 = this.storage[this.readIndex + 2] & 0xff;
        int byte3 = this.storage[this.readIndex + 3] & 0xff;
        int byte4 = this.storage[this.readIndex + 4] & 0xff;
        int byte5 = this.storage[this.readIndex + 5] & 0xff;
        int byte6 = this.storage[this.readIndex + 6] & 0xff;
        int byte7 = this.storage[this.readIndex + 7] & 0xff;
        long n = byte0 | byte1 << 8 | byte2 << 16 | (long) byte3 << 24 | (long) byte4 << 32 | (long) byte5 << 40 | (long) byte6 << 48 | (long) byte7 << 56;
        if (moveIndex) {
            this.readIndex += 8;
        }
        return n;
    }

    public boolean readBoolean() {
        return readBoolean(true);
    }

    public boolean readBoolean(boolean moveIndex) {
        if (!this.hasRemaining(1)) {
            throw new RuntimeException("length 1 exceeds the remaining data unUsed()");
        }
        int val = this.storage[this.readIndex];
        if (moveIndex) {
            this.readIndex += 1;
        }
        return val != 0;
    }
}
