package hu.uniobuda.nik.parentalcontrol;

import android.util.Log;

/**
 * Created by Pacsiz on 2015.04.28..
 */
public class AppStartedHandler implements AppStartedListener {

    public AppStartedHandler() {
    }

    @Override
    public void appStarted(String packageName) {
        synchronized (packageName) {
            Log.d("AppStartedHandler", "packageName: " + packageName);
        }
    }
}
