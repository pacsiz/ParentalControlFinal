package hu.uniobuda.nik.parentalcontrol;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import hu.uniobuda.nik.parentalcontrol.fragments.MainHelpScreenFragment;
import hu.uniobuda.nik.parentalcontrol.fragments.SetNewPersonFragment;
import hu.uniobuda.nik.parentalcontrol.fragments.StartServiceFragment;

/**
 * Created by Pacsiz on 2015.04.20..
 */
public class TabsAdapter extends FragmentPagerAdapter {

    public TabsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return new MainHelpScreenFragment();
            case 1:
                // Games fragment activity
                return new StartServiceFragment();
            case 2:
                // Movies fragment activity
                return new SetNewPersonFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }
}
