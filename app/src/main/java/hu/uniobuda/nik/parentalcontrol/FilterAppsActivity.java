package hu.uniobuda.nik.parentalcontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.widget.AdapterView.OnItemClickListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.PackageManager;

public class FilterAppsActivity extends Activity {
    ProgressDialog loading;
    AppListAdapter adapter;
    ListView appList;
    Button btnSave;
    Button btnBack;
    List<AppInfo> list;
    ArrayList<String> checkedValue = new ArrayList<String>();
    ArrayList<String> tempDelete = new ArrayList<String>();
    SharedPreferences checkedApps;
    String personName;
    Map<String, ?> checkedAppMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_apps);
        checkedApps = getSharedPreferences(getString
                (R.string.SHAREDPREFERENCE_PACKAGES), Context.MODE_PRIVATE);
        checkedAppMap = checkedApps.getAll();
        Log.d("map size", checkedAppMap.size()+"");
        personName = getIntent().getStringExtra(getString(R.string.EXTRA_PERSON_NAME));
        if (personName == null)
        {
            personName = "all";
        }

        appList = (ListView) findViewById(R.id.appList);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnBack = (Button) findViewById(R.id.btnBack);
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
                    if(tempDelete.contains(pName))
                    {
                        tempDelete.remove(pName);
                    }
                    //cb.setChecked(false);
                } else {
                    Log.d("esemeny", "checkvizsgaN");
                    checkedValue.remove(pName);
                    tempDelete.add(pName);
                    //cb.setChecked(true);
                }
            }
        });

        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        btnSave.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Editor e = checkedApps.edit();
                //e.clear();
                //BlockerHashTable.clear();
                for (String pckg : tempDelete)
                {
                    String deniedPersons = "";
                    if (checkedAppMap.containsKey(pckg))
                    {
                        deniedPersons = checkedAppMap.get(pckg).toString();
                    }
                    Log.d("DELdeniedPersons",deniedPersons);
                    Log.d("package",pckg);
                    String[] deniedPerson = deniedPersons.split(":");
                    if(deniedPerson.length <= 1)
                    {
                        Log.d("del","mindent töröl");

                        e.remove(pckg);
                        BlockerHashTable.deleteBoolean(pckg);
                    }
                    else
                    {
                        deniedPersons = deniedPersons.replace(":"+personName,"");
                        Log.d("DELdenPersReplaced",deniedPersons);
                        e.putString(pckg,deniedPersons);
                    }
                }
                for (String pckg : checkedValue) {
                    String deniedPersons = "";
                    if (checkedAppMap.containsKey(pckg))
                    {
                        deniedPersons = checkedAppMap.get(pckg).toString();
                    }
                    Log.d("ADDdeniedPersons",deniedPersons);
                    Log.d("package",pckg);
                    String[] deniedPerson = deniedPersons.split(":");
                    if(!deniedPersons.contains(personName))
                    {
                        deniedPersons = deniedPersons+":"+personName;
                        Log.d("ADDdenPersReplaced",deniedPersons);
                        e.putString(pckg,deniedPersons);
                    }
                    /*else{

                        e.putString(pckg,deniedPersons);
                    }*/
                    BlockerHashTable.setBoolean(pckg, true);
                   // e.putBoolean(pckg, true);
                   // BlockerHashTable.setBoolean(pckg, true);
                    //Log.d("save", pckg);
                }
                e.putString("hu.uniobuda.nik.parentalcontrol", "all");
                BlockerHashTable.setBoolean("hu.uniobuda.nik.parentalcontrol", true);
                e.putString("com.android.settings", "all");
                BlockerHashTable.setBoolean("com.android.settings", true);
                e.putString("com.android.packageinstaller","all");
                BlockerHashTable.setBoolean("com.android.packageinstaller", true);
                e.apply();

                Toast.makeText(FilterAppsActivity.this, "" + checkedValue, Toast.LENGTH_LONG).show();;
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
                    if (checkedApps.contains(newInfo.pName)) {
                        if(checkedAppMap.get(newInfo.pName).toString().contains(personName))
                        {
                            Log.d("nevek",checkedAppMap.get(newInfo.pName).toString());
                            checkedValue.add(newInfo.pName);
                        }
                    }
                    res.add(newInfo);
                }
            }

            /*for (AppInfo info : res) {
                if (checkedApps.contains(info.packageName)) {
                    checkedValue.add(info.packageName);

                }
            }*/
            return res;
        }
    }


}

class AppInfo {
    public String appName = "";
    public String pName = "";
    public Drawable appIcon;
}
