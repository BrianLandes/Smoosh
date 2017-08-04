package com.brianlandes.smoosh.utils;

import android.content.Context;
import android.net.Uri;

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

}
