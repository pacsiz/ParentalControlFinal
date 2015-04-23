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
    private static final int ACTION_PAUSE = 2;
    private static final int ACTION_HOME = 3;
    private static final int ACTION_LOCK = 4;
    int actionCode;

    EditText getPassword;
    boolean isCorrect;
    Button ok;
    Button forgotPassword;
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

    /*@Override
    protected void onPause() {
        Log.d("onPause","ban jár");
        if (actionCode == 0)
        {
            actionCode = ACTION_PAUSE;
            AccessControl.lock(PasswordRequestActivity.this);
        }
        finish();
        super.onPause();
    }*/

    @Override
    protected void onResume() {
        Log.d("onResume","ban jár");
        super.onResume();
    }

    @Override
    protected void onStop() {
        if (actionCode == 0)
        {
            AccessControl.lock(PasswordRequestActivity.this);
        }
        finish();
        super.onStop();
    }



    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_password_request);
        actionCode = 0;
        hideKeyboard(findViewById(R.id.pwReqlayout));
        getPassword = ((EditText) findViewById(R.id.getPassword));
        ok = (Button) findViewById(R.id.OK);
        forgotPassword = (Button)findViewById(R.id.btnForgotPassword);
        packageName = getIntent().getStringExtra(getString
                (R.string.EXTRA_PACKAGE_NAME));
        if(packageName != null)
        {
            actionCode = ACTION_HOME;
        }
        accessControl = getIntent().getBooleanExtra(getString(R.string.EXTRA_ACCESS_CONTROL), false);
        Log.d("accessControl",accessControl+"");
        Log.d("actionCode",actionCode+"");
        pwSh = getSharedPreferences(getString
                (R.string.SHAREDPREFERENCE_SETTINGS), Context.MODE_PRIVATE);
        ok.setOnClickListener(new View.OnClickListener() {
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
                    //AccessControl.playSound(R.raw.ok,PasswordRequestActivity.this);
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

                dialog.setPositiveButton(getString(R.string.OK),new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        generateNewPassword();
                        dialogInterface.dismiss();
                        //finish();
                    }
                });
                dialog.show();
                //AccessControl.block(PasswordRequestActivity.this,null);
               // finish();

            }
        });
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

    private void generateNewPassword()
    {
        //actionCode = ACTION_HOME;
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net != null && net.isConnectedOrConnecting()) {
            Editor e = pwSh.edit();
            String newPw = PasswordCreator.randomPassword();
            e.putString(getString(R.string.SHAREDPREFERENCE_PASSWORD), PasswordCreator.createPassword(newPw));
            e.apply();
            String toAddress = pwSh.getString(getString(R.string.SHAREDPREFERENCE_EMAIL), "");
            Log.d("toAddresss", toAddress);
            String fromAddress = getString(R.string.email);

            String fromPassword = getString(R.string.email_password);
            String subject = getString(R.string.subject);
            String text = getString(R.string.email_text);

            EmailSender sender = new EmailSender(fromAddress, fromPassword, toAddress, subject, text + newPw);
            sender.sendEmail();
            Toast.makeText(PasswordRequestActivity.this, R.string.email_sent, Toast.LENGTH_LONG).show();
        }
        else
        {
            WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            wifi.setWifiEnabled(true);
            Toast.makeText(PasswordRequestActivity.this, R.string.email_send_fail,Toast.LENGTH_LONG).show();
        }
    }
}
