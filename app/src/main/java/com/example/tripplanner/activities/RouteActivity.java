package com.example.tripplanner.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tripplanner.OnTaskCompleted;
import com.example.tripplanner.R;
import com.example.tripplanner.RouteGenerator;
import com.example.tripplanner.api_client.DistanceMatrixHelper;
import com.example.tripplanner.directionhelpers.FetchURL;
import com.example.tripplanner.directionhelpers.TaskLoadedCallback;
import com.example.tripplanner.models.Attraction;

import com.example.tripplanner.models.DoubleClickListener;
import com.example.tripplanner.models.Restaurant;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.annotation.SuppressLint;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RouteActivity extends AppCompatActivity implements OnTaskCompleted, OnMapReadyCallback, TaskLoadedCallback, GoogleMap.OnMarkerClickListener {
    private static final String TAG = "RouteActivity";

    private FirebaseAuth fbAuth;
    private String userID;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference;

    // In pickedAtrList, userLocation is always at last index, other locations are in correct order to visit
    private ArrayList<Attraction> pickedAtrList;
    private static final String distanceMatrixBaseUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";
    private static final String directionsBaseUrl = "https://maps.googleapis.com/maps/api/directions/";
    private static final String googleMapsInGooglePlay = "https://play.google.com/store/apps/details?id=com.google.android.apps.maps";
    private static final String API_KEY = "AIzaSyCe2kjKuINrKzh9bvmGa-ToZiEvluGRzwU";
    private static final String TRAVEL_MODE = "driving";
    // In atrRoute, userLocation is at first index, other locations are in correct order to visit after index 0
    private List<Attraction> atrRoute;
    private int[][] durationMatrix;
    private HashMap<Attraction, Integer> pickedAtrToPickedListIndexMap;
    private double userLatitude;
    private double userLongitude;

    private View mapView;

    private final float DEFAULT_ZOOM = 12;

    private String tripName;
    private String radius;
    private String totalTime;
    private String avgStayTime;
    private double actualTotalTime;

    private HashMap<LatLng, Attraction> markerPosToAtrMap;
    private GoogleMap mMap;
    private Polyline currentPolyline;
    private Button btnOpenInMaps;
    private Button btnAddRestaurants;
    private Button btnSaveTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        firestore = FirebaseFirestore.getInstance();
        fbAuth = FirebaseAuth.getInstance();
        userID = fbAuth.getCurrentUser().getUid();
        //TODO get trip name provided from user and replace here
//        documentReference =
//                firestore.collection("testUsers")
//                        .document(userID).collection("trips");
        markerPosToAtrMap = new HashMap<>();
//        userLatitude = getIntent().getDoubleExtra("latitude", 0);
//        userLongitude = getIntent().getDoubleExtra("longitude", 0);
        Bundle bundle = getIntent().getExtras();
        pickedAtrList = bundle.getParcelableArrayList("data");
        createPickedAtrToPickedListIndexMap();
        userLatitude = Double.parseDouble(pickedAtrList.get(pickedAtrList.size() - 1).latitude);
        userLongitude = Double.parseDouble(pickedAtrList.get(pickedAtrList.size() - 1).longitude);
        tripName = getIntent().getStringExtra("tripName");
        radius = getIntent().getStringExtra("radius");
        totalTime = getIntent().getStringExtra("totalTime");
        avgStayTime = getIntent().getStringExtra("avgStayTime");

        durationMatrix = new int[pickedAtrList.size()][pickedAtrList.size()];
        fetchDistances();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Log.i(TAG, "map " + mapFragment);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

       // mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(RouteActivity.this);
        btnOpenInMaps = findViewById(R.id.btnOpenInMaps);
        btnOpenInMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    goToMaps();
            }
        });
        btnAddRestaurants = findViewById(R.id.btnAddRestaurants);
        btnAddRestaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRestaurantSelection();
            }
        });
        btnSaveTrip = findViewById(R.id.btnSaveTrip);
        btnSaveTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveTrip();
            }
        });
    }

    private void createPickedAtrToPickedListIndexMap() {
        pickedAtrToPickedListIndexMap = new HashMap<>();
        for (int i = 0; i < pickedAtrList.size(); i++) {
            pickedAtrToPickedListIndexMap.put(pickedAtrList.get(i), i);
        }
    }

    private void SaveTrip() {
        Map<String, Object> tripMap = new HashMap<>();
        tripMap.put("tripName", tripName);
        tripMap.put("userLatitude", Double.toString(userLatitude));
        tripMap.put("userLongitude", Double.toString(userLongitude));
        tripMap.put("radius", radius);
        tripMap.put("totalTime", totalTime);
        tripMap.put("avgStayTime", avgStayTime);
        tripMap.put("actualTotalTime", Double.toString(actualTotalTime));
        for (int i = 1; i < atrRoute.size(); i++) {
            tripMap.put(Integer.toString(i), atrRoute.get(i));
        }
        firestore.collection("testUsers")
                .document(userID).collection("trips").document(tripName)
                .set(tripMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i(TAG, "trip data successfully saved");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "error when saving trip data");
                    }
                });
    }

    private void goToRestaurantSelection() {
        Intent restaurantsIntent = new Intent(RouteActivity.this,
                RestaurantsSelectionActivity.class);
//                restaurantsIntent.putExtra("latitude", userLatitude);
//                restaurantsIntent.putExtra("longitude", userLongitude);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("data", pickedAtrList);
        restaurantsIntent.putExtras(bundle);
        restaurantsIntent.putExtra("tripName", tripName);
        restaurantsIntent.putExtra("radius", radius);
        restaurantsIntent.putExtra("totalTime", totalTime);
        restaurantsIntent.putExtra("avgStayTime", avgStayTime);
        startActivity(restaurantsIntent);
    }

    private void goToMaps() {
        try {
            // If Google Maps are not installed then direct user to play store
            String redirectUrl = "";
            redirectUrl += "https://www.google.co.in/maps/dir/";
            redirectUrl += pickedAtrList.get(pickedAtrList.size()-1).latitude +",";
            redirectUrl += pickedAtrList.get(pickedAtrList.size()-1).longitude + "/";
            for (int i = 0; i < pickedAtrList.size() - 1; i++) {
                Log.i(TAG, "redirecting " + pickedAtrList.get(i).name + " to " + pickedAtrList.get(i+1));
                redirectUrl += pickedAtrList.get(i).latitude +",";
                redirectUrl += pickedAtrList.get(i).longitude + "/";
            }
            Uri googleMapsUri = Uri.parse(redirectUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW, googleMapsUri);
            intent.setPackage("com.google.android.apps.maps");
            // Set flag
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Uri playStoreUri = Uri.parse(googleMapsInGooglePlay);
            Intent intent = new Intent(Intent.ACTION_VIEW, playStoreUri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void fetchDistances() {
        StringBuilder stringBuilder = new StringBuilder(distanceMatrixBaseUrl);
        stringBuilder.append("&origins=");
        for (int i = 0; i < pickedAtrList.size(); i++) {
            Log.i(TAG, "picked attraction putting in link " + pickedAtrList.get(i).name);
            Attraction curOrigin = pickedAtrList.get(i);
            stringBuilder.append(curOrigin.latitude);
            stringBuilder.append("%2C");
            stringBuilder.append(curOrigin.longitude);
            stringBuilder.append("%7C");
        }
        // Add users current location to list of origin
        stringBuilder.append(Double.toString(userLatitude));
        stringBuilder.append("%2C");
        stringBuilder.append(Double.toString(userLongitude));
        stringBuilder.append("&destinations=");
        for (int i = 0; i < pickedAtrList.size(); i++) {
            Attraction curDestination = pickedAtrList.get(i);
            stringBuilder.append(curDestination.latitude);
            stringBuilder.append("%2C");
            stringBuilder.append(curDestination.longitude);
            stringBuilder.append("%7C");
        }
        // Add users current location to list of destination
        stringBuilder.append(Double.toString(userLatitude));
        stringBuilder.append("%2C");
        stringBuilder.append(Double.toString(userLongitude));

        stringBuilder.append("&departure_time=now");
        stringBuilder.append("&key=" + getString(R.string.google_maps_key));
        String url = stringBuilder.toString();
        Object dataTransfer[] = new Object[1];
        dataTransfer[0] = url;
        new DistanceMatrixHelper(this).execute(dataTransfer);
    }

    @Override
    public void onTaskCompleted(Attraction attr) {
        return;
    }

    @Override
    public void onDurationTaskCompleted(int[][] durationMatrix) {
        this.durationMatrix = durationMatrix;
//        // Add current location to the list of picked attractions
//        Attraction userLocation = new Attraction();
//        userLocation.name = "User Location";
//        userLocation.latitude = Double.toString(userLatitude);
//        userLocation.longitude = Double.toString(userLongitude);
//        pickedAtrList.add(userLocation);
        RouteGenerator rtGenerator = new RouteGenerator(pickedAtrList, durationMatrix);
        atrRoute = rtGenerator.getRouteList();
        // Add markers to the map
        addMarkers();
        // Add routes to the map
        Log.i(TAG, "atrRoute size " + atrRoute.size());

        for (int i = 0; i < atrRoute.size() - 1; i++) {
            Log.i(TAG, "atrRoute size " + atrRoute.get(i).name);
            MarkerOptions origin =
                    new MarkerOptions().position(new LatLng(Double.parseDouble(atrRoute.get(i).latitude),
                    Double.parseDouble(atrRoute.get(i).longitude))).title(atrRoute.get(i).name);
            MarkerOptions destination =
                    new MarkerOptions().position(new LatLng(Double.parseDouble(atrRoute.get(i + 1).latitude),
                    Double.parseDouble(atrRoute.get(i + 1).longitude))).title(atrRoute.get(i + 1).name);
            // Draw the polyline on the map between origin and destination
            Toast.makeText(RouteActivity.this,
                    "Drawing line for origin" + origin + " detsination " + destination,
                    Toast.LENGTH_SHORT).show();
            new FetchURL(RouteActivity.this)
                    .execute(getUrl(origin.getPosition(), destination.getPosition(), TRAVEL_MODE), TRAVEL_MODE);
        }
        if (totalTripTimeExceedsUserProvidedTime()) {
            Toast.makeText(RouteActivity.this,
                    "Warning: Total Trip Time (" + actualTotalTime + "hr) exceeds " + totalTime + "hr",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(RouteActivity.this,
                    "Total Trip Time (" + actualTotalTime + "hr) does NOT exceed " + totalTime + "hr",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean totalTripTimeExceedsUserProvidedTime() {
        actualTotalTime = 0;
        for (int i = 0; i < atrRoute.size() - 1; i++) {
            Attraction origin = atrRoute.get(i);
            Attraction destination = atrRoute.get(i + 1);
            if (!pickedAtrToPickedListIndexMap.containsKey(origin) || !pickedAtrToPickedListIndexMap.containsKey(destination)) {
                Toast.makeText(RouteActivity.this,
                        "Could not calculate total time, invalid attracions", Toast.LENGTH_SHORT).show();
            } else {
                double rideDuration = ((double) durationMatrix[pickedAtrToPickedListIndexMap.get(origin)][pickedAtrToPickedListIndexMap.get(destination)]) / 3600;
                actualTotalTime += rideDuration;
                actualTotalTime += Double.parseDouble(avgStayTime);
            }
        }
        Log.i(TAG, "actualTotalTime " + actualTotalTime + " user TotalTime " + totalTime);
        return actualTotalTime > Double.parseDouble(totalTime);
    }

    @Override
    public void onRestaurantTaskCompleted(Attraction attraction, Restaurant restaurant, int numOfTotalRestaurants) {
        return;
    }

    @Override
    public void addNullToYelpList() {
        return;
    }

    public static byte[] convert(Bitmap bitmap) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] array = stream.toByteArray();
        stream.close();
        return array;
    }

    public static Bitmap convert(byte[] array) {
        return BitmapFactory.decodeByteArray(array,0,array.length);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        DoubleClickListener doubleClickListener = new DoubleClickListener() {
            @Override
            public void onDoubleClick() {
                Log.i(TAG, "marker DOUBLE clicked");
                Attraction clickedAtr = markerPosToAtrMap.get(getMarkerPosition());
                Log.i(TAG, "clicked atr is " + clickedAtr.name);
                Intent atrDetailsWithRestaurantsIntent = new Intent(RouteActivity.this, AtrDetailsActivity.class);
                atrDetailsWithRestaurantsIntent.putExtra(Attraction.class.getSimpleName(), Parcels.wrap(clickedAtr));
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("data", pickedAtrList);
                atrDetailsWithRestaurantsIntent.putExtras(bundle);
                atrDetailsWithRestaurantsIntent.putExtra("tripName", tripName);
                atrDetailsWithRestaurantsIntent.putExtra("radius", radius);
                atrDetailsWithRestaurantsIntent.putExtra("totalTime", totalTime);
                atrDetailsWithRestaurantsIntent.putExtra("avgStayTime", avgStayTime);
                startActivity(atrDetailsWithRestaurantsIntent);
            }
        };
        mMap.setOnMarkerClickListener(doubleClickListener);

        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton =
                    ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            //LocationButton no longer on the top of the screen, remove rule
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 40, 180);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLatitude, userLongitude), DEFAULT_ZOOM));
    }

    private void addMarkers() {
        for (int i = 0; i < atrRoute.size(); i++) {
            Attraction curAtr = atrRoute.get(i);
            Log.i(TAG, "lat string " + curAtr.latitude + " long string " + curAtr.longitude
                    + " double " + Double.parseDouble(curAtr.latitude) + " double long " + Double.parseDouble(curAtr.longitude));
            MarkerOptions place =
                new MarkerOptions().position(new LatLng(Double.parseDouble(curAtr.latitude),
                        Double.parseDouble(curAtr.longitude))).title(curAtr.name);
            Log.i(TAG, "marker latitude " +  place.getPosition().latitude);
            markerPosToAtrMap.put(place.getPosition(), curAtr);
            Log.i(TAG, "map size " + markerPosToAtrMap.size());
            mMap.addMarker(place);
        }
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=" + directionMode;
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = directionsBaseUrl + output + "?" + parameters + "&key=" + API_KEY;
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Log.i(TAG, "marker clicked");
        return false;
    }
}