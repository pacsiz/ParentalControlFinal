package hu.uniobuda.nik.parentalcontrol;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class PasswordCreator {

    private static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String createPassword(String toPassword)
    {
        String hash = null;
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = toPassword.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            bytes = digest.digest();

            hash = bytesToHex( bytes );
        }
        catch( NoSuchAlgorithmException e )
        {
            e.printStackTrace();
        }
        catch( UnsupportedEncodingException e )
        {
            e.printStackTrace();
        }
        return hash;
    }


    public static String randomPassword()
    {
        Random rnd = new Random();
        StringBuilder builder = new StringBuilder();
        //int randomLength = rnd.nextInt(6);
        char c;
        for (int i = 0; i < 6; i++){
            c = (char) (rnd.nextInt(25) + 97);
            builder.append(c);
        }
        Log.d("newpw",builder.toString());
        return builder.toString();
    }

    private static String bytesToHex( byte[] bytes )
    {
        char[] hexChars = new char[ bytes.length * 2 ];
        for( int j = 0; j < bytes.length; j++ )
        {
            int v = bytes[ j ] & 0xFF;
            hexChars[ j * 2 ] = hexArray[ v >>> 4 ];
            hexChars[ j * 2 + 1 ] = hexArray[ v & 0x0F ];
        }
        return new String( hexChars );
    }
}
