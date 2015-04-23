package hu.uniobuda.nik.parentalcontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.support.v7.app.ActionBarActivity;
import android.widget.AdapterView.OnItemClickListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class FilterAppsActivity extends ActionBarActivity {
    AppListAdapter adapter;
    ListView appList;
    Button btnSave;
    TextView childName;
    List<AppInfo> list;
    ArrayList<String> checkedValue = new ArrayList<String>();
    ArrayList<String> tempDelete = new ArrayList<String>();
    SharedPreferences shSelectedApps;
    String personName;
    Map<String, ?> selectedAppsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_apps);
        shSelectedApps = getSharedPreferences(getString
                (R.string.SHAREDPREFERENCE_PACKAGES), Context.MODE_PRIVATE);
        selectedAppsMap = shSelectedApps.getAll();
        for (Map.Entry<String, ?> entry : selectedAppsMap.entrySet()) {
            Log.d("pacakage", entry.getKey());
        }

        appList = (ListView) findViewById(R.id.appList);
        btnSave = (Button) findViewById(R.id.btnSave);
        childName = (TextView) findViewById(R.id.childName);
        personName = getIntent().getStringExtra(getString(R.string.EXTRA_PERSON_NAME));
        if (personName == null) {
            personName = "all";
            childName.setText(getString(R.string.followingPersonSettings) +" "+ getString(R.string.all));
        } else {
            childName.setText(getString(R.string.followingPersonSettings) +" "+ personName);
        }

        checkedValue.clear();
        new backgroundLoadAppList().execute();

        appList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox cb = (CheckBox) view.findViewById(R.id.appChk);
                cb.performClick();
                String pName = list.get(position).pName;
                if (cb.isChecked()) {

                    Log.d("esemeny", pName);
                    checkedValue.add(pName);
                    if (tempDelete.contains(pName)) {
                        tempDelete.remove(pName);
                    }
                } else {
                    Log.d("esemeny", "checkvizsgaN");
                    checkedValue.remove(pName);
                    tempDelete.add(pName);
                }
            }
        });

        btnSave.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Editor e = shSelectedApps.edit();
                for (String pckg : tempDelete) {
                    String deniedPersons = "";
                    if (selectedAppsMap.containsKey(pckg)) {
                        deniedPersons = selectedAppsMap.get(pckg).toString();
                    }
                    Log.d("DELdeniedPersons", deniedPersons);
                    Log.d("package", pckg);
                    deniedPersons = deniedPersons.replace(":" + personName, "");
                    Log.d("DELdenPersReplaced", deniedPersons);


                    if (deniedPersons.equals("")) {
                        Log.d("deniedPerson", "ITTJÁR");
                        e.remove(pckg);
                        BlockerHashTable.deleteBoolean(pckg);
                    } else {
                        e.putString(pckg, deniedPersons);
                    }
                }
                for (String pckg : checkedValue) {
                    String deniedPersons = "";
                    if (selectedAppsMap.containsKey(pckg)) {
                        deniedPersons = selectedAppsMap.get(pckg).toString();
                    }
                    Log.d("ADDdeniedPersons", deniedPersons);
                    Log.d("package", pckg);

                    if (!deniedPersons.contains(personName)) {
                        deniedPersons = deniedPersons + ":" + personName;
                        Log.d("ADDdenPersReplaced", deniedPersons);
                        e.putString(pckg, deniedPersons);
                    }

                    BlockerHashTable.setBoolean(pckg, true);
                }


                e.putString("hu.uniobuda.nik.parentalcontrol", "all");
                //BlockerHashTable.setBoolean("hu.uniobuda.nik.parentalcontrol", true);
                e.putString("com.android.settings", "all");
                //BlockerHashTable.setBoolean("com.android.settings", true);
                e.putString("com.android.packageinstaller", "all");
                //BlockerHashTable.setBoolean("com.android.packageinstaller", true);
                e.apply();
                //BlockerHashTable.refresh(FilterAppsActivity.this);
               // Intent i = new Intent();
               // i.setAction(getString(R.string.BROADCAST_REFRESH_SERVICE_HASHTABLE));
               // sendBroadcast(i);
                finish();
            }
        });
    }

    public class backgroundLoadAppList extends
            AsyncTask<Void, Void, List<AppInfo>> {
        ProgressDialog pd = new ProgressDialog(FilterAppsActivity.this);

        @Override
        protected void onPreExecute() {
            pd.setTitle(R.string.pleaseWait);
            pd.setMessage(getString(R.string.loadingApps));
            pd.show();
        }

        @Override
        protected void onPostExecute(List<AppInfo> result) {
            adapter = new AppListAdapter(FilterAppsActivity.this, R.layout.applist_layout, result, checkedValue);
            appList.setAdapter(adapter);
            list = result;
            pd.dismiss();
        }

        @Override
        protected List<AppInfo> doInBackground(Void... arg0) {

            ArrayList<AppInfo> res = new ArrayList<AppInfo>();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> apps = getPackageManager().queryIntentActivities(intent, 0);
            for (int i = 0; i < apps.size(); i++) {
                ResolveInfo info = apps.get(i);
                AppInfo newInfo = new AppInfo();
                newInfo.pName = info.activityInfo.packageName;
                if (newInfo.pName.equals("hu.uniobuda.nik.parentalcontrol") ||
                        newInfo.pName.equals("com.android.settings")) {
                    continue;
                } else {
                    newInfo.appName = info.loadLabel(getPackageManager()).toString();
                    newInfo.pName = info.activityInfo.packageName;
                    newInfo.appIcon = info.loadIcon(getPackageManager());
                    if (shSelectedApps.contains(newInfo.pName)) {
                        if (selectedAppsMap.get(newInfo.pName).toString().contains(personName)) {
                            //Log.d("nevek", selectedAppsMap.get(newInfo.pName).toString());
                            checkedValue.add(newInfo.pName);
                        }
                    }
                    res.add(newInfo);
                }
            }
            return res;
        }
    }

}

class AppInfo {
    public String appName = "";
    public String pName = "";
    public Drawable appIcon;
}
