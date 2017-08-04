package com.brianlandes.smoosh.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Brian on 8/3/2017.
 */

public class StorageUtils {

    private static FirebaseStorage _storage = null;

    public static FirebaseStorage Storage() {
        if (_storage==null)
            _storage = FirebaseStorage.getInstance();
        return _storage;
    }

    public static StorageReference getPhotos() {
        return Storage().getReference().child("photos/");
    }

    public static void UploadPhoto(Context context, Uri uri, OnSuccessListener<UploadTask.TaskSnapshot> successCallback ) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] byteArray = baos.toByteArray();

            String videoName = DatabaseUtils.currentUserUid() + "_" + TimeUtils.Timestamp() + ".png";
            StorageReference photosRef = getPhotos().child(videoName);

            UploadTask uploadTask = photosRef.putBytes(byteArray);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // TODO: Handle unsuccessful uploads
                }
            }).addOnSuccessListener(successCallback);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
