package hu.uniobuda.nik.parentalcontrol;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class HelpActivity extends ActionBarActivity {

    ViewPager viewPager;
    ActionBar actionBar;
    TabsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        viewPager = (ViewPager) findViewById(R.id.pager);
        //actionBar = getSupportActionBar();
        adapter = new TabsAdapter(getSupportFragmentManager(), HelpActivity.this);

        viewPager.setAdapter(adapter);
        //actionBar.setHomeButtonEnabled(false);
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
       /* for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }*/
    }

}
