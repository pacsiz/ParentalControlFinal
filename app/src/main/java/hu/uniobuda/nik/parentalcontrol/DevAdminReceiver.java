package hu.uniobuda.nik.parentalcontrol;


import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class DevAdminReceiver extends DeviceAdminReceiver{

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        Log.d("Devadminreceiver", "enabled");
        Intent i = new Intent(context,PasswordRequestActivity.class);
       // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //i.putExtra(context.getString(R.string.EXTRA_DISABLE_REQUEST),true);
        intent.setClass(context,PasswordRequestActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(DeviceAdminReceiver.EXTRA_DISABLE_WARNING,true);
        context.startActivity(intent);
        intent.putExtra(DeviceAdminReceiver.EXTRA_DISABLE_WARNING,"cacaca");
        return "dfsf";

    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        Log.d("Devadminreceiver", "enabled");
    }
}
