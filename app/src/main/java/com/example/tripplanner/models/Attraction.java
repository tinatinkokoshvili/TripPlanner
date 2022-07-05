package com.example.tripplanner.models;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import org.parceler.Parcel;
import android.os.Parcel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;


public class Attraction implements Parcelable {
    public transient static final String TAG = "Attraction";
    public transient String formatted_address;
    public transient String formatted_phone_number;
    public transient String latitude;
    public transient String longitude;
    public transient String icon;
    public transient String icon_background_color;
    public transient String icon_mask_base_uri;
    public transient String international_phone_number;
    public transient String name;
    //Boolean open_now;
    //JSONArray weekday_text;
    //JSONArray photos;
    public transient String place_id;
    public transient int price_level;
    public transient int rating;
    //JSONArray reviews;
   // JSONArray types;
    public transient String url;
    public transient int user_ratings_total;
    public transient String vicinity;
    public transient String website;
    public transient Boolean picked;
    public transient Bitmap photo;


    public transient static final String FORMATTED_ADDRESS = "formatted_address";
    public transient static final String FORMATTED_PHONE_NUMBER = "formatted_phone_number";
    public transient static final String GEOMETRY = "geometry";
    public transient static final String LOCATION = "location";
    public transient static final String LAT = "lat";
    public transient static final String LNG = "lng";
    public transient static final String ICON = "icon";
    public transient static final String ICON_BACKGROUND_COLOR = "icon_background_color";
    public transient static final String ICON_MASK_BASE_URI = "icon_mask_base_uri";
    public transient static final String INTERNATIONAL_PHONE_NUMBER = "international_phone_number";
    public transient static final String NAME = "name";
    public transient static final String PLACE_ID = "place_id";
    public transient static final String PRICE_LEVEL = "price_level";
    public transient static final String RATING = "rating";
    public transient static final String URL = "url";
    public transient static final String USER_RATINGS_TOTAL = "user_ratings_total";
    public transient static final String VICINITY = "vicinity";
    public transient static final String WEBSITE = "website";

    public Attraction() {}

    public static Attraction createFromJson(JSONObject resultObject) throws JSONException {
        Attraction attraction = new Attraction();
        attraction.picked = false;
        if (resultObject.has(FORMATTED_ADDRESS))
            attraction.formatted_address = resultObject.getString(FORMATTED_ADDRESS);
        if (resultObject.has(FORMATTED_PHONE_NUMBER))
            attraction.formatted_phone_number = resultObject.getString(FORMATTED_PHONE_NUMBER);
        JSONObject locationObj = resultObject.getJSONObject(GEOMETRY).getJSONObject(LOCATION);
        if (locationObj.has(LAT))
            attraction.latitude = locationObj.getString(LAT);
        if (locationObj.has(LNG))
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
        List<Attraction> attractions = new LinkedList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            attractions.add(createFromJson(jsonArray.getJSONObject(i)));
        }
        return attractions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeString(formatted_address);
        dest.writeString(formatted_phone_number);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(icon);
        dest.writeString(icon_background_color);
        dest.writeString(icon_mask_base_uri);
        dest.writeString(international_phone_number);
        dest.writeString(name);
        dest.writeString(place_id);
        dest.writeInt(price_level);
        dest.writeInt(rating);
        dest.writeString(url);
        dest.writeInt(user_ratings_total);
        dest.writeString(vicinity);
        dest.writeString(website);
        dest.writeBoolean(picked);
//        try {
//            dest.writeByteArray(convert(photo));
//        } catch (IOException e) {
//            Log.e(TAG, "photo bitmap could not be put into Parcelable");
//        }
    }

    public Attraction(Parcel in) {
        formatted_address = in.readString();
        formatted_phone_number = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        icon = in.readString();
        icon_background_color = in.readString();
        icon_mask_base_uri = in.readString();
        international_phone_number = in.readString();
        name = in.readString();
        place_id = in.readString();
        price_level = in.readInt();
        rating = in.readInt();
        url = in.readString();
        user_ratings_total = in.readInt();
        vicinity = in.readString();
        website = in.readString();
        picked = in.readBoolean();
        //byte[] photoBytes = new byte[in.readInt()];
        //in.readByteArray(photoBytes);
    }

    public static byte[] convert(Bitmap bitmap) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] array = stream.toByteArray();
        stream.close();
        return array;


    }
    public static Bitmap convert(byte[] array) {
        return BitmapFactory.decodeByteArray(array,0,array.length);
    }

    public static final Parcelable.Creator<Attraction> CREATOR = new Parcelable.Creator<Attraction>()
    {
        public Attraction createFromParcel(Parcel in)
        {
            return new Attraction(in);
        }
        public Attraction[] newArray(int size)
        {
            return new Attraction[size];
        }
    };
}
