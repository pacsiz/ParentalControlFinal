package hu.uniobuda.nik.parentalcontrol;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Iterator;
import java.util.Map;

public class CheckService extends Service{

    MonitorlogThread mt = new MonitorlogThread();
    static Context context;
    SharedPreferences sh;
    boolean frontCamera;


    public static Context getContext() {
        return context;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getBaseContext();

        sh = getSharedPreferences(getString(R.string.SHAREDPREFERENCE_SETTINGS), Context.MODE_PRIVATE);
        frontCamera = sh.getBoolean(getString
                (R.string.SHAREDPREFERENCE_FACE_REG_ENABLED), false);
        StartT();
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
        notification.setContentTitle(getString(R.string.serviceTitle));
        notification.setContentText(getString(R.string.serviceMessage));
        notification.setSmallIcon(R.mipmap.ic_launcher);
        startForeground(1122, notification.build());
        if(sh.getBoolean(getString(R.string.SHAREDPREFERENCE_URL_ENABLED),false))
        {
          IPTablesAPI.blockAllURL(CheckService.this);
        }

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
                    Thread.sleep(200);

                    ActivityManager am = (ActivityManager) getBaseContext()
                            .getSystemService(ACTIVITY_SERVICE);
                    RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1)
                            .get(0);

                    String foregroundTaskPackageName = foregroundTaskInfo.topActivity
                            .getPackageName();

                    if (!(foregroundTaskPackageName.equals(previousPackage))
                            && previousPackage != "") {
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
