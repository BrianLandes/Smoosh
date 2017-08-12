package com.brianlandes.smoosh.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.brianlandes.smoosh.structures.SmoosherSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Brian on 8/3/2017.
 */

public class DatabaseUtils {
    static final String TAG = DatabaseUtils.class.getSimpleName();

    private static FirebaseDatabase _database = null;
    private static String _currentUid = null;

    public static FirebaseDatabase Database() {
        if ( _database == null )
            _database = FirebaseDatabase.getInstance();
        return _database;
    }

    public static String currentUserUid() {
        if ( _currentUid == null )
            _currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return _currentUid;
    }

    public static void Reset() {
        _database = null;
        _currentUid = null;
    }

    public static DatabaseReference getUserPhotos( @Nullable String uid ) {
        FirebaseDatabase database = Database();
        if ( uid == null )
            uid = currentUserUid();
        String path = "photos/" + uid + "/";
        return database.getReference( path );
    }

    public static DatabaseReference getUserBio( @Nullable String uid ) {
        FirebaseDatabase database = Database();
        if ( uid == null )
            uid = currentUserUid();
        String path = "users/" + uid + "/bio";
        return database.getReference( path );
    }

    public static void SetUserBio( @NonNull String text ) {
        DatabaseReference bioRef = DatabaseUtils.getUserBio(null);
        bioRef.setValue( text);
    }

    public static DatabaseReference getUserName( @Nullable String uid ) {
        FirebaseDatabase database = Database();
        if ( uid == null )
            uid = currentUserUid();
        String path = "users/" + uid + "/name";
        return database.getReference( path );
    }

    public static void SetUserName( @NonNull String text ) {
        DatabaseReference bioRef = DatabaseUtils.getUserName(null);
        bioRef.setValue( text);
    }

    public static DatabaseReference getUserSettings( @Nullable String uid ) {
        FirebaseDatabase database = Database();
        if ( uid == null )
            uid = currentUserUid();
        String path = "settings/" + uid + "/";
        return database.getReference( path );
    }

    public static void SetUserSettings( SmoosherSettings settings ) {
        DatabaseReference ref = DatabaseUtils.getUserSettings(null);
        ref.setValue( settings );
    }

    public static DatabaseReference getTags() {
        FirebaseDatabase database = Database();
        String path = "tags/";
        return database.getReference( path );
    }

    public static DatabaseReference getUserRatings() {
        FirebaseDatabase database = Database();
        String path = "user_ratings/";
        return database.getReference( path );
    }

    public static DatabaseReference getRatingsSentBy( @Nullable String uid ) {
        FirebaseDatabase database = Database();
        if ( uid == null )
            uid = currentUserUid();
        String path = "user_ratings/" + uid;
        return database.getReference( path );
    }

    public static DatabaseReference getRatingsAppliedTo( @Nullable String uid ) {
        FirebaseDatabase database = Database();
        if ( uid == null )
            uid = currentUserUid();
        String path = "users/" + uid + "/ratings/";
        return database.getReference( path );
    }

    public static DatabaseReference getLikes( @Nullable String uid ) {
        FirebaseDatabase database = Database();
        if ( uid == null )
            uid = currentUserUid();
        String path = "users/" + uid + "/likes/";
        return database.getReference( path );
    }

    public static DatabaseReference getNeedsTagsUpdate( @Nullable String uid ) {
        FirebaseDatabase database = Database();
        if ( uid == null )
            uid = currentUserUid();
        String path = "users/" + uid + "/ntu/";
        return database.getReference( path );
    }

    public static DatabaseReference getUserTags( @Nullable String uid ) {
        FirebaseDatabase database = Database();
        if ( uid == null )
            uid = currentUserUid();
        String path = "users/" + uid + "/tags/";
        return database.getReference( path );
    }

//    public static SmoosherSettings GetUserSettings() {
//        DatabaseReference ref = DatabaseUtils.getUserSettings(null);
//        SmoosherSettings settings = ref.
//    }
}
