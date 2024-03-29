package com.example.tripplanner.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.tripplanner.algorithms.BusinessRankHelper;
import com.example.tripplanner.interfaces.OnTaskCompleted;
import com.example.tripplanner.R;
import com.example.tripplanner.adapters.RestaurantAdapter;
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
import com.google.android.material.progressindicator.CircularProgressIndicator;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.parceler.Parcels;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class AtrDetailsActivity extends AppCompatActivity implements OnTaskCompleted {
    private static final String TAG = "AtrDetailsActivity";
    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private String placesBaseUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final String noPhotoAvailableUrl = "https://archive.org/download/no-photo-available/no-photo-available.png";
    private static final String API_KEY = "AIzaSyCe2kjKuINrKzh9bvmGa-ToZiEvluGRzwU";
    private Attraction attraction;

    private ArrayList<Attraction> alreadyPickedAtrlist;

    private RecyclerView rvRestaurants;
    private RestaurantAdapter restaurantsAdapter;
    private List<Restaurant> restaurantsList;
    private List<Attraction> resAtrList;
    private List<Attraction> allGoogleRestaurants;
    private List<Restaurant> allYelpRestaurants;
    private ArrayList<Restaurant> pickedRestaurantsList;
    private Button btnAddRestaurant;
    private HashMap<Restaurant, Attraction> resToAtrMap;

    private String tripName;
    private String radius;
    private String totalTime;
    private String avgStayTime;

    ImageView ivDetailsPicture;
    ImageView ivDetailsIcon;
    TextView tvDetailsName;
    RatingBar rbDetailsRating;
    TextView tvDetailsRatingNum;
    TextView tvDetailsAddress;
    TextView tvDetailsPhoneNumber;
    TextView tvDetailsUrl;
    TextView tvDetailsWebsite;

    private CircularProgressIndicator cRestaurantsProgressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atr_details);

        resToAtrMap = new HashMap<>();
        Bundle bundle = getIntent().getExtras();
        alreadyPickedAtrlist = bundle.getParcelableArrayList("data");
        tripName = getIntent().getStringExtra("tripName");
        radius = getIntent().getStringExtra("radius");
        totalTime = getIntent().getStringExtra("totalTime");
        avgStayTime = getIntent().getStringExtra("avgStayTime");
        allGoogleRestaurants = new LinkedList<>();
        allYelpRestaurants = new LinkedList<>();
        btnAddRestaurant = findViewById(R.id.btnAddRestaurant);
        btnAddRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizePickedList();
                //regenerate the route
                goToRouteActivity();
            }
        });
        Log.i(TAG, "AtrDetailsActivity started!");
        attraction = (Attraction) Parcels.unwrap(getIntent().getExtras().getParcelable(Attraction.class.getSimpleName()));
        Log.i(TAG, "clicked attraction is " + attraction.name);
        pickedRestaurantsList = new ArrayList<>();
        rvRestaurants = (RecyclerView) findViewById(R.id.rvRestaurants);
        restaurantsList = new LinkedList<>();
        resAtrList = new LinkedList<>();
        restaurantsAdapter = new RestaurantAdapter(this, restaurantsList, resAtrList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvRestaurants.setLayoutManager(linearLayoutManager);
        rvRestaurants.setAdapter(restaurantsAdapter);

        ivDetailsPicture = findViewById(R.id.ivDetailsPicture);
        ivDetailsIcon = findViewById(R.id.ivDetailsIcon);
        tvDetailsName = findViewById(R.id.tvDetailsName);

        rbDetailsRating = findViewById(R.id.rbDetailsRating);
        tvDetailsRatingNum = findViewById(R.id.tvDetailsRatingNum);
        tvDetailsAddress = findViewById(R.id.tvDetailsAddress);

        tvDetailsPhoneNumber = findViewById(R.id.tvDetailsPhoneNumber);
        tvDetailsUrl = findViewById(R.id.tvDetailsUrl);
        tvDetailsWebsite = findViewById(R.id.tvDetailsWebsite);

        getPhotoBitmap(null, attraction);
        Log.i(TAG, "det photo " + attraction.photo);

        if (attraction.getIcon() != null && !attraction.getIcon().isEmpty()) {
            Glide.with(this).load(attraction.getIcon()).into(ivDetailsIcon);
        }
        if (attraction.getName() != null && !attraction.getName().isEmpty()) {
            tvDetailsName.setText(attraction.getName());
        }
        float voteAverage = (float) attraction.getRating();
        Log.i(TAG, attraction.getName() + voteAverage);
        rbDetailsRating.setRating(voteAverage);
        tvDetailsRatingNum.setText(decimalFormat.format(attraction.getRating()));

        if (attraction.getFormattedAddress() != null && !attraction.getFormattedAddress().isEmpty())
            tvDetailsAddress.setText(attraction.getFormattedAddress());
        if (attraction.getFormattedPhoneNumber() != null && !attraction.getFormattedPhoneNumber().isEmpty())
            tvDetailsPhoneNumber.setText(attraction.getFormattedPhoneNumber());
        if (attraction.getUrl() != null && !attraction.getUrl().isEmpty())
            tvDetailsUrl.setText(attraction.getUrl());
        if (attraction.getWebsite() != null && !attraction.getWebsite().isEmpty())
            tvDetailsWebsite.setText(attraction.getWebsite());

        cRestaurantsProgressIndicator = findViewById(R.id.cRestaurantsProgressIndicator);
        cRestaurantsProgressIndicator.show();
        fetchRestaurants(Double.parseDouble(attraction.getLat()), Double.parseDouble(attraction.getLon()));
    }

    private void goToRouteActivity() {
        for (int i = 0; i < pickedRestaurantsList.size(); i++) {
            alreadyPickedAtrlist.add(0, resToAtrMap.get(pickedRestaurantsList.get(i)));
        }
        Intent routeRegenerateActivity = new Intent(AtrDetailsActivity.this, RouteActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("data", alreadyPickedAtrlist);
        routeRegenerateActivity.putExtras(bundle);
        routeRegenerateActivity.putExtra("tripName", tripName);
        routeRegenerateActivity.putExtra("radius", radius);
        routeRegenerateActivity.putExtra("totalTime", totalTime);
        routeRegenerateActivity.putExtra("avgStayTime", avgStayTime);
        startActivity(routeRegenerateActivity);
    }

    private void fetchRestaurants(double latitude, double longitude) {
        rvRestaurants.setVisibility(View.GONE);
        cRestaurantsProgressIndicator.show();
        StringBuilder stringBuilder = new StringBuilder(placesBaseUrl);
        stringBuilder.append("location=" + latitude + "%2C" + longitude);
        // radius for restaurants are much less because we want restaurants close to attraction
        stringBuilder.append("&radius=" + "500");
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
            Restaurant currRes = restaurantsList.get(i);
            if (currRes.picked) {
                pickedRestaurantsList.add(currRes);
                Log.i(TAG, "putting picked restaurant " + currRes.name);
            }
        }
        Log.i(TAG, "finalizedRestaurantList Size " + pickedRestaurantsList.size());
    }

    @Override
    public void onRestaurantTaskCompleted(Attraction attraction, Restaurant restaurant, int totalNumOfRestaurants) {
        allYelpRestaurants.add(restaurant);
        allGoogleRestaurants.add(attraction);
        if (allYelpRestaurants.size() == totalNumOfRestaurants) {
            rvRestaurants.setVisibility(View.VISIBLE);
            cRestaurantsProgressIndicator.hide();
            BusinessRankHelper restaurantRanker = new BusinessRankHelper(allGoogleRestaurants, allYelpRestaurants);
            List<Restaurant> rankedRestaurants = restaurantRanker.getRankedBusinesses();
            for (int i = 0; i < rankedRestaurants.size(); i++) {
                for (int j = 0; j < allGoogleRestaurants.size(); j++) {
                    if (allGoogleRestaurants.get(j) != null
                            && allGoogleRestaurants.get(j).name.equals(rankedRestaurants.get(i).name)) {
                        getPhotoBitmap(rankedRestaurants.get(i), allGoogleRestaurants.get(j));


                        //Build a hashmap for later reference when starting routeActivity intent
                        resToAtrMap.put(rankedRestaurants.get(i), allGoogleRestaurants.get(j));
                    }
                }
            }
        }
    }

    @Override
    public void addNullToYelpList() {
        /*
          Yelp does not have corresponding restaurant to Google Maps restaurant at this point
          so we add null
         */
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

    public Attraction getPhotoBitmap(Restaurant res, Attraction atr) {
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
                Log.i(TAG, "bitmap fetched " + bitmap);
                atr.photo = bitmap;
                if (atr.equals(attraction)) {
                    if (attraction.getPhoto() != null) {
                        ivDetailsPicture.setImageBitmap(bitmap);
                    }
                } else {
                    restaurantsAdapter.add(res, atr);
                }
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