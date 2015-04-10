package hu.uniobuda.nik.parentalcontrol;

import java.util.Hashtable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Blocker extends BroadcastReceiver {

    //public static String pName2;

    @Override
    public void onReceive(Context context, Intent intent) {

        blockOrNot(context,
                intent.getStringExtra(context.getResources().getString(R.string.EXTRA_PACKAGE_NAME)),
                intent.getBooleanExtra(context.getResources().getString(R.string.EXTRA_FACE_REG_ENABLED), false));
      /*  switch (intent.getAction())
        {
            case "hu.uniobuda.nik.parentalcontrol.REFRESH_HASHTABLE":
                BlockerHashTable.refresh(context);
                Log.d("OnReceive", "hashrefresh");
                break;

            case "hu.uniobuda.nik.parentalcontrol.SET_PACKAGE_FALSE":
                BlockerHashTable.setBoolean(intent.getStringExtra
                        (context.getString(R.string.EXTRA_PACKAGE_NAME)),false);
                Log.d("OnReceive", "set_package_false");
                break;
            case "hu.uniobuda.nik.parentalcontrol.NEW_APP_STARTED":

                Log.d("OnReceive", "newappstarted");
                break;
        }
*/

        /*if (intent.getAction().equals("hu.uniobuda.nik.parentalcontrol.REFRESH_HASHTABLE")) {
            BlockerHashTable.refresh(context);
            Log.d("OnReceive", "hashrefresh");
        } else {
            pName = intent.getStringExtra(context.getResources().getString
                    (R.string.EXTRA_PACKAGE_NAME));
            faceRegEnabled = intent.getBooleanExtra(context.getResources().
                    getString(R.string.EXTRA_FACE_REG_ENABLED), false);
            Log.d("OnReceivePN", Boolean.toString(BlockerHashTable.isEmpty()));

            if (BlockerHashTable.containsBoolean(pName)) {
                Log.d("OnreceivePN", "if-blockornot");
                //pName2 = packageName;
                blockOrNot(context);
            }
        }*/
    }

    private void blockOrNot(Context context, String packageName, boolean faceRegEnabled) {
        Log.d("blockornot", "blockornot");
        if (BlockerHashTable.containsBoolean(packageName))
        {
            if (BlockerHashTable.getBoolean(packageName) == false) {
                Log.d("blockornot", "if-ben");
                Log.d("hash",""+ BlockerHashTable.containsBoolean(packageName));
                BlockerHashTable.setBoolean(packageName, true);
            } else {
                Intent i;
                if (faceRegEnabled) {
                    Log.d("blockornot", "FACEON");
                    i = new Intent(CheckService.getContext(),
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
