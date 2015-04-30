package hu.uniobuda.nik.parentalcontrol.backend;


import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import hu.uniobuda.nik.parentalcontrol.R;
import hu.uniobuda.nik.parentalcontrol.service.CheckService;

public class DevAdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        //Log.d("DevAdminReceiver", ""Device admin enabled");
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        Intent i = new Intent(context,
                CheckService.class);
        context.stopService(i);
        Toast.makeText(context, R.string.adminFailure, Toast.LENGTH_LONG).show();
        super.onDisabled(context, intent);
    }
}
