package hu.uniobuda.nik.parentalcontrol;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_nonfree;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class CheckService extends Service {

    MonitorlogThread mt = new MonitorlogThread();
    //static Context context;
    // SharedPreferences sh;
    boolean frontCamera;
    boolean urlEnabled;
    BroadcastReceiver lock;
    BroadcastReceiver unlock;
    int apiLevel;


    // public static Context getContext() {
    //    return context;
    //}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final SharedPreferences settings = getSharedPreferences(getString(R.string.SHAREDPREFERENCE_SETTINGS), Context.MODE_PRIVATE);
        SharedPreferences persons = getSharedPreferences(getString(R.string.SHAREDPREFERENCE_PERSONS), Context.MODE_PRIVATE);
        frontCamera = settings.getBoolean(getString
                (R.string.SHAREDPREFERENCE_FACE_REG_ENABLED), false);
        urlEnabled = settings.getBoolean(getString(R.string.SHAREDPREFERENCE_URL_ENABLED), false);

        apiLevel = Build.VERSION.SDK_INT;

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
        notification.setContentTitle(getString(R.string.serviceTitle));
        notification.setContentText(getString(R.string.serviceMessage));
        notification.setSmallIcon(R.mipmap.ic_launcher);

        if (apiLevel < Build.VERSION_CODES.HONEYCOMB) {
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, MainScreenActivity.class), 0);
            notification.setContentIntent(contentIntent);
        }

        startForeground(1122, notification.build());

        if (urlEnabled) {
            IPTablesAPI.blockAllURL(CheckService.this);
        }

        if (!persons.getAll().isEmpty()) {
            new Thread() {
                @Override
                public void run() {
                    Loader.load(opencv_nonfree.class);
                }
            }.start();
        }

        lock = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Log.d("servicebroadcast", intent.getAction());
                mt.interrupt();
            }
        };

        unlock = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Log.d("servicebroadcast", intent.getAction());
                if (settings.getBoolean(getString(R.string.SHAREDPREFERENCE_ACCESS_CONTROL_ENABLED), false)) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mt = new MonitorlogThread();
                            mt.start();
                        }
                    }, 3000);
                } else {
                    mt = new MonitorlogThread();
                    mt.start();
                }
            }
        };
        registerReceiver(lock, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        registerReceiver(unlock, new IntentFilter(Intent.ACTION_USER_PRESENT));

        BlockerHashTable.refresh(CheckService.this);
        mt.start();
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
        unregisterReceiver(lock);
        unregisterReceiver(unlock);
        if (urlEnabled) {
            IPTablesAPI.unblockAllURL(CheckService.this);
        }
        super.onDestroy();
    }

    private class MonitorlogThread extends Thread {

        private String previousPackage = "";

        @Override
        public void run() {
            while (!this.isInterrupted()) {

                try {
                    Thread.sleep(200);
                    String foregroundTaskPackageName;
                    //Log.d("Apilevel",apiLevel+"");
                    // if (apiLevel < Build.VERSION_CODES.LOLLIPOP) {
                    foregroundTaskPackageName = getPackageNameOldApi();
                    // Log.d("Service","OldApi");
                    //} else {
                    //Log.d("Service","NewApi");
                    //  foregroundTaskPackageName = getPackageNameNewApi();
                    // }

                    if (!(foregroundTaskPackageName.equals(previousPackage))
                            && !previousPackage.equals("")) {
                        Log.d("BHT_EMPTY", Boolean.toString(BlockerHashTable.isEmpty()));
                        if (BlockerHashTable.isEmpty()) {
                            BlockerHashTable.refresh(CheckService.this);

                        }
                        Intent packageChanged = new Intent();
                        packageChanged
                                .setAction(getString(R.string.BROADCAST_NEW_APP_STARTED));
                        packageChanged.putExtra(getString(R.string.EXTRA_PACKAGE_NAME),
                                foregroundTaskPackageName);
                        packageChanged.putExtra(getString(R.string.EXTRA_FACE_REG_ENABLED), frontCamera);
                        sendBroadcast(packageChanged);
                        Log.d("Elso", foregroundTaskPackageName);
                    }

                    previousPackage = foregroundTaskPackageName;

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        private String getPackageNameOldApi() {
            ActivityManager am = (ActivityManager) getBaseContext()
                    .getSystemService(ACTIVITY_SERVICE);
            RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1)
                    .get(0);
            return foregroundTaskInfo.topActivity
                    .getPackageName();
        }
        /*private String getPackageNameNewApi() {
            UsageStatsManager usm = (UsageStatsManager) getSystemService("usagestats");
            long time = System.currentTimeMillis();
            String packageName = "";
            // We get usage stats for the last 1 seconds
            List<UsageStats> stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000, time);
            // Sort the stats by the last time used
            if (stats != null) {
                SortedMap<Long, UsageStats> sortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : stats) {
                    sortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (!sortedMap.isEmpty()) {
                    packageName = sortedMap.get(sortedMap.lastKey()).getPackageName();
                }
            }
            Log.d("newapi", packageName);
            return packageName;
        }*/
    }
}
