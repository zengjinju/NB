package com.zjj.nb.biz.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by jinju.zeng on 2017/5/11.
 */
public class MD5Util {

    private static final String hexDigits[] = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    private static final String DEFAULT_CHASET="utf-8";
    public static final String KEY_SHA = "SHA";

    private static String byteToHexString(byte b) {
        int n=b;
        if (n < 0) {
            n += 256;
        }
        int b1 = n / 16;
        int b2 = n % 16;
        return hexDigits[b1] + hexDigits[b2];
    }

    private static String byteArrayToHexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(byteToHexString(b));
        }
        return builder.toString();
    }

    public static String md5(String var) {
        return md5(var,DEFAULT_CHASET);
    }

    public static String md5(String var ,String charset){
        String result=null;
        try {
            byte[] bytes=new byte[0];
            MessageDigest md5=MessageDigest.getInstance("MD5");
            if(charset==null||"".equals(charset)) {
                bytes = md5.digest(var.getBytes());
            }else{
                bytes=md5.digest(var.getBytes(charset));
            }
            result=byteArrayToHexString(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String sha(String inStr){
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA");
            byte[] byteArray = inStr.getBytes("UTF-8");
            byte[] md5Bytes = sha.digest(byteArray);
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args){
        System.out.println(md5("123abc"));
    }
}
