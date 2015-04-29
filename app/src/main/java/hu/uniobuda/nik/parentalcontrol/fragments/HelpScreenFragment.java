package hu.uniobuda.nik.parentalcontrol.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hu.uniobuda.nik.parentalcontrol.R;

public class HelpScreenFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        int layoutIndex = 0;
        Log.d("arg",getArguments().getInt("index")+"");
        switch (getArguments().getInt("index"))
        {
            case 0:
                //layoutIndex = R.layout.fragment_main_help_screen;
                return inflater.inflate(R.layout.fragment_main_help_screen, container, false);
            case 1:
                //layoutIndex = R.layout.fragment_password_help;
                return inflater.inflate(R.layout.fragment_password_help, container, false);
            case 2:
                //layoutIndex = R.layout.fragment_apps_help;
                return inflater.inflate(R.layout.fragment_apps_help, container, false);
            case 3:
                //layoutIndex = R.layout.fragment_person_help;
                return inflater.inflate(R.layout.fragment_person_help, container, false);
            case 4:
                //layoutIndex = R.layout.fragment_url_help;
                return inflater.inflate(R.layout.fragment_url_help, container, false);
            case 5:
                //layoutIndex = R.layout.fragment_deviceaccess_help;
                return inflater.inflate(R.layout.fragment_deviceaccess_help, container, false);
        }
        //Log.d("layoutindex",layoutIndex+"");
        return null;
    }
}