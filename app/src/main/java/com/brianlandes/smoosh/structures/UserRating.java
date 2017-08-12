package com.brianlandes.smoosh.structures;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Brian on 8/11/2017.
 */

public class UserRating {
//    public String from = ""; // uid
//    public String to = ""; // uid
//
    public String uid = "";

    public ArrayList<String> tags = new ArrayList<>(); // tag keys
    public int expectedRatings = 0;
    public int actualRatings = 0;

    public HashMap<String, Integer> tagCounts = new HashMap<>();
}
