package com.brianlandes.smoosh.structures;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by Brian on 8/9/2017.
 */

public class SmoosherSettings {

    public int d = 1; // max search distance

    public int mna = 18; // min age
    public int mxa = 99; // max age

    public boolean sm = false; // show males
    public boolean sf = false; // show females

    public void assimilate(DataSnapshot snapshot ) {
        this.d = ((Long)snapshot.child("d").getValue()).intValue();
//        this.mna = (long)snapshot.child("mna").getValue();
//        this.mxa = (long)snapshot.child("mxa").getValue();
    }

//    public int getDistance() {
//        return d;
//    }
//
//    public void setDistance(int d) {
//        this.d = d;
//    }

}
