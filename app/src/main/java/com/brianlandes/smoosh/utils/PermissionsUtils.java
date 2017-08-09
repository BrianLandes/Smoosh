package com.brianlandes.smoosh.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.brianlandes.smoosh.MainActivity;
import com.brianlandes.smoosh.R;
import com.brianlandes.smoosh.fragments.edit_profile.EditPhotosFragment;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;

/**
 * Created by Brian on 8/7/2017.
 */

public class PermissionsUtils {

    public static final int REQUEST_LOCATION_FOR_COURSE_UPDATE = 1024;
    public static final int REQUEST_READ_STORAGE_FOR_UPLOAD_PHOTOS = 1025;

    private static EditPhotosFragment editPhotosFragment; // work-around hack for uploading photos

    public static void onRequestPermissionsResult(Activity activity, int requestCode, @NonNull String[] permissions,
                                                  @NonNull int[] grantResults) {
        // Called when you request permission to read and write to external storage
        switch (requestCode) {
            case REQUEST_LOCATION_FOR_COURSE_UPDATE: {
                if (grantResults.length == 2
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    LocationUtils.UpdateUsersCourseLocation(activity, false);
                } else {
                    // If you do not get permission, show a Toast
                    MyDynamicToast.errorMessage(activity, activity.getString(R.string.location_permission_denied));
                }
                break;
            }
            case REQUEST_READ_STORAGE_FOR_UPLOAD_PHOTOS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera
                    editPhotosFragment.LaunchPicChooser();
                } else {
                    // If you do not get permission, show a Toast
                    MyDynamicToast.errorMessage(activity, activity.getString(R.string.photos_permission_denied));
                }
                break;
            }

        }
    }

    public static boolean hasLocation(Activity activity) {
        return ContextCompat.checkSelfPermission(activity,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestLocation(Activity activity, int code) {
        ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                code);
    }

    public static boolean hasReadStorage(Activity activity) {
        return ContextCompat.checkSelfPermission(activity,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestReadStorage(Activity activity, int code) {
        ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                code);
    }

    // work-around hack for launching the uploading photos fragment
    public static void requestReadStorage(EditPhotosFragment fragment) {
        editPhotosFragment = fragment;
        ActivityCompat.requestPermissions(fragment.getActivity(), new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_READ_STORAGE_FOR_UPLOAD_PHOTOS);
    }
}
