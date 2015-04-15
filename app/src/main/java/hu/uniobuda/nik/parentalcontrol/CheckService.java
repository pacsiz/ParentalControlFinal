package hu.uniobuda.nik.parentalcontrol;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_nonfree;

import java.util.Iterator;
import java.util.Map;

public class CheckService extends Service {

    MonitorlogThread mt = new MonitorlogThread();
    static Context context;
    SharedPreferences sh;
    boolean frontCamera;
    BroadcastReceiver screenOff;
    BroadcastReceiver screenOn;


    public static Context getContext() {
        return context;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       // context = getBaseContext();
        //((KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE)).newKeyguardLock("IN").disableKeyguard();

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        sh = getSharedPreferences(getString(R.string.SHAREDPREFERENCE_SETTINGS), Context.MODE_PRIVATE);
        frontCamera = sh.getBoolean(getString
                (R.string.SHAREDPREFERENCE_FACE_REG_ENABLED), false);
        StartT();
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
        notification.setContentTitle(getString(R.string.serviceTitle));
        notification.setContentText(getString(R.string.serviceMessage));
        notification.setSmallIcon(R.mipmap.ic_launcher);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainScreenActivity.class), 0);

       notification.setContentIntent(contentIntent);
        startForeground(1122, notification.build());
        if (sh.getBoolean(getString(R.string.SHAREDPREFERENCE_URL_ENABLED), false)) {
            IPTablesAPI.blockAllURL(CheckService.this);
        }

        if (frontCamera) {
            new Thread() {
                @Override
                public void run() {
                    Loader.load(opencv_nonfree.class);
                }
            }.start();
        }

        screenOff = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("servicebroadcast", intent.getAction());
                mt.interrupt();
            }
        };

        screenOn = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("servicebroadcast", intent.getAction());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mt = new MonitorlogThread();
                        mt.start();
                    }
                }, 3000);
            }
        };

        registerReceiver(screenOff, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        registerReceiver(screenOn, new IntentFilter(Intent.ACTION_USER_PRESENT));
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy() {
        mt.interrupt();
        unregisterReceiver(screenOff);
        unregisterReceiver(screenOn);
        IPTablesAPI.unblockAllURL(CheckService.this);
        super.onDestroy();
    }

    private void StartT() {
        mt.start();
    }

    private class MonitorlogThread extends Thread {

        private String previousPackage = "";

        @Override
        public void run() {
            while (!this.isInterrupted()) {

                try {
                    Thread.sleep(100);

                    ActivityManager am = (ActivityManager) getBaseContext()
                            .getSystemService(ACTIVITY_SERVICE);
                    RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1)
                            .get(0);

                    String foregroundTaskPackageName = foregroundTaskInfo.topActivity
                            .getPackageName();

                    if (!(foregroundTaskPackageName.equals(previousPackage))
                            && !previousPackage.equals("")) {
                        Log.d("BHT_EMPTY", Boolean.toString(BlockerHashTable.isEmpty()));
                        if (BlockerHashTable.isEmpty()) {
                            BlockerHashTable.refresh(CheckService.this);
                            /*packages = getSharedPreferences(getString
                                    (R.string.SHAREDPREFERENCE_PACKAGES), Context.MODE_PRIVATE);
                            Map<String, ?> map = packages.getAll();
                            Iterator i = map.entrySet().iterator();
                            while (i.hasNext()) {
                                Map.Entry entry = (Map.Entry) i.next();
                                Log.d("BHT_ADD",entry.getKey().toString());
                                BlockerHashTable.setBoolean(entry.getKey().toString(), true);
                            }*/
                        }
                        Intent packageChanged = new Intent();
                        packageChanged
                                .setAction(getString(R.string.BROADCAST_NEW_APP_STARTED));
                        packageChanged.putExtra(getString(R.string.EXTRA_PACKAGE_NAME),
                                foregroundTaskPackageName);
                        packageChanged.putExtra(getString(R.string.EXTRA_FACE_REG_ENABLED), frontCamera);
                        sendBroadcast(packageChanged);
                        Log.d("Elso", foregroundTaskPackageName);
                        // Log.d("Masodik",previousPackage);
                    }

                    previousPackage = foregroundTaskPackageName;

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
}
