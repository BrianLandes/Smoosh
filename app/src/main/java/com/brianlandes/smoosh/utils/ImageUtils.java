package com.brianlandes.smoosh.utils;

import android.content.Context;
import android.net.Uri;

import com.brianlandes.smoosh.structures.ProfilePhoto;
import com.google.firebase.database.DatabaseReference;

import java.io.File;

/**
 * Created by Brian on 7/31/2017.
 */

public class ImageUtils {

    private static final String IMAGE_NAME = "photo_";

    public static Uri GetCacheImageUri(Context context) {
        String destinationFileName = IMAGE_NAME + TimeUtils.Timestamp() + ".png";
        File destFile = new File(context.getCacheDir(), destinationFileName);
        if (destFile.exists())
            destFile.delete();
        return Uri.fromFile(destFile);
    }

    public static void PushProfilePhoto(ProfilePhoto photo) {
        DatabaseReference photoRef = DatabaseUtils.getUserPhotos(null);
        photoRef.child( Integer.toString( photo.position) ).setValue( photo.storageUri.toString() );
    }
}
