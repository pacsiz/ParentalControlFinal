package hu.uniobuda.nik.parentalcontrol.identification;

import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import hu.uniobuda.nik.parentalcontrol.service.CheckService;
import hu.uniobuda.nik.parentalcontrol.R;
import hu.uniobuda.nik.parentalcontrol.service.ServiceInfo;

public class Blocker extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String packageName = intent.getStringExtra(context.getResources().getString(R.string.EXTRA_PACKAGE_NAME));
        SharedPreferences persons = context.getSharedPreferences(context.getString(R.string.SHAREDPREFERENCE_PERSONS), Context.MODE_PRIVATE);
        SharedPreferences sh = context.getSharedPreferences(context.getString(R.string.SHAREDPREFERENCE_SETTINGS), Context.MODE_PRIVATE);
        Map map = persons.getAll();
        boolean faceRecEnabled = sh.getBoolean(context.getString(R.string.SHAREDPREFERENCE_FACE_REG_ENABLED), false);
        Log.d("Blocker", "brodacastReceived: "+intent.getAction());
        if (intent.getAction().equals(context.getString(R.string.BROADCAST_UNLOCK))) {
            if (sh.getBoolean(context.getString(R.string.SHAREDPREFERENCE_ACCESS_CONTROL_ENABLED), false) && ServiceInfo.isServiceRunning(CheckService.class, context)) {

                openActivity(context, null, map, faceRecEnabled);
            }
        } else if(intent.getAction().equals(context.getString(R.string.BROADCAST_NEW_APP_STARTED))) {
            if (BlockerHashTable.isEmpty()) {
                BlockerHashTable.refresh(context);
            }
            if (BlockerHashTable.containsBoolean(packageName)) {
                if (!BlockerHashTable.getBoolean(packageName)) {
                    Log.d("Blocker", "app start once allowed");
                    BlockerHashTable.setBoolean(packageName, true);
                } else {
                    openActivity(context, packageName, map, faceRecEnabled);
                }
            }
        }
    }

    private void openActivity(Context context, String packageName, Map persons, boolean faceRecEnabled){
        Intent i;
        if (faceRecEnabled && !persons.isEmpty()) {
            Log.d("Blocker", "block with face detection");
            i = new Intent(context,
                    CheckPersonActivity.class);
        } else {
            Log.d("Blocker", "block with password request");
            i = new Intent(context,
                    PasswordRequestActivity.class);
        }
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(context.getResources().getString(R.string.EXTRA_PACKAGE_NAME), packageName);
        context.startActivity(i);
    }
}
