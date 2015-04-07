package hu.uniobuda.nik.parentalcontrol;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity {

    CheckBoxPreference urlEnabled;
    SharedPreferences sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            addPreferencesFromResource(R.xml.preferences);
            if (RootCheck.isDeviceRooted()) {
                urlEnabled = (CheckBoxPreference) findPreference("urlEnabled");
                sh = getSharedPreferences(getString(R.string.SHAREDPREFERENCE_SETTINGS), Context.MODE_PRIVATE);
                urlEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        Editor e = sh.edit();
                        e.putBoolean(getString(R.string.SHAREDPREFERENCE_URL_ENABLED), (boolean) newValue);
                        Log.d("URL_ENABLED", newValue.toString());
                        e.commit();
                        return true;
                    }
                });
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
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
        } else {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsPreferenceFragment(SettingsActivity.this))
                    .commit();

        }
    }


}