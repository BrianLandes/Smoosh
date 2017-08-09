package com.brianlandes.smoosh.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.brianlandes.smoosh.MainActivity;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Brian on 8/6/2017.
 */

public class LocationUtils {

    private static GeoFire _geoFire;
    private static FusedLocationProviderClient _fusedLocationClient;

    public static GeoFire GeoFire() {
        if ( _geoFire == null ) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("locations");
            _geoFire = new GeoFire(ref);
        }
        return _geoFire;
    }

    public static FusedLocationProviderClient LocationClient(Activity activity) {
        if ( _fusedLocationClient==null ) {
            _fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        }
        return _fusedLocationClient;
    }

//    public static void UpdateUsersLocation( float latitude, float longitude ) {
//        GeoFire geoFire = GeoFire();
//        geoFire.setLocation(DatabaseUtils.currentUserUid(),new GeoLocation(latitude,longitude) );
//    }

    public static void UpdateUsersCourseLocation(final Activity activity, boolean tryGetPermission ) {
        Log.d("LocUtils","UpdateUsersCourseLocation" );
        if (!PermissionsUtils.hasLocation(activity)) {
            if ( tryGetPermission ) {
                // If you do not have permission, request it
                PermissionsUtils.requestLocation(activity, PermissionsUtils.REQUEST_LOCATION_FOR_COURSE_UPDATE);
            }
        } else {
            final FusedLocationProviderClient mFusedLocationClient = LocationClient(activity);
            try {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    GeoFire geoFire = GeoFire();
                                    geoFire.setLocation(DatabaseUtils.currentUserUid(),
                                            new GeoLocation(location.getLatitude(), location.getLongitude()));
                                } else {
                                    Log.d("LocUtils","Location was null" );
                                    LocationRequest mLocationRequest;
                                    mLocationRequest = LocationRequest.create();
                                    mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
                                    mLocationRequest.setInterval(10000);
                                    mLocationRequest.setFastestInterval(5000);
                                    try {
                                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                                new LocationCallback() {
                                                    @Override
                                                    public void onLocationResult(LocationResult locationResult) {
                                                        for (Location location : locationResult.getLocations()) {
                                                            GeoFire geoFire = GeoFire();
                                                            geoFire.setLocation(DatabaseUtils.currentUserUid(),
                                                                    new GeoLocation(location.getLatitude(), location.getLongitude()));
                                                        }
                                                    }
                                                },
                                                null /* Looper */);
                                    } catch (SecurityException e) { } // we DID check for permissions
                                }
                            }
                        });
            } catch (SecurityException e ) { } // we DID check for permissions
        }
    }
}
