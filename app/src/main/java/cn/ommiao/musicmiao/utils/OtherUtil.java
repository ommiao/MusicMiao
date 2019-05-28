package cn.ommiao.musicmiao.utils;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class OtherUtil {

    public static String getSecret(String origin){
        return getSecret("f%H,vL;7w`k2!^8f", origin);
    }

    public static String getSecret(String pattern, String origin){
        try {
            Cipher localCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            localCipher.init(1, new SecretKeySpec(origin.getBytes(), "AES"), new IvParameterSpec("5620321569874206".getBytes()));
            String result = new String(Base64.encode(localCipher.doFinal(origin.getBytes()), Base64.DEFAULT));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
