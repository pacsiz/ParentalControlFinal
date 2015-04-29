package hu.uniobuda.nik.parentalcontrol;

import android.annotation.TargetApi;
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
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;

public class CheckService extends Service {

    MonitorlogThread mt = new MonitorlogThread();
    boolean frontCamera;
    boolean urlEnabled;
    BroadcastReceiver lock;
    BroadcastReceiver unlock;
    BroadcastReceiver refreshHashTable;
    int apiLevel;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("service started", "");
        final SharedPreferences settings = getSharedPreferences(getString(R.string.SHAREDPREFERENCE_SETTINGS), Context.MODE_PRIVATE);
        SharedPreferences apps = getSharedPreferences(getString(R.string.SHAREDPREFERENCE_PACKAGES), Context.MODE_PRIVATE);
        Map map = apps.getAll();
        if (map.isEmpty()) {
            Editor e = apps.edit();
            e.putString("hu.uniobuda.nik.parentalcontrol", "all");
            e.putString("com.android.settings", "all");
            e.putString("com.android.packageinstaller", "all");
            e.commit();
        }
        // TODO FrontCamera itt felesleges, mindig adott helyen lekérem
        //frontCamera = settings.getBoolean(getString
        //(R.string.SHAREDPREFERENCE_FACE_REG_ENABLED), false);
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

        lock = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("CheckServiceBroadcast", "lock: "+intent.getAction());
                mt.interrupt();

            }
        };

        unlock = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("CheckServiceBroadcast", "unlock: "+intent.getAction());
                if(!mt.isAlive())
                {
                    mt = new MonitorlogThread();
                    mt.start();
                }
                Intent i = new Intent();
                i.setAction(getString(R.string.BROADCAST_UNLOCK));
                sendBroadcast(i);
            }
        };

        registerReceiver(lock, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        registerReceiver(unlock, new IntentFilter(Intent.ACTION_SCREEN_ON));

        mt.start();

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {

        return null;
    }

    @Override
    public void onDestroy() {
        Log.d("CheckService", "ONDESTROY");
        mt.interrupt();
        unregisterReceiver(lock);
        unregisterReceiver(unlock);
        if (urlEnabled) {
            IPTablesAPI.unblockAllURL(CheckService.this);
        }
        super.onDestroy();
    }

    private class MonitorlogThread extends Thread {

        private volatile String previousPackage = "";

        @Override
        public void run() {
            Log.e("thread elinditva", "");
            while (!this.isInterrupted()) {
                try {
                    String foregroundTaskPackageName;
                    String className = "";
                    //Log.d("CheckService","apilevel: "+apiLevel);
                    if (apiLevel < Build.VERSION_CODES.LOLLIPOP) {
                        String[] array = getPackageNameOldApi();
                        foregroundTaskPackageName = array[0];
                        className = array[1];
                        //Log.d("CheckService","packageName: "+foregroundTaskPackageName);
                    } else {
                        //Log.d("CheckService","getPackageNameOldApi");
                        foregroundTaskPackageName = getPackageNameNewApi();
                    }
                    //Log.d("CheckService", "foregroundTaskPackageName BEFORE: " + foregroundTaskPackageName);
                    //Log.d("CheckService", "previousPackage BEFORE: " + previousPackage);
                    if (!(foregroundTaskPackageName.equals(previousPackage))
                            && !previousPackage.equals("") && (!className.equals(".PasswordRequestActivity"))) {
                        Log.d("CheckService", "classname: " + className);

                        Intent packageChanged = new Intent();
                        packageChanged
                                .setAction(getString(R.string.BROADCAST_NEW_APP_STARTED));
                        packageChanged.putExtra(getString(R.string.EXTRA_PACKAGE_NAME),
                                foregroundTaskPackageName);
                        //packageChanged.putExtra(getString(R.string.EXTRA_FACE_REG_ENABLED), frontCamera);
                        sendBroadcast(packageChanged);
                        Log.d("CheckService", "foregroundTaskPackageName IF: " + foregroundTaskPackageName);
                        Log.d("CheckService", "previousPackage IF: " + previousPackage);
                    }
                    previousPackage = foregroundTaskPackageName;
                    Thread.sleep(100);

                } catch (InterruptedException e) {
                    break;
                }
            }
            Thread.currentThread().interrupt();
            return;
        }

        private String[] getPackageNameOldApi() {
            ActivityManager am = (ActivityManager) getBaseContext()
                    .getSystemService(ACTIVITY_SERVICE);
            RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1)
                    .get(0);
            String[] array = new String[2];
            array[0] = foregroundTaskInfo.topActivity
                    .getPackageName();
            array[1] = foregroundTaskInfo.topActivity
                    .getShortClassName();
            return array;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        private String getPackageNameNewApi() {
            UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);//"usagestats"
            long time = System.currentTimeMillis();
            String packageName = "";
            List<UsageStats> stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 5000, time);
            if (stats != null) {
                SortedMap<Long, UsageStats> sortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : stats) {
                    sortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (!sortedMap.isEmpty()) {
                    packageName = sortedMap.get(sortedMap.lastKey()).getPackageName();
                }
            }
            return packageName;
        }
    }
}
