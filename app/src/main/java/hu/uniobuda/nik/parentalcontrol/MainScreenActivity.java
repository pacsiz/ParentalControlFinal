package hu.uniobuda.nik.parentalcontrol;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainScreenActivity extends Activity {


    TextView serviceState;
    Button btnSettings;
    Button btnStartService;
    boolean isRunning;
    SharedPreferences sh;
    private final String FACE_REG_SET = "faceRegEnabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        serviceState = (TextView) findViewById(R.id.isRunning);
        btnSettings = (Button) findViewById(R.id.btnSettings);
        btnStartService = (Button) findViewById(R.id.btnStartService);
        isRunning = isMyServiceRunning(CheckService.class, serviceState);
        sh = getSharedPreferences("settings", Context.MODE_PRIVATE);
        if (!sh.contains(FACE_REG_SET)) {
            Editor e = e = sh.edit();
            if (CameraSet.getFrontCameraIndex() == -1) {
                e.commit();
            } else {
                e.putBoolean(FACE_REG_SET, true);
            }
            e.commit();
        }


        if (isRunning)
            btnStartService.setText(R.string.stopService);
        else
            btnStartService.setText(R.string.startService);

        btnSettings.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(MainScreenActivity.this,
                        SettingsActivityNewApi.class);
                startActivity(intent);
            }
        });

        btnStartService.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sh = getSharedPreferences("password", Context.MODE_PRIVATE);
                if (sh.contains("password")) {
                    if (!isRunning) {
                        Intent i = new Intent(MainScreenActivity.this,
                                CheckService.class);
                        i.putExtra("name", "SurvivingwithAndroid");
                        startService(i);
                        // MainScreenActivity.this.startService(i);
                        isRunning = isMyServiceRunning(CheckService.class,
                                serviceState);
                    } else {
                        Intent i = new Intent(MainScreenActivity.this,
                                CheckService.class);
                        stopService(i);
                        isRunning = isMyServiceRunning(CheckService.class,
                                serviceState);
                    }
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

    private boolean isMyServiceRunning(Class<?> serviceClass, TextView tv) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                tv.setText(getString(R.string.isRunning));
                tv.setTextColor(Color.GREEN);
                btnStartService.setText(R.string.stopService);
                return true;
            }
        }
        tv.setText(getString(R.string.isNotRunning));
        tv.setTextColor(Color.RED);
        btnStartService.setText(R.string.startService);
        return false;
    }

}
