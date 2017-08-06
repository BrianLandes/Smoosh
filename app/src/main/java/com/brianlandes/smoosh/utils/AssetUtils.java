package com.brianlandes.smoosh.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

import com.brianlandes.smoosh.structures.Smoosher;

import java.util.HashMap;

/**
 * Created by Brian on 8/3/2017.
 */

public class AssetUtils {

    public static Smoosher currentUser;

    public static HashMap<String,Smoosher> smooshers = new HashMap<>();

    public static DisplayMetrics displayMetrics = new DisplayMetrics();

    public static void init( Activity activity ) {
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }
}
