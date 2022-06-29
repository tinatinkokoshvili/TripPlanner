package com.example.tripplanner.models;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Attraction {
    private static final String TAG = "Attraction";
    public String formatted_address;
    public String formatted_phone_number;
    public String latitude;
    public String longitude;
    public String icon;
    public String icon_background_color;
    public String icon_mask_base_uri;
    public String international_phone_number;
    public String name;
    //Boolean open_now;
    //JSONArray weekday_text;
    //JSONArray photos;
    public String place_id;
    public int price_level;
    public int rating;
    //JSONArray reviews;
   // JSONArray types;
    public String url;
    public int user_ratings_total;
    public String vicinity;
    public String website;

    public Attraction() {}

    public static Attraction fromJson(JSONObject resultObject) throws JSONException {
        Attraction attraction = new Attraction();
        if (resultObject.has("formatted_address"))
            attraction.formatted_address = resultObject.getString("formatted_address");
        if (resultObject.has("formatted_phone_number"))
            attraction.formatted_phone_number = resultObject.getString("formatted_phone_number");
        JSONObject locationObj = resultObject.getJSONObject("geometry").getJSONObject("location");
        if (resultObject.has("lat"))
            attraction.latitude = locationObj.getString("lat");
        if (resultObject.has("lng"))
            attraction.longitude = locationObj.getString("lng");
        if (resultObject.has("icon"))
            attraction.icon = resultObject.getString("icon");
        if (resultObject.has("icon_background_color"))
            attraction.icon_background_color = resultObject.getString("icon_background_color");
        if (resultObject.has("icon_mask_base_uri"))
            attraction.icon_mask_base_uri = resultObject.getString("icon_mask_base_uri");
        if (resultObject.has("international_phone_number"))
            attraction.international_phone_number = resultObject.getString("international_phone_number");
        if (resultObject.has("name"))
            attraction.name = resultObject.getString("name");
//        if (resultObject.getJSONObject("opening_hours").has("open_now"))
//            attraction.open_now = resultObject.getJSONObject("opening_hours").getBoolean("open_now");
//        if (resultObject.getJSONObject("opening_hours").has("weekday_text"))
//            attraction.weekday_text = resultObject.getJSONObject("opening_hours").getJSONArray("weekday_text");
//        if (resultObject.getJSONObject("opening_hours").has("weekday_text"))
//            attraction.photos = resultObject.getJSONArray("photos");
        if (resultObject.has("place_id"))
            attraction.place_id = resultObject.getString("place_id");
        if (resultObject.has("price_level"))
            attraction.price_level = resultObject.getInt("price_level");
        if (resultObject.has("rating"))
            attraction.rating = resultObject.getInt("rating");
//        if (resultObject.has("reviews"))
//            attraction.reviews = resultObject.getJSONArray("reviews");
//        if (resultObject.has("types"))
//            attraction.types = resultObject.getJSONArray("types");
        if (resultObject.has("url"))
            attraction.url = resultObject.getString("url");
        if (resultObject.has("user_ratings_total"))
            attraction.user_ratings_total = resultObject.getInt("user_ratings_total");
        if (resultObject.has("vicinity"))
            attraction.vicinity = resultObject.getString("vicinity");
        if (resultObject.has("website"))
            attraction.website = resultObject.getString("website");
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
