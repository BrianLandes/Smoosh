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
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Brian on 8/6/2017.
 */

public class LocationUtils {

    private static GeoFire _geoFire;
    private static FusedLocationProviderClient _fusedLocationClient;

    public static final float MILES_TO_KMS = 1.60934f;

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

    public static void QueryNearySmooshers(final Activity activity, boolean tryGetPermission) {
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
                                    GeoLocation myLoc = new GeoLocation(location.getLatitude(), location.getLongitude());
                                    // let's update the current user's location while we're at it
                                    geoFire.setLocation(DatabaseUtils.currentUserUid(), myLoc);

                                    float kms = AssetUtils.currentSettings.d * MILES_TO_KMS;
                                    GeoQuery geoQuery = geoFire.queryAtLocation(myLoc, kms);

                                    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                                        @Override
                                        public void onKeyEntered(String key, GeoLocation location) {
                                            System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
                                            if ( StringUtils.Equals(key, DatabaseUtils.currentUserUid() ) ) {
                                                // this is the current user
                                            } else {
                                                AssetUtils.AddNearbySmoosher( key );
                                            }
                                        }

                                        @Override
                                        public void onKeyExited(String key) {
                                            System.out.println(String.format("Key %s is no longer in the search area", key));
                                        }

                                        @Override
                                        public void onKeyMoved(String key, GeoLocation location) {
                                            System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
                                        }

                                        @Override
                                        public void onGeoQueryReady() {
                                            System.out.println("All initial data has been loaded and events have been fired!");
                                        }

                                        @Override
                                        public void onGeoQueryError(DatabaseError error) {
                                            System.err.println("There was an error with this query: " + error);
                                        }
                                    });
                                } else {
                                    Log.d("LocUtils","Location was null" );
                                    // todo: use requestLocationUpdates to try and find the location
                                }
                            }
                        });
            } catch (SecurityException e ) { } // we DID check for permissions
        }
    }
}
