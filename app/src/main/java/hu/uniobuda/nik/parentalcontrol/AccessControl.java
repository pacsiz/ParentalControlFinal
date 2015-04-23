package hu.uniobuda.nik.parentalcontrol;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import android.os.Process;

import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AccessControl {

    public static boolean accessControl(String childName, Context context) {
        SharedPreferences childSettings = context.getSharedPreferences(childName.toLowerCase(), Context.MODE_PRIVATE);
        boolean accessControlEnabled = childSettings.getBoolean(context.getString(R.string.SHAREDPREFERENCE_ACCESS_CONTROL_FOR_PERSON), false);
        Log.d("accContEnForChild", accessControlEnabled + "");
        if (!childSettings.getAll().isEmpty() && accessControlEnabled) {
            Calendar time = new GregorianCalendar();
            int hour = time.get(Calendar.HOUR_OF_DAY);
            int min = time.get(Calendar.MINUTE);
            int day = time.get(Calendar.DAY_OF_WEEK);
            String weekdays[] = new DateFormatSymbols(Locale.ENGLISH).getWeekdays();
            String[] selectedDays = childSettings.getString(context.getString(R.string.SHAREDPREFERENCE_SELECTED_DAYS), "").split(":");
            Log.d("weekday", weekdays[day].toLowerCase());
            //Log.d("weekday", selectedDays[day]);
            if (Arrays.asList(selectedDays).contains(weekdays[day].toLowerCase())) {
                String[] fromTime = childSettings.getString(context.getString(R.string.SHAREDPREFERENCE_TIME_FROM), "").split(":");
                String[] toTime = childSettings.getString(context.getString(R.string.SHAREDPREFERENCE_TIME_TO), "").split(":");
                Log.d("fromHour", fromTime[0]);
                Log.d("fromMin", fromTime[1]);
                Log.d("toHour", toTime[0]);
                Log.d("toTIme", toTime[1]);
                int fromHour = Integer.parseInt(fromTime[0]);
                int fromMin = Integer.parseInt(fromTime[1]);
                int toHour = Integer.parseInt(toTime[0]);
                int toMin = Integer.parseInt(toTime[1]);

                int compareFromTime = (fromHour * 60) + fromMin;
                int compareToTime = (toHour * 60) + toMin;
                int currentTime = (hour * 60) + min;
                Log.d("compareFromTime",compareFromTime+"");
                Log.d("compareToTime",compareToTime+"");
                Log.d("currentTime",currentTime+"");

                if (fromHour > toHour) //átnyúlik másik napba
                {
                    if ((compareFromTime >= currentTime && compareToTime <= currentTime)) {
                        Log.d("From>to","true");
                        return true;
                    } else {
                        Log.d("From>to","false");
                        return false;
                    }
                } else {
                    if (compareFromTime <= currentTime && compareToTime >= currentTime) {
                        Log.d("From<to","false");
                        return false;
                    } else {
                        Log.d("From<to","true");
                        return true;
                    }
                }

            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static void allow(Context context, String personName, String packageName) {
        String str = context.getString(R.string.accessAllowed);
        Toast.makeText(context, str + personName.substring(0, 1).toUpperCase() +
                personName.substring(1), Toast.LENGTH_LONG).show();

        if (packageName != null && !packageName.equals(("hu.uniobuda.nik.parentalcontrol"))) {
            BlockerHashTable.setBoolean(packageName, false);
        }
        //playSound(R.raw.ok, context);
    }

    public static void block(Context context, String packageName) {

        Intent i = new Intent("android.intent.action.MAIN");
        i.addCategory("android.intent.category.HOME");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        if (packageName != null && !packageName.equals(("hu.uniobuda.nik.parentalcontrol")))
        {
            BlockerHashTable.setBoolean(packageName, true);
        }
        //playSound(R.raw.fail, context);
    }

    public static void deny(Context context, String personName, String packageName) {
        String str = context.getString(R.string.childDenied);
        Toast.makeText(context, str + personName,
                Toast.LENGTH_LONG).show();
        block(context, packageName);
    }


    public static void personCheck(Context context,String personName, String packageName)
    {
        Log.d("personname", personName);
        Log.d("packageNaem", packageName);
        SharedPreferences apps = context.getSharedPreferences(context.getString(R.string.SHAREDPREFERENCE_PACKAGES),Context.MODE_PRIVATE);
        Map<String, ?> map = apps.getAll();
        String blockedPerson = map.get(packageName).toString();
        if(blockedPerson.contains(personName) || blockedPerson.contains("all"))
        {
            deny(context,personName,packageName);
        }
        else
        {
            allow(context,personName,packageName);
        }
    }

    public static void lock(Context context) {
        //playSound(R.raw.fail, context);
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        dpm.lockNow();
    }

    public static void playSound(int soundId, Context context) {

        MediaPlayer mp = MediaPlayer.create(context, soundId);
        mp.start();
        /*SoundPool soundPool;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        }

        final int id = soundPool.load(context,soundId,1);

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i2) {
                soundPool.play(id,0.7f,0.7f,1,0,1f);
            }
        });*/
    }


}
