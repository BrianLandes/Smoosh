package com.brianlandes.smoosh.structures;

import android.net.Uri;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by Brian on 8/3/2017.
 */

public class ProfilePhoto {

    public Uri localUri;
    public Uri storageUri;
    public int position;
    public long id;

    public static ProfilePhoto from( DataSnapshot snapshot ) {
        ProfilePhoto photo = new ProfilePhoto();
        photo.storageUri = Uri.parse( snapshot.child("s").getValue().toString());
        return photo;
    }

    public Lite asLite() {
        Lite l = new Lite();
        l.s = storageUri.toString();
        return l;
    }

    public class Lite {
        public String s;
    }
}
