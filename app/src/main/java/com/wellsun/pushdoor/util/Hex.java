package com.wellsun.pushdoor.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * date     : 2023-03-24
 * author   : ZhaoZheng
 * describe :
 */
public class Hex {
    /**
     * 16进制直接转换成为字符串(无需Unicode解码)
     *
     * @param hexStr
     * @return
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    /**
     * 字符串转换成为16进制(无需Unicode编码)
     *
     * @param str
     * @return
     */
    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            // sb.append(' ');
        }
        return sb.toString().trim();
    }


    private static final char[] DIGITS_HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
            'E', 'F'};

    protected static char[] encodeHex(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS_HEX[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_HEX[0x0F & data[i]];
        }
        return out;
    }

    protected static byte[] decodeHex(char[] data) {
        int len = data.length;
        if ((len & 0x01) != 0) {
            throw new RuntimeException("字符个数应该为偶数");
        }
        byte[] out = new byte[len >> 1];
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f |= toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }
        return out;
    }

    protected static int toDigit(char ch, int index) {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new RuntimeException("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }

    //字符串和文字转 十六进制字符串  汉字占三个字节 6位
    public static String stringToHex(String str) {
        return new String(encodeHex(str.getBytes()));
    }

    //十六进制字符串转 字符串或汉字
    public static String hexToString(String hex) {
        return new String(decodeHex(hex.toCharArray()));
    }


    public static void main(String[] args) {
        String string = stringToHex("赵征abcdeXx");
        String string1 = hexToString(string);
        System.out.println(string);
        System.out.println(string1);

        String hexStr = str2HexStr("赵征abideXx");
        String s = hexStr2Str(hexStr);
        System.out.println(hexStr);
        System.out.println(s);

        String csn = String.format("%64s", string).replace("  ", "30"); //前面补零到16位
        System.out.println(csn);



    }


}
