package hu.uniobuda.nik.parentalcontrol;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ChildListActivty extends ActionBarActivity {


    TextView childListInfo;
    ListView chilListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_list_activty);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffd6d6d6")));

        childListInfo = (TextView)findViewById(R.id.childListInfo);
        chilListView = (ListView)findViewById(R.id.childlistView);

        SharedPreferences persons = getSharedPreferences(getString(R.string.SHAREDPREFERENCE_PERSONS), Context.MODE_PRIVATE);
        Map map = persons.getAll();
        TreeMap<String, ?> sortedMap = new TreeMap(new ValueComparatorInc(map));
        sortedMap.putAll(map);
        ArrayList<String> list = new ArrayList<>();

        for (Map.Entry<String, ?> entry : sortedMap.entrySet()) {
            String name = entry.getValue().toString();
            if (name.contains("CHILD-")) {
                list.add(name.substring(6, 7).toUpperCase() + name.substring(7));
            }
        }

        if(list.isEmpty())
        {
            childListInfo.setText(R.string.emptyChildList);
        }

        chilListView.setAdapter(new ArrayAdapter(this, R.layout.personlist_layout, list));


        chilListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = chilListView.getItemAtPosition(position).toString();
                Intent i = new Intent(ChildListActivty.this,
                        ChildPreferencesActivity.class);
                i.putExtra(getString(R.string.EXTRA_PERSON_NAME), name);
                startActivity(i);
            }
        });
    }


}
