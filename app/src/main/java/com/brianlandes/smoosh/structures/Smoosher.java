package com.brianlandes.smoosh.structures;

import android.net.Uri;
import android.provider.ContactsContract;

import com.brianlandes.smoosh.utils.ImageUtils;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Brian on 8/3/2017.
 */

public class Smoosher {
    public String displayName;
    public String uid;
    public ArrayList<ProfilePhoto> profilePhotos;

    public Smoosher() {
        profilePhotos = new ArrayList<ProfilePhoto>();
    }

    public ProfilePhoto AddPhoto( Uri uri ) {
        ProfilePhoto newPhoto = new ProfilePhoto();
        newPhoto.localUri = uri;
        return AddPhoto(newPhoto);
    }

    public ProfilePhoto AddPhoto( ProfilePhoto photo ) {
        photo.position = profilePhotos.size();
        photo.id = new Random().nextLong();
        profilePhotos.add( photo );
        return photo;
    }

    public void ReorderPhotos() {
        int i = 0;
        for (ProfilePhoto photo : profilePhotos ) {
            photo.position = i++;
            ImageUtils.PushProfilePhoto(photo);
        }
    }
}
