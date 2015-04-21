package hu.uniobuda.nik.parentalcontrol;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import hu.uniobuda.nik.parentalcontrol.fragments.HelpScreenFragment;


/**
 * Created by Pacsiz on 2015.04.20..
 */
public class TabsAdapter extends FragmentPagerAdapter {

    Context context;
    public TabsAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int index) {

        Bundle bundle = new Bundle();
        bundle.putInt("index",index);
        Log.d("index", index+"");
        HelpScreenFragment fragment = new HelpScreenFragment();
        fragment.setArguments(bundle);
        return fragment;
        /*switch (index) {
            case 0:
                // Top Rated fragment activity
                return new HelpScreenFragment();
            case 1:
                // Games fragment activity
                return new StartServiceFragment();
            case 2:
                // Movies fragment activity
                return new SetNewPersonFragment();
        }
        return null;*/
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 6;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                // Top Rated fragment activity
                return context.getString(R.string.help_main_screen_title);
            case 1:
                // Games fragment activity
                return context.getString(R.string.help_password_title);
            case 2:
                // Movies fragment activity
                return context.getString(R.string.help_apps_title);
            case 3:
                return context.getString(R.string.help_person_title);
            case 4:
                return context.getString(R.string.help_url_title);
            case 5:
                return context.getString(R.string.help_deviceAccess_title);
        }
        return null;
    }
}
