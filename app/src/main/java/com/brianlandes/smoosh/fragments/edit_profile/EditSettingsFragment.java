package com.brianlandes.smoosh.fragments.edit_profile;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brianlandes.smoosh.R;
import com.brianlandes.smoosh.utils.AssetUtils;
import com.brianlandes.smoosh.utils.DatabaseUtils;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.apptik.widget.MultiSlider;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditSettingsFragment extends Fragment {

    public final String TAG = EditSettingsFragment.class.getSimpleName();

//    @BindView(R.id.range_slider) MultiSlider distanceSlider;
    @BindView(R.id.distance_seek_bar) DiscreteSeekBar distanceSlider;

    private Unbinder unbinder;

    public EditSettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_settings, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        distanceSlider.setProgress(AssetUtils.currentSettings.d);

        distanceSlider.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                Log.d(TAG, Integer.toString(value) );
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
//                AssetUtils.currentSettings.setDistance(seekBar.getProgress());
                AssetUtils.currentSettings.d = seekBar.getProgress();
                DatabaseUtils.SetUserSettings(AssetUtils.currentSettings);
            }
        });

//        distanceSlider.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
//            @Override
//            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
//                Log.d(TAG, Integer.toString(thumbIndex) + " : " + Integer.toString(value) );
////                if (thumbIndex == 0) {
////                    doSmth(String.valueOf(value));
////                } else {
////                    doSmthElse(String.valueOf(value));
////                }
//            }
//        });

        return rootView;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
