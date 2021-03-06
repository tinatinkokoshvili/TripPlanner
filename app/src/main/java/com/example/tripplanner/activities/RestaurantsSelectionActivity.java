package com.example.tripplanner.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

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
import java.util.List;

public class RestaurantsSelectionActivity extends AppCompatActivity implements OnTaskCompleted {
    private static final String TAG = "RestaurantsSelectionActivity";
    private String placesBaseUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final String API_KEY = "AIzaSyCe2kjKuINrKzh9bvmGa-ToZiEvluGRzwU";

    private String tripName;
    private String radius;
    private String totalTime;
    private String avgStayTime;

    private double userLatitude;
    private double userLongitude;
    private ArrayList<Attraction> alreadyPickedAtrList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants_selection);

        Bundle bundle = getIntent().getExtras();
        alreadyPickedAtrList = bundle.getParcelableArrayList("data");
        userLatitude = Double.parseDouble(alreadyPickedAtrList.get(alreadyPickedAtrList.size() - 1).getLat());
        userLongitude = Double.parseDouble(alreadyPickedAtrList.get(alreadyPickedAtrList.size() - 1).getLon());
        tripName = getIntent().getStringExtra("tripName");
        radius = getIntent().getStringExtra("radius");
        totalTime = getIntent().getStringExtra("totalTime");
        avgStayTime = getIntent().getStringExtra("avgStayTime");
        fetchRestaurants(userLatitude, userLongitude);
    }

    void fetchRestaurants(double latitude, double longitude) {
        StringBuilder stringBuilder = new StringBuilder(placesBaseUrl);
        stringBuilder.append("location=" + latitude + "%2C" + longitude);
        stringBuilder.append("&radius=" + "6000");
        stringBuilder.append("&type=" + "restaurant");
        //ranks by prominence by default
        stringBuilder.append("&rankby");
        stringBuilder.append("&key=" +API_KEY);
        String url = stringBuilder.toString();
        Object dataTransfer[] = new Object[1];
        dataTransfer[0] = url;

        NearbyPlacesHelper getPlaces = new NearbyPlacesHelper(RestaurantsSelectionActivity.this);
        getPlaces.execute(dataTransfer);
    }

    @Override
    public void onTaskCompleted(Attraction restaurant) {
        return;
    }

    @Override
    public void onDurationTaskCompleted(int[][] durationMatrix) {
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
        return atr;
    }
}