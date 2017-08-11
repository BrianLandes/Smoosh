package com.brianlandes.smoosh.structures;

import com.google.firebase.database.Exclude;

/**
 * Created by Brian on 8/11/2017.
 */

public class Tag {

    @Exclude public String key;
    public String phrase = "#";
    public int uses = 1;

    @Exclude public boolean selected = false;

    public Tag() {}
    public Tag( String phrase ) {
        this.phrase = phrase.toLowerCase();
    }
}
