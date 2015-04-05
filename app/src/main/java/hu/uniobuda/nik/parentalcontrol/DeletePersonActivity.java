package hu.uniobuda.nik.parentalcontrol;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class DeletePersonActivity extends ListActivity {
    String[] labels;
    String[] names;
    SharedPreferences persons;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        persons = getSharedPreferences(getString(R.string.SHAREDPREFERENCE_PERSONS), Context.MODE_PRIVATE);
        Map map = persons.getAll();
        TreeMap sortedMap = new TreeMap(new ValueComparator(map));
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
            }
            else
            {
                names[i] = name.substring(0,1).toUpperCase()+name.substring(1);
            }
            labels[i] = entry.getKey().toString();
            Log.d("name+label", names[i] + "+" + labels[i]);
            i++;
        }

        setListAdapter(new ArrayAdapter(this, R.layout.activity_delete_person, names));
        final ListView listView = getListView();
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                dialog.setTitle(R.string.deleteAlert);
                final String name = (String) listView.getItemAtPosition(pos);
                dialog.setMessage(getString(R.string.deleteMessage) + "/n" + name);
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
                                int index = Arrays.asList(names).indexOf(name);
                                if (names.length == 1) {
                                    new File(Environment.getExternalStorageDirectory().toString() + "/teszt.xml").delete();
                                } else {
                                    try {
                                        FaceDataEditor.loadXML(Environment.getExternalStorageDirectory().toString() + "/teszt.xml");
                                        Iterator i = FaceDataEditor.faceData.iterator();
                                        while (i.hasNext()) {
                                            if (((FaceData) i.next()).id == Integer.parseInt(labels[index])) {
                                                i.remove();
                                            }
                                        }
                                        FaceDataEditor.writeXML(Environment.getExternalStorageDirectory().toString() + "/teszt.xml");
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ParserConfigurationException e) {
                                        e.printStackTrace();
                                    } catch (SAXException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Editor e = persons.edit();
                                    e.remove(labels[index]);
                                    e.commit();
                                    Arrays.asList(labels).remove(index);
                                    Arrays.asList(names).remove(index);
                                }
                            }
                        });
                dialog.show();
                arg0.postInvalidate();
            }
        });
    }
}
