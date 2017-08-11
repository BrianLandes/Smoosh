package com.brianlandes.smoosh.utils;

/**
 * Created by Brian on 8/9/2017.
 */

public class StringUtils {

    public static boolean Equals( String s1, String s2 ) {
        int code1 = s1.hashCode();
        int code2 = s2.hashCode();
        return code1 == code2;
    }

    public static boolean Contains( String bigString, String substring ) {
        return bigString.contains(substring);
    }
}
