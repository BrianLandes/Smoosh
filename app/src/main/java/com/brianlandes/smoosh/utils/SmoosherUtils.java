package com.brianlandes.smoosh.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

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
        AssetUtils.currentUser = Load(DatabaseUtils.currentUserUid());
    }

    public static Smoosher Load( String uid ) {
        final Smoosher smoosher = new Smoosher();
        smoosher.uid = uid;
        DatabaseReference photoRef = DatabaseUtils.getUserPhotos(smoosher.uid);
        Query photoQuery = photoRef.orderByKey();
        photoQuery.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for( DataSnapshot child : dataSnapshot.getChildren() ) {
                    ProfilePhoto photo = ProfilePhoto.from( child );
                    smoosher.AddPhoto(photo);
                }
                smoosher.PhotoStubsWereLoaded();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // todo: handle this shit
            }
        });
        return smoosher;
    }

    public static void FillEditTextWithUserName(@NonNull final TextView editText, @Nullable String uid ) {
        DatabaseReference bioRef = DatabaseUtils.getUserName(uid);

        bioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ( dataSnapshot.exists() ) {
                    String text = dataSnapshot.getValue().toString();
                    editText.setText(text);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // todo: handle this shit
            }
        });
    }

    public static void FillEditTextWithUserBio(@NonNull final TextView editText, @Nullable String uid ) {
        DatabaseReference bioRef = DatabaseUtils.getUserBio(uid);

        bioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ( dataSnapshot.exists() ) {
                    String text = dataSnapshot.getValue().toString();
                    editText.setText(text);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // todo: handle this shit
            }
        });
    }
}
