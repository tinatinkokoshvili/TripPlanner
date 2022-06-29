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
    public Boolean picked;


    private static final String FORMATTED_ADDRESS = "formatted_address";
    private static final String FORMATTED_PHONE_NUMBER = "formatted_phone_number";
    private static final String GEOMETRY = "geometry";
    private static final String LOCATION = "location";
    private static final String LAT = "lat";
    private static final String LNG = "lng";
    private static final String ICON = "icon";
    private static final String ICON_BACKGROUND_COLOR = "icon_background_color";
    private static final String ICON_MASK_BASE_URI = "icon_mask_base_uri";
    private static final String INTERNATIONAL_PHONE_NUMBER = "international_phone_number";
    private static final String NAME = "name";
    private static final String PLACE_ID = "place_id";
    private static final String PRICE_LEVEL = "price_level";
    private static final String RATING = "rating";
    private static final String URL = "url";
    private static final String USER_RATINGS_TOTAL = "user_ratings_total";
    private static final String VICINITY = "vicinity";
    private static final String WEBSITE = "website";

    public Attraction() {
        picked = false;
    }

    public static Attraction createFromJson(JSONObject resultObject) throws JSONException {
        Attraction attraction = new Attraction();
        if (resultObject.has(FORMATTED_ADDRESS))
            attraction.formatted_address = resultObject.getString(FORMATTED_ADDRESS);
        if (resultObject.has(FORMATTED_PHONE_NUMBER))
            attraction.formatted_phone_number = resultObject.getString(FORMATTED_PHONE_NUMBER);
        JSONObject locationObj = resultObject.getJSONObject(GEOMETRY).getJSONObject(LOCATION);
        if (resultObject.has(LAT))
            attraction.latitude = locationObj.getString(LAT);
        if (resultObject.has(LNG))
            attraction.longitude = locationObj.getString(LNG);
        if (resultObject.has(ICON))
            attraction.icon = resultObject.getString(ICON);
        if (resultObject.has(ICON_BACKGROUND_COLOR))
            attraction.icon_background_color = resultObject.getString(ICON_BACKGROUND_COLOR);
        if (resultObject.has(ICON_MASK_BASE_URI))
            attraction.icon_mask_base_uri = resultObject.getString(ICON_MASK_BASE_URI);
        if (resultObject.has(INTERNATIONAL_PHONE_NUMBER))
            attraction.international_phone_number = resultObject.getString(INTERNATIONAL_PHONE_NUMBER);
        if (resultObject.has(NAME))
            attraction.name = resultObject.getString(NAME);
//        if (resultObject.getJSONObject("opening_hours").has("open_now"))
//            attraction.open_now = resultObject.getJSONObject("opening_hours").getBoolean("open_now");
//        if (resultObject.getJSONObject("opening_hours").has("weekday_text"))
//            attraction.weekday_text = resultObject.getJSONObject("opening_hours").getJSONArray("weekday_text");
//        if (resultObject.getJSONObject("opening_hours").has("weekday_text"))
//            attraction.photos = resultObject.getJSONArray("photos");
        if (resultObject.has(PLACE_ID))
            attraction.place_id = resultObject.getString(PLACE_ID);
        if (resultObject.has(PRICE_LEVEL))
            attraction.price_level = resultObject.getInt(PRICE_LEVEL);
        if (resultObject.has(RATING))
            attraction.rating = resultObject.getInt(RATING);
//        if (resultObject.has("reviews"))
//            attraction.reviews = resultObject.getJSONArray("reviews");
//        if (resultObject.has("types"))
//            attraction.types = resultObject.getJSONArray("types");
        if (resultObject.has(URL))
            attraction.url = resultObject.getString(URL);
        if (resultObject.has(USER_RATINGS_TOTAL))
            attraction.user_ratings_total = resultObject.getInt(USER_RATINGS_TOTAL);
        if (resultObject.has(VICINITY))
            attraction.vicinity = resultObject.getString(VICINITY);
        if (resultObject.has(WEBSITE))
            attraction.website = resultObject.getString(WEBSITE);
        return attraction;
    }

    public static List<Attraction> createFromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Attraction> attractions = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            attractions.add(createFromJson(jsonArray.getJSONObject(i)));
        }
        return attractions;
    }

}
