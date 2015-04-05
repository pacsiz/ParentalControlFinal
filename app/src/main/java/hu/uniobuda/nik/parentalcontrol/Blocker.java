package hu.uniobuda.nik.parentalcontrol;

import java.util.Hashtable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Blocker extends BroadcastReceiver {

    private static String pName;
    public static String pName2;
    private boolean faceRegEnabled;

    @Override
    public void onReceive(Context context, Intent intent) {
        pName = intent.getStringExtra(context.getResources().getString
                (R.string.EXTRA_PACKAGE_NAME));
        faceRegEnabled = intent.getBooleanExtra(context.getResources().
                getString(R.string.EXTRA_FACE_REG_ENABLED), false);
        Log.d("OnReceivePN", pName);
        if (BlockerHashTable.containsBoolean(pName)) {
            Log.d("OnreceivePN", "if-blockornot");
            pName2 = pName;
            blockOrNot(context);
        }
    }

    private void blockOrNot(Context context) {
        // Log.d("blockornot",pName);
        if (BlockerHashTable.containsBoolean(pName)
                && BlockerHashTable.getBoolean(pName) == false) {
            Log.d("blockornot", "if-ben");
            Log.d("hash",
                    ""
                            + BlockerHashTable.containsBoolean(pName));
            BlockerHashTable.setBoolean(pName, true);
        } else {
            Intent i;
            if (faceRegEnabled) {
                Log.d("blockornot", "BLOKK");
                i = new Intent(CheckService.getContext(),
                        CheckPersonActivity.class);
            } else {
                i = new Intent(CheckService.getContext(),
                        PasswordRequestActivity.class);
            }
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(context.getResources().getString
                    (R.string.EXTRA_PACKAGE_NAME), pName);
            CheckService.getContext().startActivity(i);


        }
    }
}
