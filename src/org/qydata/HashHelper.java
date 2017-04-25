package org.qydata;

import java.security.MessageDigest;

/**
 * Created by jonhn on 2016/11/11.
 */
public class HashHelper {
    public static String md5(String plainText) {
        StringBuffer buf = new StringBuffer("");
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return  buf.toString().toUpperCase();
    }

    public static String reqId() {
        int str1 = 0;
        for (int i = 0; i < 25; i++) {
            str1 = (char) (Math.random() * 26);
        }
        String str = "";
        for (int i = 0; i < 6; i++) {
            str = str + (char) (Math.random() * 26 + 'A');
        }
        return str+str1;
    }
}
