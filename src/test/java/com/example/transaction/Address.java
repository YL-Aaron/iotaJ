package com.example.transaction;


import com.example.common.ReadStream;
import com.example.common.WriteStream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author YL
 * @date 17:39 2021/6/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private Integer type;

    private String address;

    public static final int ED25519_ADDRESS_TYPE = 0;

    public static final int ADDRESS_LENGTH = 32;

    public static final int MIN_ADDRESS_LENGTH = 1;

    public static final int MIN_ED25519_ADDRESS_LENGTH = MIN_ADDRESS_LENGTH + ADDRESS_LENGTH;


    public static void serializeAddress(WriteStream writeStream, Address address) {
        if (address.getType().equals(ED25519_ADDRESS_TYPE)) {
            serializeEd25519Address(writeStream, address);
        } else {
            throw new RuntimeException("Unrecognized address type");
        }
    }

    public static void serializeEd25519Address(WriteStream writeStream, Address address) {
        writeStream.writeByte(address.getType());
        writeStream.writeFixedHex(ADDRESS_LENGTH, address.getAddress());
    }


    public static Address deserializeAddress(ReadStream readStream) {
        if (!readStream.hasRemaining(MIN_ADDRESS_LENGTH)) {
            throw new RuntimeException("Address data is ${readStream.length()} in length which is less than the minimimum size required of ${MIN_ADDRESS_LENGTH}");
        }

        int type = readStream.readByte(false);
        Address address;
        if (type == ED25519_ADDRESS_TYPE) {
            address = deserializeEd25519Address(readStream);
        } else {
            throw new RuntimeException("Unrecognized address type ${type}");
        }
        return address;
    }

    public static Address deserializeEd25519Address(ReadStream readStream) {
        if (!readStream.hasRemaining(MIN_ED25519_ADDRESS_LENGTH)) {
            throw new RuntimeException("Ed25519 address data is ${readStream.length()} in length which is less than the minimimum size required of ${MIN_ED25519_ADDRESS_LENGTH}");
        }
        int type = readStream.readByte();
        if (type != ED25519_ADDRESS_TYPE) {
            throw new RuntimeException("Type mismatch in ed25519Address ${type}");
        }
        String address = readStream.readFixeHex(ADDRESS_LENGTH);
        return new Address(ED25519_ADDRESS_TYPE, address);
    }
}
