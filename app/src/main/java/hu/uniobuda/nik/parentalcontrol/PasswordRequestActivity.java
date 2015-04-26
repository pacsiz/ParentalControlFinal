package hu.uniobuda.nik.parentalcontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.*;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import javax.mail.MessagingException;

public class PasswordRequestActivity
        extends Activity {
    private static final int ACTION_FINISH = 1;
    private static final int ACTION_HOME = 2;
    private static final int ACTION_LOCK = 3;
    int actionCode;

    EditText getPassword;
    Button ok;
    Button forgotPassword;
    String packageName;
    SharedPreferences pwSh;
    boolean accessControl;

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_password_request);
        hideKeyboard(findViewById(R.id.pwReqlayout));

        actionCode = 0;
        getPassword = ((EditText) findViewById(R.id.getPassword));
        ok = (Button) findViewById(R.id.OK);
        forgotPassword = (Button) findViewById(R.id.btnForgotPassword);
        packageName = getIntent().getStringExtra(getString
                (R.string.EXTRA_PACKAGE_NAME));

        if (packageName != null) {
            actionCode = ACTION_HOME;
        }

        accessControl = getIntent().getBooleanExtra(getString(R.string.EXTRA_ACCESS_CONTROL), false);
        //Log.d("PasswordRequestActivity", "Access control enabled: "+accessControl);

        pwSh = getSharedPreferences(getString
                (R.string.SHAREDPREFERENCE_SETTINGS), Context.MODE_PRIVATE);

        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String pw = PasswordCreator.createPassword(getPassword.getText().toString());
                String savedPw = pwSh.getString(getString
                        (R.string.SHAREDPREFERENCE_PASSWORD), "");
                if (pw.equals(savedPw)) {
                    actionCode = ACTION_FINISH;
                    if (!accessControl) {

                        if (packageName != null && !packageName.equals("hu.uniobuda.nik.parentalcontrol")) {
                            BlockerHashTable.setBoolean(packageName, false);
                        }
                    }
                    Toast.makeText(PasswordRequestActivity.this, R.string.accessAllowedByPassword, Toast.LENGTH_LONG).show();
                    //AccessControl.playSound(R.raw.ok,PasswordRequestActivity.this);
                    finish();

                } else {
                    if (accessControl) {
                        actionCode = ACTION_LOCK;
                        AccessControl.lock(PasswordRequestActivity.this);
                    } else {
                        actionCode = ACTION_HOME;
                        AccessControl.block(PasswordRequestActivity.this, null);
                    }
                    finish();
                    Toast.makeText(PasswordRequestActivity.this, R.string.incorrectPassword, Toast.LENGTH_LONG).show();
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(PasswordRequestActivity.this);
                dialog.setTitle(R.string.failTitle);
                dialog.setMessage(R.string.askResetPassword);
                dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });

                dialog.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PasswordCreator.generateNewPassword(PasswordRequestActivity.this);
                        dialogInterface.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    protected void onStop() {
        if (actionCode == 0) {
            AccessControl.lock(PasswordRequestActivity.this);
        }
        finish();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (actionCode != 0)
        {
           AccessControl.block(PasswordRequestActivity.this, null);
        }
    }

    public void hideKeyboard(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View arg0, MotionEvent arg1) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    return false;
                }
            });
        }
    }
}
