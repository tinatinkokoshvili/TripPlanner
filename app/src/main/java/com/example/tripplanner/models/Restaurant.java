package com.example.tripplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class Restaurant implements Parcelable {
    public transient static final String TAG = "Restaurant";
    public transient Boolean picked;
    public transient String restaurantId;
    public transient String name;
    public transient String image_url;
    public transient Boolean is_claimed;
    public transient Boolean is_closed;
    public transient String url;
    public transient String display_phone;
    public transient String review_count;
    public transient String rating;
    public transient String address;
    public transient String latitude;
    public transient String longitude;
    public transient List<String> photos;
    public transient String hours;

    public transient static final String ID = "id";
    public transient static final String NAME = "name";
    public transient static final String IMAGE_URL = "image_url";
    public transient static final String IS_CLAIMED = "is_claimed";
    public transient static final String IS_CLOSED = "is_closed";
    public transient static final String URL = "url";
    public transient static final String DISPLAY_PHONE = "display_phone";
    public transient static final String REVIEW_COUNT = "review_count";
    public transient static final String RATING = "rating";
    public transient static final String LOCATION = "location";
    public transient static final String DISPLAY_ADDRESS = "display_address";
    public transient static final String COORDINATES = "coordinates";
    public transient static final String LATITUDE = "latitude";
    public transient static final String LONGITUDE = "longitude";
    public transient static final String PHOTOS = "photos";

    public Restaurant() {}

    protected Restaurant(Parcel in) {
    }

    public static Restaurant createFromJson(JSONObject resultObject) throws JSONException {
        Restaurant restaurant = new Restaurant();
        restaurant.picked = false;
        restaurant.restaurantId = resultObject.getString(ID);
        restaurant.name = resultObject.getString(NAME);
        restaurant.image_url = resultObject.getString(IMAGE_URL);
        restaurant.is_claimed = resultObject.getBoolean(IS_CLAIMED);
        restaurant.is_closed = resultObject.getBoolean(IS_CLOSED);
        restaurant.url = resultObject.getString(URL);
        restaurant.display_phone = resultObject.getString(DISPLAY_PHONE);
        restaurant.review_count = resultObject.getString(REVIEW_COUNT);
        restaurant.rating = resultObject.getString(RATING);
        restaurant.address = resultObject.getJSONObject(LOCATION).getJSONArray(DISPLAY_ADDRESS).getString(0)
                + "\n" + resultObject.getJSONObject(LOCATION).getJSONArray(DISPLAY_ADDRESS).getString(1);
        restaurant.latitude = resultObject.getJSONObject(COORDINATES).getString(LATITUDE);
        restaurant.longitude = resultObject.getJSONObject(COORDINATES).getString(LONGITUDE);
        restaurant.photos = new LinkedList<>();
        for (int i = 0; i < resultObject.getJSONArray(PHOTOS).length(); i++) {
            restaurant.photos.add(resultObject.getJSONArray(PHOTOS).getString(i));
        }
        return restaurant;
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
