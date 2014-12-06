package com.naukma.cauliflower.mail;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Formatter;
import java.util.Random;

/**
 * Created by ihor on 06.12.2014.
 */
public class Cryptographer {


    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    private static  String KEY;

    static {
        generateKey();
    }

    public static String hmacSha1(String data){
        return hmacSha1(data,KEY);
    }

    private static String hmacSha1(String data,String key)

    {

        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = null;
        try {
            mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            mac.init(signingKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return toHexString(mac.doFinal(data.getBytes()));
    }



    private static void generateKey(){

        KEY=hmacSha1("cauliflower"," ");
    }

    private static String toHexString(byte[] bytes) {

        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        StringBuffer buffer= new StringBuffer(formatter.toString());
        return buffer.substring(0,36).toString();
    }
}

