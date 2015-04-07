/*package hu.uniobuda.nik.parentalcontrol;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingsPreferenceFragment extends PreferenceFragment {
    CheckBoxPreference urlEnabled;
    SharedPreferences sh;
    Context context;
    public SettingsPreferenceFragment(Context context)
    {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        if(RootCheck.isDeviceRooted()) {
            sh = context.getSharedPreferences(getString
                    (R.string.SHAREDPREFERENCE_SETTINGS), Context.MODE_PRIVATE);
            urlEnabled = (CheckBoxPreference) findPreference("urlEnabled");
            urlEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Editor e = sh.edit();
                    e.putBoolean(getString(R.string.SHAREDPREFERENCE_URL_ENABLED), (boolean) newValue);
                    Log.d("URL_ENABLED", newValue.toString());
                    e.commit();
                    if(urlEnabled.isChecked())
                    {
                        IPTablesAPI.blockAllURL(SettingsActivity.this);
                    }
                    else
                    {
                        IPTablesAPI.unblockAllURL(SettingsActivity.this);
                    }
                    return true;
                }
            });
        }
        else
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle(R.string.failTitle);
            dialog.setMessage(getString(R.string.rootFailed));
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            urlEnabled.setChecked(false);
        }
    }
}*/