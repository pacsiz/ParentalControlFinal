package hu.uniobuda.nik.parentalcontrol;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity {

    LongTextCheckBoxPreference urlEnabled;
    LongTextCheckBoxPreference accessControlEnabled;
    SharedPreferences sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            addPreferencesFromResource(R.xml.preferences);
            urlEnabled = (LongTextCheckBoxPreference) findPreference("urlEnabled");
            accessControlEnabled = (LongTextCheckBoxPreference)findPreference("deviceAccessEnabled");
            sh = getSharedPreferences(getString(R.string.SHAREDPREFERENCE_SETTINGS), Context.MODE_PRIVATE);
            urlEnabled.setChecked(sh.getBoolean(getString(R.string.SHAREDPREFERENCE_URL_ENABLED),false));
            accessControlEnabled.setChecked(sh.getBoolean(getString(R.string.SHAREDPREFERENCE_ACCESS_CONTROL_ENABLED),false));


            urlEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (RootCheck.isDeviceRooted()) {

                        if (ServiceInfo.isServiceRunning(CheckService.class, SettingsActivity.this)) {
                            if (urlEnabled.isChecked()) {
                                IPTablesAPI.unblockAllURL(SettingsActivity.this);
                            } else {
                                IPTablesAPI.blockAllURL(SettingsActivity.this);
                            }
                        }
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(SettingsActivity.this);
                        dialog.setTitle(R.string.failTitle);
                        dialog.setMessage(getString(R.string.rootFailed));
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //urlEnabled.setChecked(false);
                            }
                        });
                        dialog.show();

                    }
                    Editor e = sh.edit();
                    e.putBoolean(getString(R.string.SHAREDPREFERENCE_URL_ENABLED), (boolean) newValue);
                    Log.d("URL_ENABLED", newValue.toString());
                    e.apply();
                    return true;
                }
            });

            accessControlEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Editor e = sh.edit();
                    e.putBoolean(getString(R.string.SHAREDPREFERENCE_ACCESS_CONTROL_ENABLED),(boolean) newValue);
                    e.apply();
                    return true;
                }
            });

        } else {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsPreferenceFragment())
                    .commit();

        }
    }



    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SettingsPreferenceFragment extends PreferenceFragment {
        LongTextCheckBoxPreference urlEnabled;
        LongTextCheckBoxPreference accessControlEnabled;
        SharedPreferences sh;
        //Context context;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            sh = getActivity().getSharedPreferences(getString
                    (R.string.SHAREDPREFERENCE_SETTINGS), Context.MODE_PRIVATE);

            accessControlEnabled = (LongTextCheckBoxPreference)findPreference("deviceAccessEnabled");
            urlEnabled = (LongTextCheckBoxPreference) findPreference("urlEnabled");

            urlEnabled.setChecked(sh.getBoolean(getString(R.string.SHAREDPREFERENCE_URL_ENABLED),false));
            accessControlEnabled.setChecked(sh.getBoolean(getString(R.string.SHAREDPREFERENCE_ACCESS_CONTROL_ENABLED),false));

            urlEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (RootCheck.isDeviceRooted()) {

                        if (ServiceInfo.isServiceRunning(CheckService.class, getActivity())) {
                            if (urlEnabled.isChecked()) {
                                IPTablesAPI.unblockAllURL(getActivity());
                            } else {
                                IPTablesAPI.blockAllURL(getActivity());
                            }
                        }
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setTitle(R.string.failTitle);
                        dialog.setMessage(getString(R.string.rootFailed));
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //urlEnabled.setChecked(false);
                            }
                        });
                        dialog.show();

                    }
                    Editor e = sh.edit();
                    e.putBoolean(getString(R.string.SHAREDPREFERENCE_URL_ENABLED), (boolean) newValue);
                    Log.d("URL_ENABLED", newValue.toString());
                    e.apply();
                    return true;
                }
            });

            accessControlEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Editor e = sh.edit();
                    e.putBoolean(getString(R.string.SHAREDPREFERENCE_ACCESS_CONTROL_ENABLED),(boolean) newValue);
                    e.apply();
                    return true;
                }
            });

        }
    }

}

