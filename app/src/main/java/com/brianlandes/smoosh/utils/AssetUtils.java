package com.brianlandes.smoosh.utils;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;

import com.brianlandes.smoosh.MainActivity;
import com.brianlandes.smoosh.R;
import com.brianlandes.smoosh.structures.QuickCallback;
import com.brianlandes.smoosh.structures.Smoosher;
import com.brianlandes.smoosh.structures.SmoosherSettings;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Brian on 8/3/2017.
 */

public class AssetUtils {

    public static Smoosher currentUser;

    public static SmoosherSettings currentSettings;

    public static HashMap<String,Smoosher> smooshers = new HashMap<>();

    public static DisplayMetrics displayMetrics = new DisplayMetrics();

    public static ArrayList<String> nearbySmooshers = new ArrayList<String>();
    public static ArrayList<QuickCallback> nearbySmooshersLoadedListeners = new ArrayList<>();

    public static void init( Activity activity ) {
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    public static void AddNearbySmoosher( String uid ) {
        if ( !nearbySmooshers.contains( uid ) ) {
            // todo: check this smoosher against already matched smooshers
            // todo: check that this smoosher meets our criteria
            nearbySmooshers.add( uid );

            for( QuickCallback callback: nearbySmooshersLoadedListeners ) {
                callback.Activate( uid );
            }

            // todo: load smoosher
        }
    }

    public static void ShowFragment( Activity activity, Fragment fragment ) {
        FragmentManager fragmentManager = ((MainActivity)activity).getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    public static void ShowFragmentNoBack( Activity activity, Fragment fragment ) {
        FragmentManager fragmentManager = ((MainActivity)activity).getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
//                .addToBackStack(null)
                .commit();
    }
}
