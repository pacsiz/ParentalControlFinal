package hu.uniobuda.nik.parentalcontrol;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MainScreenActivity extends Activity {


    TextView serviceState;
    Button btnSettings;
    Button btnStartService;
    boolean isRunning;
    SharedPreferences sh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        serviceState = (TextView) findViewById(R.id.isRunning);
        btnSettings = (Button) findViewById(R.id.btnSettings);
        btnStartService = (Button) findViewById(R.id.btnStartService);
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
            e.commit();
        }
        Calendar time = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        Date date = null;
        try {
            date = sdf.parse("17:21");
        } catch (ParseException e) {
            e.printStackTrace();
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
                sh = getSharedPreferences(getString
                        (R.string.SHAREDPREFERENCE_PASSWORD), Context.MODE_PRIVATE);

                PackageManager pm = getPackageManager();
                ComponentName receiver = new ComponentName(MainScreenActivity.this, CheckServiceStarter.class);

                if (sh.contains(getString
                        (R.string.SHAREDPREFERENCE_PASSWORD))) {
                    SharedPreferences serviceEnabled = getSharedPreferences(getString(R.string.SHAREDPREFERENCE_SETTINGS), Context.MODE_PRIVATE);
                    Editor e = serviceEnabled.edit();
                    if (!isRunning) {
                        //e.putBoolean(getString(R.string.SHAREDPREFERENCE_SERVICE_ENABLED), true);

                        pm.setComponentEnabledSetting(receiver,
                                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                PackageManager.DONT_KILL_APP);
                        Intent i = new Intent(MainScreenActivity.this,
                                CheckService.class);
                        startService(i);
                        serviceState.setText(getString(R.string.isRunning));
                        btnStartService.setText(R.string.stopService);
                        serviceState.setTextColor(Color.GREEN);
                        isRunning = true;
                    } else {
                        //e.putBoolean(getString(R.string.SHAREDPREFERENCE_SERVICE_ENABLED), false);
                        pm.setComponentEnabledSetting(receiver,
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
                    e.commit();
                } else {
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
                }
            }
        });
    }


}
