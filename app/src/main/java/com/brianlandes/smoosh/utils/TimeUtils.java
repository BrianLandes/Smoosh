package com.brianlandes.smoosh.utils;

import java.text.SimpleDateFormat;

/**
 * Created by Brian on 8/3/2017.
 */

public class TimeUtils {
    public static String Timestamp() {
        java.util.Date date= new java.util.Date();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
                .format(date.getTime());
        return timeStamp;
    }
}
