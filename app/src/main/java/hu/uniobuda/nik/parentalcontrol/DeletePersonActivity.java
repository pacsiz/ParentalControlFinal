package hu.uniobuda.nik.parentalcontrol;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class DeletePersonActivity extends ListActivity {
    String[] labels;
    String[] names;
    SharedPreferences persons;
    ArrayAdapter adapter;
    ArrayList<String> nn = new ArrayList<String>();
    ArrayList<String> ll = new ArrayList<String>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        persons = getSharedPreferences(getString(R.string.SHAREDPREFERENCE_PERSONS), Context.MODE_PRIVATE);
        Map map = persons.getAll();
        TreeMap sortedMap = new TreeMap(new ValueComparatorInc(map));
        sortedMap.putAll(map);
        Iterator iterator = sortedMap.entrySet().iterator();
        int size = sortedMap.size();

        names = new String[size];
        labels = new String[size];
        int i = 0;

        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String name = entry.getValue().toString();
            if (name.contains("CHILD-"))
            {
                names[i] = name.substring(6,7).toUpperCase()+name.substring(7);
                nn.add(name.substring(6,7).toUpperCase()+name.substring(7));
            }
            else
            {
                names[i] = name.substring(0,1).toUpperCase()+name.substring(1);
                nn.add(name.substring(0,1).toUpperCase()+name.substring(1));
            }
            labels[i] = entry.getKey().toString();
            ll.add(entry.getKey().toString());
            Log.d("name+label", names[i] + "+" + labels[i]);
            i++;
        }

        adapter = new ArrayAdapter(this, R.layout.activity_delete_person, nn);
        setListAdapter(adapter);
        final ListView listView = getListView();
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                dialog.setTitle(R.string.deleteAlert);
                final String name = (String) listView.getItemAtPosition(pos);
                dialog.setMessage(getString(R.string.deleteMessage) + "\n" + name);
                dialog.setNegativeButton(getString(R.string.cancel),
                        new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                            }
                        });
                dialog.setPositiveButton("OK",
                        new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                //int index = Arrays.asList(names).indexOf(name);
                                int index = nn.indexOf(name);
                                new Remover(index).execute();
                                //if (names.length == 1) {
                               // File f = new File(getFilesDir(),getString(R.string.xmlName));
                                /*if(nn.size() == 1){
                                    f.delete();
                                } else {
                                    try {

                                        FaceDataEditor.loadXML(f.getAbsolutePath());
                                        Iterator i = FaceDataEditor.faceData.iterator();
                                        while (i.hasNext()) {
                                            if (((FaceData) i.next()).id == Integer.parseInt(labels[index])) {
                                                i.remove();
                                            }
                                        }
                                        FaceDataEditor.writeXML(f.getAbsolutePath());
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ParserConfigurationException e) {
                                        e.printStackTrace();
                                    } catch (SAXException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }


                                }*/
                               // Arrays.asList(labels).remove(index);
                               // Arrays.asList(names).remove(index);

                                Editor e = persons.edit();
                                e.remove(ll.get(index));
                                Log.d("name", nn.get(index));
                                e.apply();
                                File sh = new File(getApplicationInfo().dataDir+"/shared_prefs/"+nn.get(index)+".xml");
                                Log.d("sh_path", sh.getAbsolutePath());
                                if(sh.exists())
                                {
                                    sh.delete();
                                }
                                nn.remove(index);
                                ll.remove(index);
                                adapter.notifyDataSetChanged();
                            }
                        });
                dialog.show();

            }
        });
    }

    private class Remover extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd = new ProgressDialog(DeletePersonActivity.this);
        int index;
        public Remover(int index)
        {
            this.index = index;
        }
        @Override
        protected void onPreExecute() {
            pd.setTitle(R.string.pleaseWait);
            pd.setMessage(getString(R.string.deleting));
            pd.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            pd.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {
            File f = new File(getFilesDir(), getString(R.string.xmlName));
            if (nn.size() == 1) {
                f.delete();
            } else {
                try {

                    FaceDataEditor.loadXML(f.getAbsolutePath());
                    Iterator i = FaceDataEditor.faceData.iterator();
                    while (i.hasNext()) {
                        if (((FaceData) i.next()).id == Integer.parseInt(labels[index])) {
                            i.remove();
                        }
                    }
                    FaceDataEditor.writeXML(f.getAbsolutePath());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

    }
}
