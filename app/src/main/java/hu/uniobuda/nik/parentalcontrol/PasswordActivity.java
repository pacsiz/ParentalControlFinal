package hu.uniobuda.nik.parentalcontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordActivity
        extends Activity
{
    EditText confirmPw;
    EditText newPw;
    EditText email;
    SharedPreferences pwSh;
    Button save;
    private static final int PW_MIN_LENGTH = 4;
    private static int REQUEST_CODE = 1001;
    Editor e;


    protected void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_password);
        hideKeyboard(findViewById(R.id.pwLayout));
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        newPw = (EditText)findViewById(R.id.newPassword);
        confirmPw = (EditText)findViewById(R.id.confirmPassword);
        email = (EditText)findViewById(R.id.editText_email_address);
        save = ((Button)findViewById(R.id.setPassword));
        pwSh = getSharedPreferences(getString
                (R.string.SHAREDPREFERENCE_SETTINGS), Context.MODE_PRIVATE);
        email.setText(pwSh.getString(getString(R.string.SHAREDPREFERENCE_EMAIL),""));

        save.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                String pw = newPw.getText().toString();
                String confPw = confirmPw.getText().toString();
                String email_address = email.getText().toString();
                Log.d("pw", pw);
                Log.d("confpw", confPw);
                if (pw.isEmpty())
                {
                    dialog.setTitle(R.string.failTitle);
                    dialog.setMessage(R.string.noPassword);
                    dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                        }
                    });
                    dialog.show();
                    return;
                }

                if(pw.length() < PW_MIN_LENGTH)
                {
                    dialog.setTitle(R.string.failTitle);
                    dialog.setMessage(R.string.shortPassword);
                    dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                        }
                    });
                    dialog.show();
                    return;
                }

                if (!pw.equals(confPw))
                {
                    dialog.setTitle(R.string.failTitle);
                    dialog.setMessage(R.string.passwordFailMessage);
                    dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                        }
                    });
                    dialog.show();
                    return;
                }

                if(email_address.isEmpty() ||
                        !Patterns.EMAIL_ADDRESS.matcher(email_address).matches())
                {
                    dialog.setTitle(R.string.failTitle);
                    dialog.setMessage(R.string.emailFailMessage);
                    dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                        }
                    });
                    dialog.show();
                    return;
                }

                e = pwSh.edit();
                e.putString(getString
                        (R.string.SHAREDPREFERENCE_PASSWORD), PasswordCreator.createPassword(pw));
                e.putString(getString(R.string.SHAREDPREFERENCE_EMAIL),email_address);

                /*DevicePolicyManager dpm
                        = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                ComponentName componentName
                        = new ComponentName(PasswordActivity.this, DevAdminReceiver.class);
                boolean isAdminActive = dpm.isAdminActive(componentName);
                if (!isAdminActive) {
                    Intent i = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    i.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
                    startActivityForResult(i, REQUEST_CODE);
                }
                else
                {
                    finish();
                    Toast.makeText(PasswordActivity.this, R.string.passwordChanged, Toast.LENGTH_LONG).show();*/
                    e.apply();
                    Toast.makeText(PasswordActivity.this, R.string.passwordChanged, Toast.LENGTH_LONG).show();
                    finish();
                //}

            }
        });
    }

  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(PasswordActivity.this, R.string.passwordChanged, Toast.LENGTH_LONG).show();
                e.commit();
                finish();
                Log.d("ONRESULT", "Administration enabled!");
            } else {
                Toast.makeText(PasswordActivity.this, R.string.adminNeeded, Toast.LENGTH_LONG).show();
                Log.d("ORESULT", "Administration enable FAILED!");
            }
        }

    }*/

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
