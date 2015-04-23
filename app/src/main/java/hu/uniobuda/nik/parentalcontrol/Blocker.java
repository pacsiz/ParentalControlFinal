package hu.uniobuda.nik.parentalcontrol;

import java.util.Hashtable;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.WindowManager;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_nonfree;

public class Blocker extends BroadcastReceiver {

    //public static String pName2;

    @Override
    public void onReceive(Context context, Intent intent) {


        String packageName = intent.getStringExtra(context.getResources().getString(R.string.EXTRA_PACKAGE_NAME));
        SharedPreferences persons = context.getSharedPreferences(context.getString(R.string.SHAREDPREFERENCE_PERSONS), Context.MODE_PRIVATE);
        SharedPreferences sh = context.getSharedPreferences(context.getString(R.string.SHAREDPREFERENCE_SETTINGS), Context.MODE_PRIVATE);
        Map map = persons.getAll();
        boolean faceRecEnabled = sh.getBoolean(context.getString(R.string.SHAREDPREFERENCE_FACE_REG_ENABLED),false);
        Log.d("blockerbroadcast", intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {

            Log.d("sh_access_control", faceRecEnabled + "");
            if (sh.getBoolean(context.getString(R.string.SHAREDPREFERENCE_ACCESS_CONTROL_ENABLED), false) && ServiceInfo.isServiceRunning(CheckService.class, context)) {
                Intent i;

               if (faceRecEnabled && !map.isEmpty()) {
                    i = new Intent(context,
                            CheckPersonActivity.class);

                } else {
                    i = new Intent(context,
                            PasswordRequestActivity.class);
                }
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //i.putExtra(context.getResources().getString
                        //(R.string.EXTRA_PACKAGE_NAME), packageName);
                i.putExtra(context.getResources().getString(R.string.EXTRA_ACCESS_CONTROL),true);
                context.startActivity(i);
            }
        }

       // else if(intent.getAction().equals(context.getString(R.string.BROADCAST_REFRESH_MAIN_HASHTABLE)))
        //{
          //  BlockerHashTable.refresh(context);
       // }
        else
        {
            if (BlockerHashTable.isEmpty())
            {
                BlockerHashTable.refresh(context);
            }
            blockOrNot(context, packageName, faceRecEnabled, map);
        }
    }

    private void blockOrNot(Context context, String packageName, boolean faceRecEnabled, Map map) {
        Log.d("blockornot", packageName);
        Log.d("contains", BlockerHashTable.containsBoolean(packageName)+"");
        if (BlockerHashTable.containsBoolean(packageName)) {
            if (!BlockerHashTable.getBoolean(packageName)) {
                Log.d("blockornot", "if-ben");
                Log.d("hash", "" + BlockerHashTable.containsBoolean(packageName));
                BlockerHashTable.setBoolean(packageName, true);
            } else {
                Intent i;
                if (faceRecEnabled && !map.isEmpty()) {
                    Log.d("blockornot", "FACEON");
                    i = new Intent(context,
                            CheckPersonActivity.class);
                } else {
                    Log.d("blockornot", "FACEOFF");
                    i = new Intent(context,
                            PasswordRequestActivity.class);
                }
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra(context.getResources().getString
                        (R.string.EXTRA_PACKAGE_NAME), packageName);
                context.startActivity(i);
                Log.d("blockornot", "activitystarted");
                //CheckService.getContext().startActivity(i);
            }
        }
    }

}
