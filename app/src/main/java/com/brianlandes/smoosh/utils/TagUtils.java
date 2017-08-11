package com.brianlandes.smoosh.utils;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;

import com.brianlandes.smoosh.structures.Smoosher;
import com.brianlandes.smoosh.structures.Tag;
import com.brianlandes.smoosh.structures.TagList;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Brian on 8/11/2017.
 */

public class TagUtils {

    public static void GetMostUsedTags(@NonNull final TagList destList) {
        DatabaseReference ref = DatabaseUtils.getTags();
        Query query = ref.orderByChild("uses");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Tag> newTags = new ArrayList<>();
                for ( DataSnapshot child: dataSnapshot.getChildren() ) {
                    Tag newTag = child.getValue(Tag.class);
                    newTag.key = child.getKey();
                    newTags.add(newTag);
                }
                destList.AddAll(newTags);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private static Query containingQuery;
    private static TagList containingDestList;
    private static String containingPhrase;
    private static ValueEventListener containingListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            ArrayList<Tag> newTags = new ArrayList<>();
            for ( DataSnapshot child: dataSnapshot.getChildren() ) {
                Tag newTag = child.getValue(Tag.class);
                newTag.key = child.getKey();
                if ( StringUtils.Contains(newTag.phrase,containingPhrase) )
                    newTags.add(newTag);
            }
            containingDestList.RemoveAllThenAddAll(newTags);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public static void GetTagsContaining(@NonNull final String phrase, @NonNull final TagList destList) {
        if ( containingQuery == null ) {
            DatabaseReference ref = DatabaseUtils.getTags();
            containingQuery = ref.orderByChild("uses");
        } else {
            containingQuery.removeEventListener(containingListener);
        }
        containingDestList = destList;
        containingPhrase = phrase;
        containingQuery.addListenerForSingleValueEvent(containingListener);
    }

    public static void GetTagsFromLastReview(@NonNull final TagList destList, Smoosher targetSmoosher ) {
        final DatabaseReference tagRef = DatabaseUtils.getTags();
        DatabaseReference sentRatingsRef = DatabaseUtils.getRatingsSentBy(null); // current user
        sentRatingsRef.child( targetSmoosher.uid ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                ArrayList<String> tagKeys = dataSnapshot.getValue( ArrayList.class );
//                Log.d("TagUtils", tagKeys.toString() );
                for ( DataSnapshot child: dataSnapshot.getChildren() ) {

                    String newKey = child.getValue(String.class);
                    tagRef.child(newKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Tag newTag = dataSnapshot.getValue(Tag.class);
                            newTag.key = dataSnapshot.getKey();
                            destList.Add( newTag );
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void AddTag( String phrase ) {
        // todo: check that this tag doesn't already exist
        Tag tag = new Tag(phrase);
        DatabaseReference ref = DatabaseUtils.getTags();
        ref.push().setValue(tag);
    }

    public static boolean Equals( Tag t1, Tag t2 ) {
        return StringUtils.Equals(t1.phrase,t2.phrase );
    }
}
