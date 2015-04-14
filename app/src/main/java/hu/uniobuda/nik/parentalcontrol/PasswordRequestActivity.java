package hu.uniobuda.nik.parentalcontrol;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.*;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.PrivilegedAction;
import java.util.List;

public class PasswordRequestActivity
        extends Activity {
    EditText getPassword;
    boolean isCorrect;
    Button ok;
    String pName;
    SharedPreferences pwSh;
    boolean accessControl;
    boolean disableRequest;

    public void onBackPressed() {
        Intent i = new Intent("android.intent.action.MAIN");
        i.addCategory("android.intent.category.HOME");
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_password_request);
        getPassword = ((EditText) findViewById(R.id.getPassword));
        ok = ((Button) findViewById(R.id.OK));
        pName = getIntent().getStringExtra(getString
                (R.string.EXTRA_PACKAGE_NAME));
        accessControl = getIntent().getBooleanExtra(getString(R.string.EXTRA_ACCESS_CONTROL), false);
        pwSh = getSharedPreferences(getString
                (R.string.SHAREDPREFERENCE_PASSWORD), Context.MODE_PRIVATE);
        this.ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String pw = PasswordCreator.createPassword(getPassword.getText().toString());
                String savedPw = pwSh.getString(getString
                        (R.string.SHAREDPREFERENCE_PASSWORD), "");
//                Log.d("pwpname", pName);
                if (pw.equals(savedPw)) {

                    if(!accessControl) {
                        if (!pName.equals("hu.uniobuda.nik.parentalcontrol")) {
                            BlockerHashTable.setBoolean(pName, false);
                        }
                    }
                    Toast.makeText(PasswordRequestActivity.this, R.string.accessAllowedByPassword, Toast.LENGTH_LONG).show();
                    finish();

                } else {

                    if (accessControl)
                    {
                        AccessControl.lock(PasswordRequestActivity.this);
                    }
                    else {
                        AccessControl.block(PasswordRequestActivity.this,null);
                        finish();
                        //ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                        //BlockerHashTable.setBoolean(pName, true);

                        //List<ActivityManager.RunningAppProcessInfo> pids = am.getRunningAppProcesses();
                    /*for(ActivityManager.RunningAppProcessInfo info : pids)
                    {
                        if(info.processName.equals(pName))
                        {
                            android.os.Process.killProcess(info.pid);
                            break;
                        }
                    }*/

                    }
                    Toast.makeText(PasswordRequestActivity.this, R.string.incorrectPassword, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
