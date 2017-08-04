package com.brianlandes.smoosh.utils;

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

    public static DatabaseReference getUserPhotos(String uid ) {
        FirebaseDatabase database = Database();
        if ( uid == null )
            uid = currentUserUid();
        String path = "photos/" + uid + "/";
        return database.getReference( path );
    }
}
