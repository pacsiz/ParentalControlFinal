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
                    .replace(android.R.id.content, new SettingsPreferenceFragment())
                    .commit();

        }
    }

    public static class SettingsPreferenceFragment extends PreferenceFragment {
        CheckBoxPreference urlEnabled;
        SharedPreferences sh;
        //Context context;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            if (RootCheck.isDeviceRooted()) {
                sh = getActivity().getSharedPreferences(getString
                        (R.string.SHAREDPREFERENCE_SETTINGS), Context.MODE_PRIVATE);
                urlEnabled = (CheckBoxPreference) findPreference("urlEnabled");
                urlEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        Editor e = sh.edit();
                        e.putBoolean(getString(R.string.SHAREDPREFERENCE_URL_ENABLED), (boolean) newValue);
                        Log.d("URL_ENABLED", newValue.toString());
                        e.commit();
                        if(ServiceInfo.isServiceRunning(CheckService.class,getActivity()))
                        {
                            if (urlEnabled.isChecked()) {
                                IPTablesAPI.unblockAllURL(getActivity());
                            } else {
                                IPTablesAPI.blockAllURL(getActivity());
                            }
                        }
                        return true;
                    }
                });
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
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
    }
}