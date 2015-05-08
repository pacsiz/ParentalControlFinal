package hu.uniobuda.nik.parentalcontrol.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import hu.uniobuda.nik.parentalcontrol.service.CheckService;
import hu.uniobuda.nik.parentalcontrol.service.ServiceInfo;

public class CheckServiceStarter extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        context.startActivity(intent); //csak a CheckServiceStarterTest miatt
        /*if (!ServiceInfo.isServiceRunning(CheckService.class, context)) {
            context.startService(new Intent(context, CheckService.class));
            //Log.d("CheckServiceStarter", "Start service");
        }*/
    }
}