package hu.uniobuda.nik.parentalcontrol;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;


public class ChildListActivty extends ListActivity {

    SharedPreferences persons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_device_access);
        persons = getSharedPreferences(getString(R.string.SHAREDPREFERENCE_PERSONS), Context.MODE_PRIVATE);
        Map map = persons.getAll();
        TreeMap<String, ?> sortedMap = new TreeMap(new ValueComparatorInc(map));
        sortedMap.putAll(map);
        ArrayList<String> list = new ArrayList<>();
        String[] names = new String[sortedMap.size()];

        for (Map.Entry<String, ?> entry : sortedMap.entrySet())
        {
            String name = entry.getValue().toString();
            if (name.contains("CHILD-"))
            {
                list.add(name.substring(6,7).toUpperCase()+name.substring(7));
            }
        }

        setListAdapter(new ArrayAdapter(this, R.layout.activity_delete_person, list));
        final ListView listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = listView.getItemAtPosition(position).toString();
                Intent i = new Intent(ChildListActivty.this,
                        ChildPreferencesActivity.class);
                i.putExtra(getString(R.string.EXTRA_PERSON_NAME),name);
                startActivity(i);
            }
        });
    }
}
