package com.brianlandes.smoosh.utils;

import com.brianlandes.smoosh.structures.Smoosher;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by Brian on 8/11/2017.
 */

public class RatingUtils {

    public static void UpdateRating( Smoosher targetUser, ArrayList<String> tagKeys ) {
        // put the list of tag keys under 'user_ratings/<this user>/<that user>/'
        DatabaseReference sentRef = DatabaseUtils.getRatingsSentBy(null); // current user
        sentRef.child( targetUser.uid ).setValue( tagKeys );

        // put a reference to the list under 'users/<that user>/ratings/<this user>/'
        DatabaseReference recRef = DatabaseUtils.getRatingsAppliedTo(targetUser.uid);
        recRef.child( DatabaseUtils.currentUserUid() ).setValue(tagKeys!=null && tagKeys.size()>0);
    }

}
