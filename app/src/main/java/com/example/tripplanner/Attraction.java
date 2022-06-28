package com.example.tripplanner;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Attraction {
    private static final String TAG = "Attraction";
    private String place_id;
    private String name;
    private String business_status;
    private String opening_hours;
    private String address;
    private String location;
    private String picture;

    public Attraction() {}

    public static Attraction fromJson(JSONObject jsonObject) throws JSONException {
        Attraction attraction = new Attraction();
        // Set attributes
        return attraction;
    }

    public static List<Attraction> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Attraction> attractions = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            attractions.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return attractions;
    }

}
