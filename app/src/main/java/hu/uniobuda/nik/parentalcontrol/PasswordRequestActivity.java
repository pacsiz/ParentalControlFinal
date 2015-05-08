package hu.uniobuda.nik.parentalcontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.*;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import hu.uniobuda.nik.parentalcontrol.backend.PasswordCreator;
import hu.uniobuda.nik.parentalcontrol.identification.AccessControl;
import hu.uniobuda.nik.parentalcontrol.identification.BlockerHashTable;

public class PasswordRequestActivity extends Activity {
    private static final int ACTION_NOT_SET = 0;
    private static final int ACTION_FINISH = 1;
    private static final int ACTION_BLOCK = 2;
    private static final int ACTION_LOCK = 3;
    int actionCode = ACTION_NOT_SET;

    EditText getPassword;
    Button ok;
    Button forgotPassword;
    String packageName;
    SharedPreferences pwSh;
    boolean deviceAccessControl = false;

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_password_request);
        hideKeyboard(findViewById(R.id.pwReqlayout));

        getPassword = ((EditText) findViewById(R.id.getPassword));
        ok = (Button) findViewById(R.id.OK);
        forgotPassword = (Button) findViewById(R.id.btnForgotPassword);

        packageName = getIntent().getStringExtra(getString(R.string.EXTRA_PACKAGE_NAME));
        deviceAccessControl = packageName == null;

        if(deviceAccessControl &&
                !(((KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE)).inKeyguardRestrictedInputMode()))
        {
            actionCode = ACTION_LOCK;
        }
        //Log.d("PasswordRequestActivity", "Access control enabled: "+deviceAccessControl);

        pwSh = getSharedPreferences(getString
                (R.string.SHAREDPREFERENCE_SETTINGS), Context.MODE_PRIVATE);

        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String pw = PasswordCreator.createPassword(getPassword.getText().toString());
                String savedPw = pwSh.getString(getString
                        (R.string.SHAREDPREFERENCE_PASSWORD), "");
                if (pw.equals(savedPw)) {
                    actionCode = ACTION_FINISH;
                    if (!deviceAccessControl) {
                        if (packageName != null && !packageName.equals("hu.uniobuda.nik.parentalcontrol")) {
                            BlockerHashTable.setBoolean(packageName, false);
                            //Log.d("PasswordRequestActivity", "Package set false: " + packageName);
                        }
                    }
                    Toast.makeText(PasswordRequestActivity.this, R.string.accessAllowedByPassword, Toast.LENGTH_LONG).show();
                    //AccessControl.playSound(R.raw.ok,PasswordRequestActivity.this);
                } else {
                    if (deviceAccessControl) {
                        actionCode = ACTION_LOCK;
                    } else {
                        actionCode = ACTION_BLOCK;
                    }
                    Toast.makeText(PasswordRequestActivity.this, R.string.incorrectPassword, Toast.LENGTH_LONG).show();
                }
                onStop();
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
        switch(actionCode){
            case ACTION_LOCK:
                AccessControl.lock(PasswordRequestActivity.this);
                break;
            case ACTION_FINISH:
                break;
            case ACTION_BLOCK:
                AccessControl.block(PasswordRequestActivity.this, null);
                break;
            default:
                super.onStop();
                return;
        }
        finish();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        onStop();
    }

    @Override
    protected void onRestart() {
        if(deviceAccessControl) actionCode = ACTION_LOCK;
        super.onRestart();
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
