package hu.uniobuda.nik.parentalcontrol;


import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class DevAdminReceiver extends DeviceAdminReceiver{

    @Override
    public void onEnabled(Context context, Intent intent) {
        Log.d("Devadminreceiver", "enabled");
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        Intent i = new Intent(context,
                CheckService.class);
        context.stopService(i);
        Toast.makeText(context,R.string.adminFailure,Toast.LENGTH_LONG).show();
        super.onDisabled(context, intent);


    }
}
