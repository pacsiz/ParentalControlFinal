package hu.uniobuda.nik.parentalcontrol;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import hu.uniobuda.nik.parentalcontrol.fragments.HelpScreenFragment;

public class TabsAdapter extends FragmentPagerAdapter {

    private final int NUMBER_OF_FRAGMENST = 6;
    Context context;

    public TabsAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int index) {

        Bundle bundle = new Bundle();
        bundle.putInt(context.getString(R.string.BUNDLE_FRAGMENT_INDEX), index);
        HelpScreenFragment fragment = new HelpScreenFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return NUMBER_OF_FRAGMENST;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.help_main_screen_title);
            case 1:
                return context.getString(R.string.help_password_title);
            case 2:
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
