package hu.uniobuda.nik.parentalcontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class URLActivity extends Activity {

    Button addUrl;
    Button deleteUrl;
    Button saveUrl;
    ListView urlListView;
    ArrayList<String> urls;
    ArrayList<String> tempDeletedUrls;
    SharedPreferences sh;
    URLLIstAdapter adapter;
    Editor e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url);
        sh = getSharedPreferences(getString(R.string.SHAREDPREFERENCE_SETTINGS), Context.MODE_PRIVATE);
        if (!sh.getBoolean(getString(R.string.SHAREDPREFERENCE_URL_ENABLED), false)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.failTitle);
            dialog.setMessage(getString(R.string.urlDisabled));
            dialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    });
            dialog.show();
        }
        sh = getSharedPreferences(getString(R.string.SHAREDPREFERENCE_URLS), Context.MODE_PRIVATE);
        addUrl = (Button) findViewById(R.id.btnUrlAdd);
        deleteUrl = (Button) findViewById(R.id.btnUrlDelete);
        saveUrl = (Button) findViewById(R.id.btnUrlSave);
        urlListView = (ListView) findViewById(R.id.urlListView);
        e = sh.edit();
        new backgroundLoadURLS().execute();
        tempDeletedUrls = new ArrayList<>();

        urlListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.urlCheckBox);
                checkBox.performClick();
                if (checkBox.isChecked()) {
                    Log.d("position", Integer.toString(position));
                    Log.d("url_meret_onclick", Integer.toString(urls.size()));
                    Log.d("url", urls.get(position));
                    tempDeletedUrls.add(urls.get(position));
                } else {
                    tempDeletedUrls.remove(urls.get(position));
                }
            }
        });

        addUrl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(URLActivity.this);
                alert.setTitle(R.string.url_add_title);
                alert.setMessage(getString(R.string.url_add_message));

                final EditText input = new EditText(URLActivity.this);
                alert.setView(input);

                alert.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        e.putString(input.getText().toString(), "");
                        urls.add(input.getText().toString());
                        adapter.updateItemCheckedSize(urls.size());
                        adapter.notifyDataSetChanged();
                    }
                });

                alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        deleteUrl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String url : tempDeletedUrls) {
                    e.remove(url);
                    urls.remove(url);
                }

                adapter.updateItemCheckedSize(urls.size());
                adapter.notifyDataSetChanged();

            }
        });

        saveUrl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                e.commit();
                if (ServiceInfo.isServiceRunning(CheckService.class, URLActivity.this)) {
                    setURLS(tempDeletedUrls, urls);
                }
                finish();
            }
        });
    }

    private void setURLS(final ArrayList<String> unBlockURL, final ArrayList<String> blockURL) {
        new Thread() {
            @Override
            public void run() {
                for (String url : unBlockURL) {
                    IPTablesAPI.unblockDomain(url);
                }
                for (String url : blockURL) {
                    IPTablesAPI.blockDomain(url);
                }
            }
        }.start();
    }

    public class backgroundLoadURLS extends
            AsyncTask<Void, Void, ArrayList<String>> {

        ProgressDialog pd = new ProgressDialog(URLActivity.this);

        @Override
        protected void onPreExecute() {
            pd.setTitle(R.string.pleaseWait);
            pd.setMessage(getString(R.string.loadingApps));
            pd.show();
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            adapter = new URLLIstAdapter(URLActivity.this, R.layout.urllist_layout, result);
            urlListView.setAdapter(adapter);
            urls = result;
            Log.d("url_meret", Integer.toString(urls.size()));
            pd.dismiss();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            ArrayList<String> temp = new ArrayList<>();
            Map<String, ?> map = sh.getAll();
            //Map sortedMap = new TreeMap(new ValueComparator(map));
            //sortedMap.putAll(map);
            for (Map.Entry entry : map.entrySet()) {
                temp.add(entry.getKey().toString());
            }
            return temp;
        }
    }
}


