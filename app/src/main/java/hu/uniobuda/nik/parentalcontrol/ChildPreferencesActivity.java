package hu.uniobuda.nik.parentalcontrol;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class ChildPreferencesActivity extends PreferenceActivity {

    SharedPreferences sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {

            String personName = getIntent().getStringExtra(getString(R.string.EXTRA_PERSON_NAME));
            PreferenceCategory prefCat = (PreferenceCategory) findPreference("childPreferenceCategory");

            Preference childSelectApps = (Preference)findPreference("childSelectApps");
            Preference childDeviceAccess = (Preference)findPreference("childDeviceAccess");
            setIntent(ChildPreferencesActivity.this,FilterAppsActivity.class,personName,childSelectApps);
            setIntent(ChildPreferencesActivity.this,DeviceAccessPersonSettingsActivity.class,personName,childDeviceAccess);
            prefCat.setTitle(personName + getString(R.string.childSettings));
            addPreferencesFromResource(R.xml.child_preferences);

        } else {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsPreferenceFragment())
                    .commit();

        }
    }

    private void setIntent(Context context, Class targetClass, String personName, Preference preference) {
        Intent intent = new Intent(context, targetClass);
        intent.putExtra(context.getString(R.string.EXTRA_PERSON_NAME), personName);
        preference.setIntent(intent);
    }

    public static class SettingsPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);
            String personName = getActivity().getIntent().getStringExtra(getString(R.string.EXTRA_PERSON_NAME));


            addPreferencesFromResource(R.xml.child_preferences);
            PreferenceCategory prefCat = (PreferenceCategory) findPreference("childPreferenceCategory");
            Preference childSelectApps = (Preference)findPreference("childSelectApps");
            Preference childDeviceAccess = (Preference)findPreference("childDeviceAccess");
            setIntent(getActivity(),FilterAppsActivity.class,personName,childSelectApps);
            setIntent(getActivity(),DeviceAccessPersonSettingsActivity.class,personName,childDeviceAccess);

            prefCat.setTitle(personName + getString(R.string.childSettings));
        }

        private void setIntent(Context context, Class targetClass, String personName, Preference preference) {
            Intent intent = new Intent(context, targetClass);
            intent.putExtra(context.getString(R.string.EXTRA_PERSON_NAME), personName);
            preference.setIntent(intent);
        }
    }


}
