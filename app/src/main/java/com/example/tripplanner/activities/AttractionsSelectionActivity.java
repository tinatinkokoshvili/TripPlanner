package com.example.tripplanner.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.tripplanner.interfaces.OnTaskCompleted;
import com.example.tripplanner.R;
import com.example.tripplanner.adapters.PlacesAdapter;
import com.example.tripplanner.api_client.NearbyPlacesHelper;
import com.example.tripplanner.models.Attraction;
import com.example.tripplanner.models.Restaurant;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AttractionsSelectionActivity extends AppCompatActivity implements OnTaskCompleted {
    private static final String TAG = "PickAttractionsActivity";
    private String placesBaseUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final String API_KEY = "AIzaSyCe2kjKuINrKzh9bvmGa-ToZiEvluGRzwU";
    private Button btnGenerate;
    private RecyclerView rvPlaces;
    private PlacesAdapter placesAdapter;
    private List<Attraction> attractionsList;
    private ArrayList<Attraction> pickedAttractionsList;

    private String tripName;
    private String radius;
    private String totalTime;
    private String avgStayTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attractions_selection);

        tripName = getIntent().getStringExtra("tripName");
        radius = getIntent().getStringExtra("radius");
        totalTime = getIntent().getStringExtra("totalTime");
        avgStayTime = getIntent().getStringExtra("avgStayTime");
        double latitude = getIntent().getDoubleExtra("latitude", 0);
        double longitude = getIntent().getDoubleExtra("longitude", 0);
        pickedAttractionsList = new ArrayList<>();
        btnGenerate = findViewById(R.id.btnGenerate);
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizePickedList();
                goToRouteActivity(latitude, longitude);
            }
        });
        rvPlaces = (RecyclerView) findViewById(R.id.rvAttractions);
        attractionsList = new LinkedList<>();
        placesAdapter = new PlacesAdapter(this, attractionsList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvPlaces.setLayoutManager(linearLayoutManager);
        rvPlaces.setAdapter(placesAdapter);

        fetchPlaces(latitude, longitude);
    }

    private void goToRouteActivity(double latitude, double longitude) {
        // Add current location to the list of picked attractions
        Attraction userLocation = new Attraction("User Location", true,
                Double.toString(latitude), Double.toString(longitude));
        pickedAttractionsList.add(userLocation);
        Intent routeIntent = new Intent(AttractionsSelectionActivity.this, RouteActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("data", pickedAttractionsList);
        routeIntent.putExtras(bundle);
        routeIntent.putExtra("tripName", tripName);
        routeIntent.putExtra("radius", radius);
        routeIntent.putExtra("totalTime", totalTime);
        routeIntent.putExtra("avgStayTime", avgStayTime);
        // Pass the location user looked up so that we can include it in the final route
        startActivity(routeIntent);
    }

    private void finalizePickedList() {
        pickedAttractionsList.clear();
        for (int i = 0; i < attractionsList.size(); i++) {
            Attraction currAtr = attractionsList.get(i);
            if (currAtr.picked) {
                pickedAttractionsList.add(currAtr);
                Log.i(TAG, "putting picked atr " + currAtr.name);
            }
        }
        Log.i(TAG, "finalizedList Size " + pickedAttractionsList.size());
    }

    public void fetchPlaces(double latitude, double longitude) {
        StringBuilder stringBuilder = new StringBuilder(placesBaseUrl);
        stringBuilder.append("location=" + latitude + "%2C" + longitude);
        stringBuilder.append("&radius=" + Integer.parseInt(radius));
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
        try {
            Attraction atrWithPhoto = getPhotoBitmap(atr);

        } catch (Exception e) {
            Log.e(TAG, "Json exception", e);
        }
    }

    public Attraction getPhotoBitmap(Attraction atr) {
        //API calls to get photo of the place
        PlacesClient placesClient = Places.createClient(this);
        final String placeId = atr.placeId;
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
                    .build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                atr.photo = bitmap;
                placesAdapter.add(atr);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    final ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                }
            });
        });
        return atr;
    }

    @Override
    public void onDurationTaskCompleted(int[][] distanceMatrix) {
        return;
    }

    @Override
    public void onRestaurantTaskCompleted(Attraction attraction, Restaurant restaurant, int numOfTotalRestaurants) {
        return;
    }

    @Override
    public void addNullToYelpList() {
        return;
    }
}