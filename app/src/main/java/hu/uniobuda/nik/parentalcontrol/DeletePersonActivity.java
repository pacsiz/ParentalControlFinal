package hu.uniobuda.nik.parentalcontrol;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import hu.uniobuda.nik.parentalcontrol.identification.BlockerHashTable;
import hu.uniobuda.nik.parentalcontrol.backend.FaceData;
import hu.uniobuda.nik.parentalcontrol.backend.FaceDataEditor;
import hu.uniobuda.nik.parentalcontrol.backend.ValueComparatorInc;

public class DeletePersonActivity extends ActionBarActivity {
    SharedPreferences persons;
    ArrayAdapter adapter;
    TextView personListInfo;
    ListView personlistView;
    ArrayList<String> names = new ArrayList<String>();
    ArrayList<String> labels = new ArrayList<String>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_person);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffd6d6d6")));

        personListInfo = (TextView) findViewById(R.id.personListInfo);
        personlistView = (ListView) findViewById(R.id.personlistView);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        persons = getSharedPreferences(getString(R.string.SHAREDPREFERENCE_PERSONS), Context.MODE_PRIVATE);
        Map personsMap = persons.getAll();
        TreeMap<String, ?> sortedMap = new TreeMap(new ValueComparatorInc(personsMap));
        sortedMap.putAll(personsMap);

        for (Map.Entry<String, ?> entry : sortedMap.entrySet()) {
            String name = entry.getValue().toString();
            if (name.contains("CHILD-")) {
                names.add(name.substring(6, 7).toUpperCase() + name.substring(7));
            } else {
                names.add(name.substring(0, 1).toUpperCase() + name.substring(1));
            }

            labels.add(entry.getKey());
            //Log.d("DeletePersonActivity", "Name: "+name+", " + "label: "+entry.getKey());
        }

        if (names.isEmpty()) {
            personListInfo.setText(R.string.emptyPersonList);
        }

        personlistView.setAdapter(new ArrayAdapter(this, R.layout.personlist_layout, names));

        personlistView.setTextFilterEnabled(true);
        personlistView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                dialog.setTitle(R.string.deleteAlert);
                final String name = (String) personlistView.getItemAtPosition(pos);
                dialog.setMessage(getString(R.string.deleteMessage) + "\n" + name);
                dialog.setNegativeButton(getString(R.string.cancel),
                        new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int arg1) {
                                dialogInterface.dismiss();
                            }
                        });
                dialog.setPositiveButton(R.string.OK,
                        new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int arg1) {
                                int index = names.indexOf(name);
                                deletePersonPackages(names.get(index));
                                new Remover(index).execute();


                            }
                        });
                dialog.show();
            }
        });
    }

    private void deletePersonPackages(String deletedPerson) {
        SharedPreferences packages = getSharedPreferences(getString(R.string.SHAREDPREFERENCE_PACKAGES), Context.MODE_PRIVATE);
        Editor e = packages.edit();
        for (Map.Entry entry : packages.getAll().entrySet()) {
            String packageName = entry.getKey().toString();
            String personName = entry.getValue().toString();
            if (personName.contains(deletedPerson)) {
                personName = personName.replace(":" + deletedPerson, "");
                if (personName.equals("")) {
                    e.remove(packageName);
                    BlockerHashTable.deleteBoolean(packageName);
                } else {
                    e.putString(packageName, personName);
                }
            }
        }
        e.commit();
    }

    private class Remover extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd = new ProgressDialog(DeletePersonActivity.this);
        int index;
        //long deleteTime;

        public Remover(int index) {
            this.index = index;
        }

        @Override
        protected void onPreExecute() {
            //deleteTime = System.nanoTime();
            pd.setTitle(R.string.pleaseWait);
            pd.setMessage(getString(R.string.deleting));
            pd.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            //deleteTime = (System.nanoTime() - deleteTime)/1000000;
            //Log.i("DeletePersonActivity","Delete person time: "+deleteTime+" ms");
            if (names.size() > 0)
            {
                personlistView.setAdapter(new ArrayAdapter(DeletePersonActivity.this, R.layout.personlist_layout, names));
            }

            Editor e = persons.edit();
            e.remove(labels.get(index));

            File sh = new File(getApplicationInfo().dataDir + "/shared_prefs/" + names.get(index).toLowerCase() + ".xml");
            //Log.d("DeletePersonActivity", "Delete person's preference: " + sh.getAbsolutePath());

            if (sh.exists()) {
                sh.delete();
            }
            names.remove(index);
            labels.remove(index);
            e.commit();

            if (names.size() == 0)
            {
                personListInfo.setText(R.string.emptyPersonList);
            }
            pd.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {
            File f = new File(getFilesDir(), getString(R.string.xmlName));
            if (names.size() == 1) {
                f.delete();
            } else {
                try {
                    FaceDataEditor.loadXML(f.getAbsolutePath());
                    Iterator i = FaceDataEditor.faceData.iterator();
                    while (i.hasNext()) {
                        if (((FaceData) i.next()).getId() == Integer.parseInt(labels.get(index))) {
                            i.remove();
                        }
                    }
                    FaceDataEditor.writeXML(f.getAbsolutePath());
                } catch (InterruptedException | ParserConfigurationException | SAXException | IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
