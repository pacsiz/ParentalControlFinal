package hu.uniobuda.nik.parentalcontrol;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class PasswordCreator {

    private static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String createPassword(String toPassword) {
        String hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = toPassword.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            bytes = digest.digest();

            hash = bytesToHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return hash;
    }

    public static void generateNewPassword(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        SharedPreferences pwSh = context.getSharedPreferences(context.getString(R.string.SHAREDPREFERENCE_SETTINGS),Context.MODE_PRIVATE);

        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net != null && net.isConnectedOrConnecting()) {
            SharedPreferences.Editor e = pwSh.edit();
            String newPw = randomPassword();
            e.putString(context.getString(R.string.SHAREDPREFERENCE_PASSWORD), PasswordCreator.createPassword(newPw));
            e.commit();
            String toAddress = pwSh.getString(context.getString(R.string.SHAREDPREFERENCE_EMAIL), "");
            //Log.d("PasswordCreator", "Send new password to: "+toAddress);
            String fromAddress = context.getString(R.string.email);

            String fromPassword = context.getString(R.string.email_password);
            String subject = context.getString(R.string.subject);
            String text = context.getString(R.string.email_text);

            EmailSender sender = new EmailSender(fromAddress, fromPassword, toAddress, subject, text + newPw);
            sender.sendEmail();
            Toast.makeText(context, R.string.email_sent, Toast.LENGTH_LONG).show();
        } else {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            wifi.setWifiEnabled(true);
            Toast.makeText(context, R.string.email_send_fail, Toast.LENGTH_LONG).show();
        }
    }

    private static String randomPassword() {
        Random rnd = new Random();
        StringBuilder builder = new StringBuilder();
        char c;
        for (int i = 0; i < 6; i++) {
            c = (char) (rnd.nextInt(25) + 97);
            builder.append(c);
        }
        //Log.d("PasswordCreator","Created random password: "+builder.toString());
        return builder.toString();
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
