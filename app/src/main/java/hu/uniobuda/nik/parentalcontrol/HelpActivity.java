package hu.uniobuda.nik.parentalcontrol;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import hu.uniobuda.nik.parentalcontrol.adapters.TabsAdapter;

public class HelpActivity extends ActionBarActivity {

    ViewPager viewPager;
    TabsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffd6d6d6")));

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new TabsAdapter(getSupportFragmentManager(), HelpActivity.this);

        viewPager.setAdapter(adapter);

    }

}
