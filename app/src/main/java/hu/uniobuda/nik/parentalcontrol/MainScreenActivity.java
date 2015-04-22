package hu.uniobuda.nik.parentalcontrol;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainScreenActivity extends ActionBarActivity {

    TextView serviceState;
    Button btnSettings;
    Button btnStartService;
    Button btnHelp;
    boolean isRunning;
    SharedPreferences sh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        serviceState = (TextView) findViewById(R.id.isRunning);
        btnSettings = (Button) findViewById(R.id.btnSettings);
        btnStartService = (Button) findViewById(R.id.btnStartService);
        btnHelp = (Button) findViewById(R.id.btnHelp);
        isRunning = ServiceInfo.isServiceRunning(CheckService.class, MainScreenActivity.this);
        sh = getSharedPreferences(getString
                (R.string.SHAREDPREFERENCE_SETTINGS), Context.MODE_PRIVATE);
        if (!sh.contains(getString(R.string.SHAREDPREFERENCE_FACE_REG_ENABLED))) {
            Editor e = e = sh.edit();
            if (CameraSet.getFrontCameraIndex() == -1) {
                e.putBoolean(getString(R.string.SHAREDPREFERENCE_FACE_REG_ENABLED), false);
            } else {
                e.putBoolean(getString(R.string.SHAREDPREFERENCE_FACE_REG_ENABLED), true);
            }
            e.apply();
        }

        if (isRunning) {
            serviceState.setText(getString(R.string.isRunning));
            serviceState.setTextColor(Color.GREEN);
            btnStartService.setText(R.string.stopService);
        } else {
            serviceState.setText(getString(R.string.isNotRunning));
            serviceState.setTextColor(Color.RED);
            btnStartService.setText(R.string.startService);
        }

        btnSettings.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(MainScreenActivity.this,
                        SettingsActivity.class);
                startActivity(intent);
            }
        });

        btnStartService.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DevicePolicyManager dpm
                        = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                ComponentName componentName
                        = new ComponentName(MainScreenActivity.this, DevAdminReceiver.class);
                ;

                if (!sh.contains(getString
                        (R.string.SHAREDPREFERENCE_PASSWORD))) {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainScreenActivity.this);
                    dialog.setTitle(R.string.failTitle);
                    dialog.setMessage(R.string.noPasswordSet);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else if (!dpm.isAdminActive(componentName)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainScreenActivity.this);
                    dialog.setTitle(R.string.failTitle);
                    dialog.setMessage(R.string.adminFailure);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                            i.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, new ComponentName(MainScreenActivity.this, DevAdminReceiver.class));
                            startActivity(i);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();


                } else {
                    if (!isRunning) {
                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        {
                            if(!isUsageStatsEnabled())
                            {
                                Intent i = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                                startActivity(i);
                                Toast.makeText(MainScreenActivity.this,R.string.usageStatsMustEnabled,Toast.LENGTH_LONG).show();
                                Log.d("return", "return");
                                return;
                            }
                        }*/
                        Log.d("startservice", "enabledservice");
                        enableService();
                    } else {
                        disableService();
                    }
                }
            }
        });

        btnHelp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(MainScreenActivity.this,
                        HelpActivity.class);
                startActivity(intent);
            }
        });
    }


    private void enableService() {
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(MainScreenActivity.this, CheckServiceStarter.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Intent i = new Intent(MainScreenActivity.this,
                CheckService.class);
        startService(i);
        serviceState.setText(getString(R.string.isRunning));
        btnStartService.setText(R.string.stopService);
        serviceState.setTextColor(Color.GREEN);
        isRunning = true;
    }

    private void disableService() {
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(MainScreenActivity.this, CheckServiceStarter.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        Intent i = new Intent(MainScreenActivity.this,
                CheckService.class);
        stopService(i);
        btnStartService.setText(R.string.startService);
        serviceState.setText(getString(R.string.isNotRunning));
        serviceState.setTextColor(Color.RED);
        isRunning = false;
    }

    private boolean isUsageStatsEnabled() {
        try {
            PackageManager pm = getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(getPackageName(), 0);
            AppOpsManager aom = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int allowed = aom.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, appInfo.uid, appInfo.packageName);
            return (allowed == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
