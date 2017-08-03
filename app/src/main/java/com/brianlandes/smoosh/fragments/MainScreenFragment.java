package com.brianlandes.smoosh.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brianlandes.smoosh.R;

/**
 * Created by Brian on 8/3/2017.
 */

public class MainScreenFragment extends Fragment{

    public MainScreenFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_screen, container, false);

        return rootView;
    }

}
