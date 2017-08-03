package com.brianlandes.smoosh.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.brianlandes.smoosh.AppSettings;
import com.brianlandes.smoosh.R;
import com.labo.kaji.fragmentanimations.MoveAnimation;

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

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return MoveAnimation.create(MoveAnimation.RIGHT, enter, AppSettings.TRANSITION_DURATION);
//        } else if (transit == FragmentTransaction.TRANSIT_FRAGMENT_CLOSE ) {
//            return MoveAnimation.create(MoveAnimation.RIGHT, enter, AppSettings.TRANSITION_DURATION);
        } else {
            return MoveAnimation.create(MoveAnimation.LEFT, enter, AppSettings.TRANSITION_DURATION);
        }
    }
}
