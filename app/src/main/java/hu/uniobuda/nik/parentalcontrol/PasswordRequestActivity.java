package hu.uniobuda.nik.parentalcontrol;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.*;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordRequestActivity
        extends Activity {
    private static final int ACTION_FINISH = 1;
    private static final int ACTION_NEXTSTEP = 2;
    private static final int ACTION_HOME = 3;
    private static final int ACTION_LOCK = 4;
    int actionCode = 0;

    EditText getPassword;
    boolean isCorrect;
    Button ok;
    String packageName;
    SharedPreferences pwSh;
    boolean accessControl;
    boolean disableRequest;

    public void onBackPressed() {

        Intent i = new Intent("android.intent.action.MAIN");
        i.addCategory("android.intent.category.HOME");
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    protected void onStop() {
        if (actionCode == 0)
        {
            AccessControl.lock(PasswordRequestActivity.this);
        }
        super.onStop();
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_password_request);
        getPassword = ((EditText) findViewById(R.id.getPassword));
        ok = ((Button) findViewById(R.id.OK));
        packageName = getIntent().getStringExtra(getString
                (R.string.EXTRA_PACKAGE_NAME));
        if(packageName != null)
        {
            actionCode = ACTION_HOME;
        }
        accessControl = getIntent().getBooleanExtra(getString(R.string.EXTRA_ACCESS_CONTROL), false);
        pwSh = getSharedPreferences(getString
                (R.string.SHAREDPREFERENCE_PASSWORD), Context.MODE_PRIVATE);
        this.ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String pw = PasswordCreator.createPassword(getPassword.getText().toString());
                String savedPw = pwSh.getString(getString
                        (R.string.SHAREDPREFERENCE_PASSWORD), "");
//                Log.d("pwpname", packageName);
                if (pw.equals(savedPw)) {
                    actionCode = ACTION_FINISH;
                    if(!accessControl) {

                        if (packageName != null && !packageName.equals("hu.uniobuda.nik.parentalcontrol")) {
                            BlockerHashTable.setBoolean(packageName, false);
                        }
                    }
                    Toast.makeText(PasswordRequestActivity.this, R.string.accessAllowedByPassword, Toast.LENGTH_LONG).show();
                    finish();

                } else {

                    if (accessControl)
                    {
                        actionCode = ACTION_LOCK;
                        AccessControl.lock(PasswordRequestActivity.this);
                        //finish();
                    }
                    else {
                        actionCode = ACTION_HOME;
                        AccessControl.block(PasswordRequestActivity.this,null);
                        //finish();
                        //ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                        //BlockerHashTable.setBoolean(packageName, true);

                        //List<ActivityManager.RunningAppProcessInfo> pids = am.getRunningAppProcesses();
                    /*for(ActivityManager.RunningAppProcessInfo info : pids)
                    {
                        if(info.processName.equals(packageName))
                        {
                            android.os.Process.killProcess(info.pid);
                            break;
                        }
                    }*/

                    }
                    finish();
                    Toast.makeText(PasswordRequestActivity.this, R.string.incorrectPassword, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
