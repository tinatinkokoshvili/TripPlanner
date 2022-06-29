package com.example.tripplanner.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.tripplanner.OnTaskCompleted;
import com.example.tripplanner.R;
import com.example.tripplanner.adapters.PlacesAdapter;
import com.example.tripplanner.apiclient.GetNearbyPlaces;
import com.example.tripplanner.models.Attraction;

import org.w3c.dom.Attr;

import java.util.ArrayList;
import java.util.List;

public class PickAttractionsActivity extends AppCompatActivity implements OnTaskCompleted {
    private static final String TAG = "PickAttractionsActivity";
    private String placesBaseUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final String API_KEY = "AIzaSyCe2kjKuINrKzh9bvmGa-ToZiEvluGRzwU";
    private Button btnGenerate;
    private RecyclerView rvPlaces;
    private PlacesAdapter placesAdapter;
    private List<Attraction> attractionsList;
    private List<Attraction> pickedAttractionsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_attractions);

        pickedAttractionsList = new ArrayList<>();
        btnGenerate = findViewById(R.id.btnGenerate);
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizePickedList();
            }
        });
        rvPlaces = (RecyclerView) findViewById(R.id.rvPlaces);
        attractionsList = new ArrayList<>();
        placesAdapter = new PlacesAdapter(this, attractionsList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvPlaces.setLayoutManager(linearLayoutManager);
        rvPlaces.setAdapter(placesAdapter);

        double latitude = getIntent().getDoubleExtra("latitude", 0);
        double longitude = getIntent().getDoubleExtra("longitude", 0);
        Log.i(TAG, "latlang " + latitude + " long " + longitude);
        fetchPlaces(latitude, longitude);
    }

    private void finalizePickedList() {
        pickedAttractionsList.clear();
        for (int i = 0; i < attractionsList.size(); i++) {
            Attraction currAtr = attractionsList.get(i);
            if (currAtr.picked) {
                pickedAttractionsList.add(currAtr);
            }
        }
        Log.i(TAG, "finalizedList Size " + pickedAttractionsList.size());
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

        GetNearbyPlaces getPlaces = new GetNearbyPlaces(this);
        getPlaces.execute(dataTransfer);
    }

    @Override
    public void onTaskCompleted(Attraction atr) {
        Log.i(TAG, "Task completed " + atr.name + " adapter size " + placesAdapter.getItemCount());
        try {
            placesAdapter.add(atr);
        } catch (Exception e) {
            Log.e(TAG, "Json exception", e);
        }
    }

}