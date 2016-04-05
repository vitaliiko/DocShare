package com.geekhub.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DocumentNameDigest {

    public static String hashName(String text) {
        MessageDigest md5 ;
        StringBuilder hexString = new StringBuilder();
        try {
            md5 = MessageDigest.getInstance("md5");
            md5.reset();
            md5.update(text.getBytes());

            byte messageDigest[] = md5.digest();
            for (byte aMessageDigest : messageDigest) {
                hexString.append(Integer.toHexString(0xFF & aMessageDigest));
            }
        }
        catch (NoSuchAlgorithmException e) {
            return e.toString();
        }
        return hexString.toString();
    }
}
