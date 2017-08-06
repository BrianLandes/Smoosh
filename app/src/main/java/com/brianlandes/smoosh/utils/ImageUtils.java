package com.brianlandes.smoosh.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.brianlandes.smoosh.structures.ProfilePhoto;
import com.brianlandes.smoosh.structures.QuickCallback;
import com.google.firebase.database.DatabaseReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Created by Brian on 7/31/2017.
 */

public class ImageUtils {

    private static final String IMAGE_NAME = "photo_";

    public static int arbitraryCacheIncrementer = 101;

    public static Uri GetCacheImageUri(Context context) {
        return Uri.fromFile(GetCacheImageFile(context));
    }

    public static File GetCacheImageFile(Context context) {
        String destinationFileName = IMAGE_NAME
                + TimeUtils.Timestamp()
                + Integer.toString( arbitraryCacheIncrementer++ )
                + ".png";
        File destFile = new File(context.getCacheDir(), destinationFileName);
        if (destFile.exists())
            destFile.delete();
        return destFile;
    }

    public static void PushProfilePhoto(ProfilePhoto photo) {
        DatabaseReference photoRef = DatabaseUtils.getUserPhotos(null);
        photoRef.child( Integer.toString( photo.position) ).setValue( photo.asLite() );
    }

    public static void FillImageViewWithUrl(final ImageView imageView, String url, @Nullable final QuickCallback postCallback ) {
        new AsyncTask<String, Integer, Bitmap>() {

            @Override
            protected Bitmap doInBackground(String... params) {
                String path = params[0];
                return getBitmapFromURL(path);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                if ( result!=null ) {
                    imageView.setImageBitmap(result);
                    if ( postCallback!=null )
                        postCallback.Activate( result );
                }
            }
        }
        .execute( url );
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Uri SaveBitmapToCache(Context context, Bitmap inImage) {
        File file = GetCacheImageFile(context);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Uri.fromFile(file );
    }

    /**
     * Either simply sets the image view to display the photo if it is already loaded localy,
     * or downloads it from the online storage, fills the image view after it is loaded, saves the image
     * to the cache, and updates the photo's local pointer.
     * @param imageView
     * @param photo
     */
    public static void FillImageViewWithProfilePhoto(@NonNull final Context context,
                                                     @NonNull final ImageView imageView,
                                                     @NonNull final ProfilePhoto photo) {
        imageView.setImageURI(null);
        if ( photo.localUri!=null ) {
            imageView.setImageURI(photo.localUri);
        } else if ( photo.storageUri!=null ) {
            FillImageViewWithUrl(
                    imageView,
                    photo.storageUri.toString(),
                    new QuickCallback() {
                        @Override
                        public void Activate(Object... objects) {
                            Bitmap bitmap = (Bitmap) objects[0];
                            if ( bitmap!=null ) {
                                photo.localUri = ImageUtils.SaveBitmapToCache(context,bitmap);
                            }
                        }
                    });
        }
    }
}
