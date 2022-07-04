package com.example.tripplanner.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.tripplanner.OnTaskCompleted;
import com.example.tripplanner.R;
import com.example.tripplanner.adapters.PlacesAdapter;
import com.example.tripplanner.apiclient.NearbyPlacesHelper;
import com.example.tripplanner.apiclient.PlacePhotoRequest;
import com.example.tripplanner.models.Attraction;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.w3c.dom.Attr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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
                Intent routeIntent = new Intent(PickAttractionsActivity.this, RouteActivity.class);
                routeIntent.putExtra("list", (Serializable) pickedAttractionsList);
                startActivity(routeIntent);
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

        NearbyPlacesHelper getPlaces = new NearbyPlacesHelper(this);
        getPlaces.execute(dataTransfer);
    }

    @Override
    public void onTaskCompleted(Attraction atr) {
        Log.i(TAG, "Task completed " + atr.name + " adapter size " + placesAdapter.getItemCount());
        try {
            //API calls to get photo of the place
            PlacesClient placesClient = Places.createClient(this);
            final String placeId = atr.place_id;
            final List<Place.Field> fields = Collections.singletonList(Place.Field.PHOTO_METADATAS);
            final FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(placeId, fields);
            placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
                final Place place = response.getPlace();
                final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
                if (metadata == null || metadata.isEmpty()) {
                    Log.w(TAG, "No photo metadata.");
                    return;
                }
                final PhotoMetadata photoMetadata = metadata.get(0);
                final String attributions = photoMetadata.getAttributions();
                // Create a FetchPhotoRequest.
                final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .setMaxWidth(500) // Optional.
                        .setMaxHeight(300) // Optional.
                        .build();
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    atr.photo = bitmap;
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        final ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + exception.getMessage());
                    }
                });
            });
            placesAdapter.add(atr);
        } catch (Exception e) {
            Log.e(TAG, "Json exception", e);
        }
    }

}