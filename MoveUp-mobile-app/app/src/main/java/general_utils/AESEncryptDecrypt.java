package general_utils;


import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryptDecrypt {

    String iv="WrWReisa4AumamDY";//"WrWReisa4AumamDY";
    String key = "qpFccUqsN3McuSu9eK9jAv0Tcv4YvFLx";
    //z1mI3ksTHRvPwkKd/zOTnQ==
    public static void main(String args[]) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException {

        //String msg = "mehdimehdikerkarmehdimehdikerkar";//"YbK8Q0kM3X7JqqkLRoOYjNDBcFQWMvDE";
        AESEncryptDecrypt aesEncryptDecrypt = new AESEncryptDecrypt();
        //String enc = aesEncryptDecrypt.AESencrypt(msg);
        //System.out.println(enc);
        String enc = "gvvtgCLnFLfryNsZOtSQMLzkCdIelU9e31dWBty3T48=";
        String dec = aesEncryptDecrypt.AESdecrypt(enc );
        System.out.println(dec);


    }

    public String AESencrypt(String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

        int blockSize = cipher.getBlockSize();
        byte[] dataBytes = data.getBytes();
        int plaintextLength = dataBytes.length;
        if (plaintextLength % blockSize != 0) {
            plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
        }

        byte[] plaintext = new byte[plaintextLength];
        System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
        byte[] encrypted = cipher.doFinal(plaintext);

        return (Base64.encode(encrypted));
    }

    public String AESdecrypt(String encrypted) throws InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException {

        SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

        byte[] b64encrypted = Base64.decode(encrypted);
        System.out.println("B64e(b64enc): "+Base64.encode(b64encrypted));

        cipher.init(Cipher.DECRYPT_MODE, secretKey,ivspec);
        return (new String(cipher.doFinal(b64encrypted)));
    }

}