package com.brianlandes.smoosh.fragments.matching;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brianlandes.smoosh.R;
import com.brianlandes.smoosh.utils.LocationUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class MatchFragment extends Fragment {


    public MatchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_match, container, false);
        LocationUtils.UpdateUsersCourseLocation(getActivity(),true);
        return rootView;
    }

}
