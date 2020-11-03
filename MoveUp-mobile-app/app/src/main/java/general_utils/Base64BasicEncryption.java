package general_utils;

import android.util.Base64;

public class Base64BasicEncryption {

    public static void main(String[] args) {

        String str = "mehdi";
        String strr = "H1QHKvnbNyKJ3DdqBiw4zg==";
        System.out.println(""+Base64.decode(str.getBytes(),Base64.DEFAULT));
        //System.out.println("encodedBytes " +encodeB64(str));
        //System.out.println("decodedBytes " +decodeB64(encodeB64(str)));
    }

    public static String encodeB64(String str){
        byte[] b64= Base64.encode(str.getBytes(),Base64.DEFAULT);
        return new String(b64);
    }
    public static String decodeB64(String b64){
        byte[] str = Base64.decode(b64,Base64.DEFAULT);
        return new String(str);
    }

}