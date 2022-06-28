package com.example.tripplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
        stringBuilder.append("&radius=" + "1500");
        stringBuilder.append("&type=" + "tourist_attraction");
        stringBuilder.append("&key=" + API_KEY);
        String url = stringBuilder.toString();
    //    String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522%2C151.1957362&radius=1500&type=restaurant&keyword=cruise&key=AIzaSyCe2kjKuINrKzh9bvmGa-ToZiEvluGRzwU";
        Object dataTransfer[] = new Object[1];
        dataTransfer[0] = url;
        new GetNearbyPlaces().execute(dataTransfer);
    }


}