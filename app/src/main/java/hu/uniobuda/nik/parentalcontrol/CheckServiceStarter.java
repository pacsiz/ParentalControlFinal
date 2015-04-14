package hu.uniobuda.nik.parentalcontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class CheckServiceStarter extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        SharedPreferences sh = context.getSharedPreferences(context.getString(R.string.SHAREDPREFERENCE_SETTINGS), Context.MODE_PRIVATE);
        Log.d("Broadcast received", intent.getAction());
        if (!ServiceInfo.isServiceRunning(CheckService.class, context)) {
            context.startService(new Intent(context, CheckService.class));
            Log.d("StartService", intent.getAction());
        }
    }
}