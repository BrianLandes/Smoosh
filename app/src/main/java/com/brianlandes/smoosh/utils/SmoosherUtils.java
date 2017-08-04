package com.brianlandes.smoosh.utils;

import android.util.Log;

import com.brianlandes.smoosh.structures.ProfilePhoto;
import com.brianlandes.smoosh.structures.Smoosher;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

/**
 * Created by Brian on 8/3/2017.
 */

public class SmoosherUtils {

    public static final String TAG = SmoosherUtils.class.getSimpleName();

    public static void LoadCurrent() {
        final Smoosher smoosher = new Smoosher();
        smoosher.uid = DatabaseUtils.currentUserUid();
        DatabaseReference photoRef = DatabaseUtils.getUserPhotos(smoosher.uid);
        Query photoQuery = photoRef.orderByKey();
        photoQuery.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for( DataSnapshot child : dataSnapshot.getChildren() ) {
//                    Log.d(TAG, child.child("storagePath").getValue().toString() );
                    ProfilePhoto photo = ProfilePhoto.from( child );
                    smoosher.AddPhoto(photo);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // todo: handle this shit
            }
        });
        AssetUtils.currentUser = smoosher;
    }
}
