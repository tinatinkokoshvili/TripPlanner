package com.example.tripplanner.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.example.tripplanner.BusinessRankHelper;
import com.example.tripplanner.OnTaskCompleted;
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

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AtrDetailsActivity extends AppCompatActivity implements OnTaskCompleted {
    private static final String TAG = "AtrDetailsActivity";
    private String placesBaseUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final String API_KEY = "AIzaSyCe2kjKuINrKzh9bvmGa-ToZiEvluGRzwU";
    private ImageView ivAtrPhoto;
    private Attraction attraction;

    private RecyclerView rvRestaurants;
    private PlacesAdapter restaurantsAdapter;
    private List<Attraction> restaurantsList;
    private List<Attraction> allGoogleRestaurants;
    private List<Restaurant> allYelpRestaurants;
    private ArrayList<Attraction> pickedRestaurantsList;
    private Button btnAddRestaurant;

    int restaurantCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atr_details);

        restaurantCounter = 0;
        allGoogleRestaurants = new LinkedList<>();
        allYelpRestaurants = new LinkedList<>();
        btnAddRestaurant = findViewById(R.id.btnAddRestaurant);
        btnAddRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizePickedList();
                //TODO regenerate the route
            }
        });
        Log.i(TAG, "AtrDetailsActivity started!");
        attraction = (Attraction) Parcels.unwrap(getIntent().getExtras().getParcelable(Attraction.class.getSimpleName()));
        Log.i(TAG, "clicked attraction is " + attraction.name);
        pickedRestaurantsList = new ArrayList<>();
        rvRestaurants = (RecyclerView) findViewById(R.id.rvRestaurants);
        restaurantsList = new LinkedList<>();
        restaurantsAdapter = new PlacesAdapter(this, restaurantsList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvRestaurants.setLayoutManager(linearLayoutManager);
        rvRestaurants.setAdapter(restaurantsAdapter);
//        ivAtrPhoto = findViewById(R.id.ivAtrDetailsPhoto);
//        Log.i(TAG, "Details bitmap " +  attraction.photo);
//        ivAtrPhoto.setImageBitmap(attraction.photo);
        fetchRestaurants(Double.parseDouble(attraction.latitude), Double.parseDouble(attraction.longitude));
    }

    void fetchRestaurants(double latitude, double longitude) {
        StringBuilder stringBuilder = new StringBuilder(placesBaseUrl);
        stringBuilder.append("location=" + latitude + "%2C" + longitude);
        // radius for restaurants are much less because we want restaurants close to attraction
        stringBuilder.append("&radius=" + "2000");
        stringBuilder.append("&type=" + "restaurant");
        //ranks by prominence by default
        stringBuilder.append("&rankby");
        stringBuilder.append("&key=" + API_KEY);
        String url = stringBuilder.toString();
        Object dataTransfer[] = new Object[1];
        dataTransfer[0] = url;

        NearbyPlacesHelper getPlaces = new NearbyPlacesHelper(this);
        getPlaces.execute(dataTransfer);
    }

    private void finalizePickedList() {
        pickedRestaurantsList.clear();
        for (int i = 0; i < restaurantsList.size(); i++) {
            Attraction currAtr = restaurantsList.get(i);
            if (currAtr.picked) {
                pickedRestaurantsList.add(currAtr);
                Log.i(TAG, "putting picked atr " + currAtr.name);
            }
        }
        Log.i(TAG, "finalizedList Size " + pickedRestaurantsList.size());
    }

//    @Override
//    public void onTaskCompleted(Attraction restaurant) {
//        Log.i(TAG, "restaurant fetched " + restaurant.name);
//        try {
//            Attraction resWithPhoto = getPhotoBitmap(restaurant);
//            allGoogleRestaurants.add(restaurant);
//            Log.i(TAG, "allGoogleRestaurants size " + allGoogleRestaurants.size());
//            restaurantsAdapter.add(restaurant);
//        } catch (Exception e) {
//            Log.e(TAG, "Json exception", e);
//        }
//    }

    @Override
    public void onRestaurantTaskCompleted(Attraction attraction, Restaurant restaurant, int totalNumOfRestaurants) {
        allYelpRestaurants.add(restaurant);
        allGoogleRestaurants.add(attraction);
        Log.i(TAG, "yelp size " + allYelpRestaurants.size() + " total num " + totalNumOfRestaurants);
        if (allYelpRestaurants.size() == totalNumOfRestaurants) {
            Log.i(TAG, "about to rank");
            BusinessRankHelper restaurantRanker = new BusinessRankHelper(allGoogleRestaurants, allYelpRestaurants);
            List<Restaurant> rankedRestaurants = restaurantRanker.getRankedBusinesses();
            Log.i(TAG, "ranked restaurant size " + rankedRestaurants.size());
            for (Restaurant r : rankedRestaurants) {
                Log.i(TAG, "ranked " + r.googleYelpRating);
            }
            // TODO add the restaurants to the adapter
        }
    }

    @Override
    public void addNullToYelpList() {
        // Yelp does not have corresponding restaurant to Google Maps restaurant at this point
        allYelpRestaurants.add(null);
        allGoogleRestaurants.add(null);
    }

    @Override
    public void onTaskCompleted(Attraction attr) {
        return;
    }

    @Override
    public void onDurationTaskCompleted(int[][] durationMatrix) {
        return;
    }

    public Attraction getPhotoBitmap(Attraction atr) {
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
        return atr;
    }
}