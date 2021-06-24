package com.example.common;


import org.junit.platform.commons.util.StringUtils;

/**
 * @author YL
 * @date 16:37 2021/5/27
 */
public class Converter {
    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /*
     * byte[]数组转十六进制
     */
    public static String bytes2hexStr(byte[] bytes) {
        int len = bytes.length;
        if (len == 0) {
            return null;
        }
        char[] cbuf = new char[len * 2];
        for (int i = 0; i < len; i++) {
            int x = i * 2;
            cbuf[x] = HEX_CHARS[(bytes[i] >>> 4) & 0xf];
            cbuf[x + 1] = HEX_CHARS[bytes[i] & 0xf];
        }
        return new String(cbuf);
    }


    /*
     * 十六进制转byte[]数组
     */
    public static byte[] hexStr2bytes(String hexStr) {
        if (StringUtils.isBlank(hexStr)) {
            return null;
        }
        if (hexStr.length() % 2 != 0) {//长度为单数
            hexStr = "0" + hexStr;//前面补0
        }
        char[] chars = hexStr.toCharArray();
        int len = chars.length / 2;
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            int x = i * 2;
            bytes[i] = (byte) Integer.parseInt(String.valueOf(new char[]{chars[x], chars[x + 1]}), 16);
        }
        return bytes;
    }

    public static byte[] add(byte[] data1, byte[] data2) {

        byte[] result = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, result, 0, data1.length);
        System.arraycopy(data2, 0, result, data1.length, data2.length);

        return result;
    }
}
