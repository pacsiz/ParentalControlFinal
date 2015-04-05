package hu.uniobuda.nik.parentalcontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CheckServiceStarter extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        Log.d("Broadcast received", intent.getAction());
        if (!ServiceInfo.isServiceRunning(CheckService.class, context)) {
            context.startService(new Intent(context, CheckService.class));
            Log.d("StartService", intent.getAction());
        }
    }
}