package com.brianlandes.smoosh.structures;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Brian on 8/3/2017.
 */

public class Smoosher {
    public String displayName;
    public ArrayList<ProfilePhoto> profilePhotos;

    public Smoosher() {
        profilePhotos = new ArrayList<ProfilePhoto>();
    }

    public ProfilePhoto AddPhoto( Uri uri ) {
        ProfilePhoto newPhoto = new ProfilePhoto();
        newPhoto.localUri = uri;
        newPhoto.position = profilePhotos.size();
        newPhoto.id = new Random().nextLong();
        profilePhotos.add( newPhoto );
        return newPhoto;
    }
}
