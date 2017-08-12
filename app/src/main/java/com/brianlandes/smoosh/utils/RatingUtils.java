package com.brianlandes.smoosh.utils;

import android.util.Log;

import com.brianlandes.smoosh.structures.Smoosher;
import com.brianlandes.smoosh.structures.TagList;
import com.brianlandes.smoosh.structures.UserRating;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by Brian on 8/11/2017.
 */

public class RatingUtils {

    public static final int MAX_TAGS = 8;
    public static final int MIN_TAG_COUNT = 0;

    public static void UpdateRating( Smoosher targetUser, ArrayList<String> tagKeys ) {
        // put the list of tag keys under 'user_ratings/<this user>/<that user>/'
        DatabaseReference sentRef = DatabaseUtils.getRatingsSentBy(null); // current user
        sentRef.child( targetUser.uid ).setValue( tagKeys );

        // put a reference to the list under 'users/<that user>/ratings/<this user>/'
        DatabaseReference recRef = DatabaseUtils.getRatingsAppliedTo(targetUser.uid);
        recRef.child( DatabaseUtils.currentUserUid() ).setValue(tagKeys!=null && tagKeys.size()>0);

        // flag the user as needing a tag update
        DatabaseReference ntuRef = DatabaseUtils.getNeedsTagsUpdate(targetUser.uid );
        ntuRef.setValue(true);
    }

    public static void FinalizeRating( UserRating rating, TagList destList ) {
        // count up the tags, sort them and cull them, then publish
        rating.tags.clear();
        for ( int i = 0; i < MAX_TAGS && rating.tagCounts.size()>0; i ++ ) {
//            Log.d("RatingUtils", Integer.toString(i) + " : " + rating.tagCounts.toString() );
            int highestVal = Integer.MIN_VALUE;
            String highestKey = null;
            for( String key : rating.tagCounts.keySet()) {
                Integer value = rating.tagCounts.get(key);
                if ( value > highestVal && value > MIN_TAG_COUNT ) {
                    highestKey = key;
                    highestVal = value;
                }
            }
            if ( highestKey !=null ) {
                rating.tags.add(highestKey);
                rating.tagCounts.remove(highestKey);
            }
        }
//        Log.d("RatingUtils",rating.tags.toString() );
        DatabaseReference ref = DatabaseUtils.getUserTags(rating.uid);
        ref.setValue( rating.tags );

        // flag the user as not needing a tag update
        DatabaseReference ntuRef = DatabaseUtils.getNeedsTagsUpdate(rating.uid );
        ntuRef.setValue(false);

        //
        destList.Clear();
        Log.d("RatingUtils",rating.tags.toString() );
        TagUtils.FillListFromTagKeys(destList, rating.tags);
    }
}
