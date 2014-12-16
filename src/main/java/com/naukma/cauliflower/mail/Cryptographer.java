package com.naukma.cauliflower.mail;



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
    private static final int MAX_STRING_LENGTH = 36;
    private static final String CONVERSION_FRORMAT="%02x";
    static {
        generateKey();
    }
    private static String KEY;

    public static String hmacSha1(String data) {
        return hmacSha1(data, KEY);
    }
    //the name of the secret-key algorithm to be associated with the given key material
    private static String hmacSha1(String data, String key)

    {
       //Constructs a secret key from the given byte array.
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
       //This class provides the functionality of a "Message Authentication Code" (MAC) algorithm.
        Mac mac = null;
        try {
            mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            //initializes this Mac object with the given key
            mac.init(signingKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        //Processes the given array of bytes and finishes the MAC operation.
        return toHexString(mac.doFinal(data.getBytes()));
    }


    private static void generateKey() {
        String data = "cauliflower";
        String key = "secret";
        KEY = hmacSha1(data, key);
    }

    private static String toHexString(byte[] bytes) {
        int zero=0;
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format(CONVERSION_FRORMAT, b);
        }
        StringBuffer buffer = new StringBuffer(formatter.toString());
        return buffer.substring(zero, MAX_STRING_LENGTH).toString();
    }


    public static void main(String[] args) {
        System.out.println(Cryptographer.hmacSha1("123456"));
    }
}

