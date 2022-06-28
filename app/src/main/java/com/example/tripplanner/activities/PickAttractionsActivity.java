package com.example.tripplanner.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.tripplanner.R;
import com.example.tripplanner.apiclient.GetNearbyPlaces;

public class PickAttractionsActivity extends AppCompatActivity {
    private static final String TAG = "PickAttractionsActivity";
    private String placesBaseUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final String API_KEY = "AIzaSyCe2kjKuINrKzh9bvmGa-ToZiEvluGRzwU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_attractions);

        double latitude = getIntent().getDoubleExtra("latitude", 0);
        double longitude = getIntent().getDoubleExtra("longitude", 0);
        Log.i(TAG, "latlang " + latitude + " long " + longitude);
        fetchPlaces(latitude, longitude);
    }

    void fetchPlaces(double latitude, double longitude) {
        StringBuilder stringBuilder = new StringBuilder(placesBaseUrl);
        stringBuilder.append("location=" + latitude + "%2C" + longitude);
        stringBuilder.append("&radius=" + "6000");
        stringBuilder.append("&type=" + "tourist_attraction");
        //ranks by prominence by default
        stringBuilder.append("&rankby");
        stringBuilder.append("&key=" + API_KEY);
        String url = stringBuilder.toString();
        Object dataTransfer[] = new Object[1];
        dataTransfer[0] = url;

        GetNearbyPlaces getPlaces = new GetNearbyPlaces();
        getPlaces.execute(dataTransfer);
    }

}