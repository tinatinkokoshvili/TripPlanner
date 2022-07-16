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
    public transient String placeId;
    public transient int price_level;
    public transient int rating;
    //JSONArray reviews;
   // JSONArray types;
    public transient String url;
    public transient int user_ratings_total;
    public transient String vicinity;
    public transient String website;
    public transient boolean picked;
    public transient Bitmap photo;
    public transient boolean isRestaurant;

    public transient String street_number;
    public transient String street;
    public transient String subpremise;
    public transient String address1;
    public transient String city;
    public transient String short_state;
    public transient String country;


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
    public transient static final String TYPES = "types";

    public transient static final String ADDRESS_COMPONENTS = "address_components";

    public transient static final String LONG_NAME = "long_name";
    public transient static final String SHORT_NAME = "short_name";
    public transient static final String STREET_NUMBER = "street_number";
    public transient static final String ROUTE = "route";
    public transient static final String SUBPREMISE = "subpremise";
    public transient static final String LOCALITY = "locality";
    public transient static final String ADMINISTRATIVE_AREA_LEVEL_1 = "administrative_area_level_1";
    public transient static final String COUNTRY = "country";

    public Attraction() {}

    public static Attraction createFromJson(JSONObject resultObject) throws JSONException {
        Attraction attraction = new Attraction();
        attraction.picked = false;
        JSONArray address_components = resultObject.getJSONArray(ADDRESS_COMPONENTS);
        for (int i = 0; i < address_components.length(); i++) {
            JSONObject curObject = address_components.getJSONObject(i);
            for (int j = 0; j < curObject.getJSONArray(TYPES).length(); j++) {
                if (curObject.getJSONArray(TYPES).getString(j).equals(STREET_NUMBER)) {
                    attraction.street_number = curObject.getString(SHORT_NAME);
                    Log.i(TAG, "street_number " + attraction.street_number);
                }
                if (curObject.getJSONArray(TYPES).getString(j).equals(ROUTE)) {
                    attraction.street = curObject.getString(SHORT_NAME);
                    Log.i(TAG, "street " + attraction.street);
                }
                if (curObject.getJSONArray(TYPES).getString(j).equals(SUBPREMISE)) {
                    attraction.subpremise = curObject.getString(SHORT_NAME);
                    Log.i(TAG, "subpremise " + attraction.subpremise);
                }
                if (curObject.getJSONArray(TYPES).getString(j).equals(LOCALITY)) {
                    attraction.city = curObject.getString(SHORT_NAME);
                    Log.i(TAG, "city " + attraction.city);
                }
                if (curObject.getJSONArray(TYPES).getString(j).equals(ADMINISTRATIVE_AREA_LEVEL_1)) {
                    attraction.short_state = curObject.getString(SHORT_NAME);
                    Log.i(TAG, "short_state " + attraction.short_state);
                }
                if (curObject.getJSONArray(TYPES).getString(j).equals(COUNTRY)) {
                    attraction.country = curObject.getString(SHORT_NAME);
                    Log.i(TAG, "country " + attraction.country);
                }
            }
        }
        attraction.address1 = attraction.street_number + attraction.street;
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
            attraction.placeId = resultObject.getString(PLACE_ID);
        if (resultObject.has(PRICE_LEVEL))
            attraction.price_level = resultObject.getInt(PRICE_LEVEL);
        if (resultObject.has(RATING))
            attraction.rating = resultObject.getInt(RATING);
//        if (resultObject.has("reviews"))
//            attraction.reviews = resultObject.getJSONArray("reviews");
        if (resultObject.has("types"))
            for (int i = 0; i < resultObject.getJSONArray("types").length(); i++) {
                if (resultObject.getJSONArray("types").get(i).equals("restaurant")) {
                    attraction.isRestaurant = true;
                }
            }
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

    public String getTAG() {
        return TAG;
    }

    public String get_formatted_address() {
        return formatted_address;
    }

    public String get_formatted_phone_number() {
        return formatted_phone_number;
    }

    public String get_latitude() {
        return latitude;
    }

    public String get_longitude() {
        return longitude;
    }

    public String get_icon() {
        return icon;
    }

    public String get_icon_background_color() {
        return icon_background_color;
    }

    public String get_icon_mask_base_uri() {
        return icon_mask_base_uri;
    }

    public String get_international_phone_number() {
        return international_phone_number;
    }

    public String getName() {
        return name;
    }

    public String getPlaceId() {
        return placeId;
    }

    public int price_level() {
        return price_level;
    }

    public int get_rating() {
        return rating;
    }

    public String get_url() {
        return url;
    }

    public int get_user_ratings_total() {
        return user_ratings_total;
    }

    public String get_vicinity() {
        return vicinity;
    }

    public String get_website() {
        return website;
    }

    public boolean get_picked() {
        return picked;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public boolean get_isRestaurant() {
        return isRestaurant;
    }

    public String get_street_number() {
        return street_number;
    }

    public String get_street() {
        return street;
    }

    public String get_subpremise() {
        return subpremise;
    }

    public String get_address1() {
        return address1;
    }

    public String get_city() {
        return city;
    }

    public String get_short_state() {
        return short_state;
    }

    public String get_country() {
        return country;
    }

    public String get_static_FORMATTED_ADDRESS() {
        return FORMATTED_ADDRESS;
    }

    public String get_static_FORMATTED_PHONE_NUMBER() {
        return FORMATTED_PHONE_NUMBER;
    }
    public String get_static_GEOMETRY() {
        return GEOMETRY;
    }
    public String get_static_LOCATION() {
        return LOCATION;
    }
    public String get_static_LAT() {
        return LAT;
    }

    public String get_static_LNG() {
        return LNG;
    }

    public String get_static_ICON() {
        return ICON;
    }

    public String get_static_ICON_BACKGROUND_COLOR() {
        return ICON_BACKGROUND_COLOR;
    }

    public String get_static_ICON_MASK_BASE_URI() {
        return ICON_MASK_BASE_URI;
    }

    public String get_static_INTERNATIONAL_PHONE_NUMBER() {
        return INTERNATIONAL_PHONE_NUMBER;
    }

    public String get_static_NAME() {
        return NAME;
    }

    public String get_static_PLACE_ID() {
        return PLACE_ID;
    }

    public String get_static_PRICE_LEVEL() {
        return PRICE_LEVEL;
    }

    public String get_static_RATING() {
        return RATING;
    }
    public String get_static_URL() {
        return URL;
    }
    public String get_static_USER_RATINGS_TOTAL() {
        return USER_RATINGS_TOTAL;
    }
    public String get_static_VICINITY() {
        return VICINITY;
    }

    public String get_static_WEBSITE() {
        return WEBSITE;
    }
    public String get_static_TYPES() {
        return TYPES;
    }
    public String get_static_ADDRESS_COMPONENTS() {
        return ADDRESS_COMPONENTS;
    }
    public String get_static_LONG_NAME() {
        return LONG_NAME;
    }
    public String get_static_SHORT_NAME() {
        return SHORT_NAME;
    }
    public String get_static_STREET_NUMBER() {
        return STREET_NUMBER;
    }

    public String get_static_ROUTE() {
        return ROUTE;
    }
    public String get_static_SUBPREMISE() {
        return SUBPREMISE;
    }
    public String get_static_LOCALITY() {
        return LOCALITY;
    }

    public String get_static_ADMINISTRATIVE_AREA_LEVEL_1() {
        return ADMINISTRATIVE_AREA_LEVEL_1;
    }

    public String get_static_COUNTRY() {
        return COUNTRY;
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
        dest.writeString(placeId);
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
        placeId = in.readString();
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
