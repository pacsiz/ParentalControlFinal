package hu.uniobuda.nik.parentalcontrol;

import java.util.ArrayList;
import java.util.List;

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

public class FilterAppsActivity extends Activity implements OnItemClickListener {
    ProgressDialog loading;
    AppListAdapter adapter;
    ListView appList;
    Button btnSave;
    Button btnBack;
    List<AppInfo> list;
    ArrayList<String> checkedValue = new ArrayList<String>();
    SharedPreferences checkedApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_apps);
        checkedApps = getSharedPreferences("apps", Context.MODE_PRIVATE);
        appList = (ListView) findViewById(R.id.appList);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnBack = (Button) findViewById(R.id.btnBack);
        checkedValue.clear();
        new backgroundLoadAppList().execute();
        //list = getInstalledApps();
        //adapter = new AppListAdapter(this, R.layout.applist_layout, list);
        //appList.setAdapter(adapter);
        //appList.setOnItemClickListener(this);
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
                Toast.makeText(FilterAppsActivity.this, "" + checkedValue, Toast.LENGTH_LONG).show();
                Editor e = checkedApps.edit();
                e.clear();
                BlockerHashTable.clear();
                for (String pckg : checkedValue) {
                    e.putBoolean(pckg, true);
                    BlockerHashTable.setBoolean(pckg, true);
                    //Log.d("save", pckg);
                }
                e.putBoolean("hu.uniobuda.nik.parentalcontrol", true);
                BlockerHashTable.setBoolean("hu.uniobuda.nik.parentalcontrol", true);
                e.putBoolean("com.android.settings", true);
                BlockerHashTable.setBoolean("com.android.settings", true);
                e.commit();
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
            appList.setOnItemClickListener(FilterAppsActivity.this);
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
                    //Log.d("appname", p.packageName);
                    //Log.d("appname", newInfo.pName);
                    res.add(newInfo);
                }
            }

            for (AppInfo info : res) {
                if (checkedApps.contains(info.pName)) {
                    checkedValue.add(info.pName);
                }
            }
            return res;
        }
    }

    @Override
    public void onItemClick(AdapterView arg0, View v, int position, long arg3) {
        //Log.d("esemeny", "pipa");
        CheckBox cb = (CheckBox) v.findViewById(R.id.appChk);

        if (cb.isChecked()) {
            Log.d("esemeny", list.get(position).pName);
            checkedValue.remove(list.get(position).pName);
            cb.setChecked(false);

        } else {
            Log.d("esemeny", "checkvizsgaN");
            checkedValue.add(list.get(position).pName);
            cb.setChecked(true);
        }
        cb.performClick();
    }
}

class AppInfo {
    public String appName = "";
    public String pName = "";
    public Drawable appIcon;
}
