package com.brianlandes.smoosh.utils;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brian on 8/11/2017.
 */

public class SnapshotUtils {

    public static boolean getValueAsBool(DataSnapshot snapshot, boolean onNotExists) {
        if ( !snapshot.exists() )
            return onNotExists;
        return (Boolean)snapshot.getValue();
    }

    public static ArrayList<String> getValueAsListOfStrings(DataSnapshot snapshot) {
        ArrayList<String> list = new ArrayList<>();
        for ( DataSnapshot child: snapshot.getChildren() )
            list.add( child.getValue(String.class) );
        return list;
    }
}
