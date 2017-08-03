package com.brianlandes.smoosh.utils;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * Created by Brian on 7/31/2017.
 */

public class ImageUtils {

    private static final String IMAGE_NAME = "photo_";

    public static Uri GetCacheImageUri(Context context) {
        String destinationFileName = IMAGE_NAME + Timestamp() + ".png";
        File destFile = new File(context.getCacheDir(), destinationFileName);
        if (destFile.exists())
            destFile.delete();
        Uri destUri = Uri.fromFile(destFile);
        return destUri;
    }

    public static String Timestamp() {
        java.util.Date date= new java.util.Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(date.getTime());
        return timeStamp;
    }

}
