package com.example.tripplanner.activities;

import androidx.appcompat.app.AppCompatActivity;
import com.example.tripplanner.R;
import com.example.tripplanner.apiclient.DistanceMatrixHelper;
import com.example.tripplanner.apiclient.NearbyPlacesHelper;
import com.example.tripplanner.models.Attraction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class RouteActivity extends AppCompatActivity {
    private static final String TAG = "RouteActivity";
    List<Attraction> pickedAtrList;
    private static final String distanceMatrixBaseUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";
    private static final String API_KEY = "AIzaSyCe2kjKuINrKzh9bvmGa-ToZiEvluGRzwU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        Intent intent = getIntent();
        pickedAtrList = (List<Attraction>) intent.getSerializableExtra("list");
        Log.i(TAG, "picked attraction" + pickedAtrList.size());
        fetchDistances();
    }

    private void fetchDistances() {
        for (int i = 0; i < pickedAtrList.size(); i++) {
            for (int j = 0; j < pickedAtrList.size(); j++) {
                StringBuilder stringBuilder = new StringBuilder(distanceMatrixBaseUrl);
                stringBuilder.append("&origins=");
                Attraction origin = pickedAtrList.get(i);
                Attraction destination = pickedAtrList.get(j);
                stringBuilder.append(origin.latitude);
                stringBuilder.append("%2C");
                stringBuilder.append(origin.longitude);
                stringBuilder.append("&destinations=");
                stringBuilder.append(destination.latitude);
                stringBuilder.append("%2C");
                stringBuilder.append(destination.longitude);
                stringBuilder.append("&key=" + API_KEY);
                String url = stringBuilder.toString();
                Object dataTransfer[] = new Object[1];
                dataTransfer[0] = url;
                new DistanceMatrixHelper().execute(dataTransfer);
            }
        }
    }
}