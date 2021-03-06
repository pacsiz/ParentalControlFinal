package hu.uniobuda.nik.parentalcontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import hu.uniobuda.nik.parentalcontrol.backend.PasswordCreator;

public class PasswordActivity extends ActionBarActivity {
    EditText confirmPw;
    EditText newPw;
    EditText email;
    SharedPreferences settings;
    Button save;
    private static final int PW_MIN_LENGTH = 4;
    Editor e;


    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_password);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffd6d6d6")));

        hideKeyboard(findViewById(R.id.pwLayout));
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        newPw = (EditText) findViewById(R.id.newPassword);
        confirmPw = (EditText) findViewById(R.id.confirmPassword);
        email = (EditText) findViewById(R.id.editText_email_address);
        save = ((Button) findViewById(R.id.setPassword));
        settings = getSharedPreferences(getString
                (R.string.SHAREDPREFERENCE_SETTINGS), Context.MODE_PRIVATE);
        email.setText(settings.getString(getString(R.string.SHAREDPREFERENCE_EMAIL), ""));

        save.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String pw = newPw.getText().toString();
                String confPw = confirmPw.getText().toString();
                String email_address = email.getText().toString();
                //Log.d("PasswordActivity", "Password: "+pw);
                //Log.d("PasswordActivity", "Confirmed password: "+confPw);
                if (pw.isEmpty()) {
                    dialog.setTitle(R.string.failTitle);
                    dialog.setMessage(R.string.noPassword);
                    dialog.setNegativeButton(R.string.OK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int arg1) {
                            dialogInterface.dismiss();
                        }
                    });
                    dialog.show();
                    return;
                } else if (pw.length() < PW_MIN_LENGTH) {
                    dialog.setTitle(R.string.failTitle);
                    dialog.setMessage(R.string.shortPassword);
                    dialog.setNegativeButton(R.string.OK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int arg1) {
                            dialogInterface.dismiss();
                        }
                    });
                    dialog.show();
                    return;
                } else if (!pw.equals(confPw)) {
                    dialog.setTitle(R.string.failTitle);
                    dialog.setMessage(R.string.passwordFailMessage);
                    dialog.setNegativeButton(R.string.OK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int arg1) {
                            dialogInterface.dismiss();
                        }
                    });
                    dialog.show();
                    return;
                } else if (email_address.isEmpty() ||
                        !Patterns.EMAIL_ADDRESS.matcher(email_address).matches()) {
                    dialog.setTitle(R.string.failTitle);
                    dialog.setMessage(R.string.emailFailMessage);
                    dialog.setNegativeButton(R.string.OK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int arg1) {
                            dialogInterface.dismiss();
                        }
                    });
                    dialog.show();
                    return;
                }

                e = settings.edit();
                e.putString(getString
                        (R.string.SHAREDPREFERENCE_PASSWORD), PasswordCreator.createPassword(pw));
                e.putString(getString(R.string.SHAREDPREFERENCE_EMAIL), email_address);

                e.commit();
                Toast.makeText(PasswordActivity.this, R.string.passwordChanged, Toast.LENGTH_LONG).show();
                finish();

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
}
