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

        switch (getArguments().getInt("index"))
        {
            case 0:
                return inflater.inflate(R.layout.fragment_main_help_screen, container, false);
            case 1:
                return inflater.inflate(R.layout.fragment_password_help, container, false);
            case 2:
                return inflater.inflate(R.layout.fragment_apps_help, container, false);
            case 3:
                return inflater.inflate(R.layout.fragment_person_help, container, false);
            case 4:
                return inflater.inflate(R.layout.fragment_url_help, container, false);
            case 5:
                return inflater.inflate(R.layout.fragment_deviceaccess_help, container, false);
        }
        return null;
    }
}
