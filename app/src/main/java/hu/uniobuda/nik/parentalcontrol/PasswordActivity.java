package hu.uniobuda.nik.parentalcontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordActivity
        extends Activity
{
    EditText confirmPw;
    EditText newPw;
    SharedPreferences pwSh;
    Button save;
    private static final int PW_MIN_LENGTH = 4;


    protected void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_password);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        newPw = ((EditText)findViewById(R.id.newPassword));
        confirmPw = ((EditText)findViewById(R.id.confirmPassword));
        save = ((Button)findViewById(R.id.setPassword));
        pwSh = getSharedPreferences(getString
                (R.string.SHAREDPREFERENCE_PASSWORD), Context.MODE_PRIVATE);
        save.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                String pw = newPw.getText().toString();
                String confPw = confirmPw.getText().toString();
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
                Editor e = pwSh.edit();
                e.putString(getString
                        (R.string.SHAREDPREFERENCE_PASSWORD), PasswordCreator.createPassword(pw));
                e.commit();
                Toast.makeText(PasswordActivity.this, R.string.passwordChanged, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
