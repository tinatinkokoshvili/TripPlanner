package com.example.tripplanner.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.tripplanner.interfaces.OnTaskCompleted;
import com.example.tripplanner.R;
import com.example.tripplanner.algorithms.RouteGenerator;
import com.example.tripplanner.api_client.DistanceMatrixHelper;
import com.example.tripplanner.directionhelpers.FetchURL;
import com.example.tripplanner.directionhelpers.TaskLoadedCallback;
import com.example.tripplanner.models.Attraction;

import com.example.tripplanner.models.DoubleClickListener;
import com.example.tripplanner.models.Restaurant;
import com.example.tripplanner.models.Trip;
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
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import android.annotation.SuppressLint;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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


public class RouteActivity extends AppCompatActivity implements OnTaskCompleted, OnMapReadyCallback, TaskLoadedCallback, GoogleMap.OnMarkerClickListener {
    private static final String TAG = "RouteActivity";

    private FirebaseAuth fbAuth;
    private String userID;
    private FirebaseFirestore firestore;

    // In pickedAtrList, userLocation is always at last index, other locations are in correct order to visit
    private ArrayList<Attraction> pickedAtrList;
    private static final String distanceMatrixBaseUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";
    private static final String directionsBaseUrl = "https://maps.googleapis.com/maps/api/directions/";
    private static final String googleMapsInGooglePlay = "https://play.google.com/store/apps/details?id=com.google.android.apps.maps";
    private static final String API_KEY = "AIzaSyCe2kjKuINrKzh9bvmGa-ToZiEvluGRzwU";
    private static final String TRAVEL_MODE = "driving";
    private static final String C2 = "%2C";
    private static final String C7 = "%7C";
    private static final String DEPARTURE_TIME = "now";
    private static final String TRIP_SAVED = "Trip Saved";
    private static final String TRIP_SAVE_FAILED = "Save Failed";
    private static final String UNDO = "UNDO";
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
    private CircularProgressIndicator cSaveProgressIndicator;
    ProgressDialog progressDialog;
    CoordinatorLayout snackbarCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        snackbarCoordinatorLayout = findViewById(R.id.snackbarCoordinatorLayout);
        progressDialog = new ProgressDialog(this);
        firestore = FirebaseFirestore.getInstance();
        fbAuth = FirebaseAuth.getInstance();
        userID = fbAuth.getCurrentUser().getUid();
        markerPosToAtrMap = new HashMap<>();
        Bundle bundle = getIntent().getExtras();
        pickedAtrList = bundle.getParcelableArrayList("data");
        createPickedAtrToPickedListIndexMap();
        Log.i(TAG, "userlocation lat " + pickedAtrList.get(pickedAtrList.size() - 1).getName());
        Log.i(TAG, "userlocation lon " + pickedAtrList.get(pickedAtrList.size() - 1).getLon());
        userLatitude = Double.parseDouble(pickedAtrList.get(pickedAtrList.size() - 1).getLat());
        userLongitude = Double.parseDouble(pickedAtrList.get(pickedAtrList.size() - 1).getLon());
        tripName = getIntent().getStringExtra("tripName");
        radius = getIntent().getStringExtra("radius");
        totalTime = getIntent().getStringExtra("totalTime");
        avgStayTime = getIntent().getStringExtra("avgStayTime");

        showProgressDialogWithTitle("Please Wait", "Getting required data...");
        durationMatrix = new int[pickedAtrList.size()][pickedAtrList.size()];
        fetchDistances();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showProgressDialogWithTitle("Please Wait", "Creating the most efficient route...");
            }
        }, 1000);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Log.i(TAG, "map " + mapFragment);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

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

        cSaveProgressIndicator = findViewById(R.id.cSaveProgressIndicator);
        cSaveProgressIndicator.hide();
    }

    private void createPickedAtrToPickedListIndexMap() {
        pickedAtrToPickedListIndexMap = new HashMap<>();
        for (int i = 0; i < pickedAtrList.size(); i++) {
            pickedAtrToPickedListIndexMap.put(pickedAtrList.get(i), i);
        }
    }

    private void SaveTrip() {
        cSaveProgressIndicator.show();
        Trip tripToSave = new Trip(tripName, Double.toString(userLatitude), Double.toString(userLatitude),
                radius, Double.parseDouble(totalTime), Double.parseDouble(avgStayTime),
                actualTotalTime, atrRoute);
        firestore.collection("testUsers")
                .document(userID).collection("trips").document(tripName)
                .set(tripToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i(TAG, "trip data successfully saved");
                        cSaveProgressIndicator.hide();
                        Snackbar.make(snackbarCoordinatorLayout, TRIP_SAVED,
                                        Snackbar.LENGTH_LONG)
                                .setAction(UNDO, new UndoListener(firestore, userID, tripName))
                                .show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "error when saving trip data");
                        cSaveProgressIndicator.hide();
                        Snackbar.make(snackbarCoordinatorLayout, TRIP_SAVE_FAILED,
                                        Snackbar.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    private void goToRestaurantSelection() {
        Intent restaurantsIntent = new Intent(RouteActivity.this,
                RestaurantsSelectionActivity.class);
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
            redirectUrl += pickedAtrList.get(pickedAtrList.size()-1).getLat() +",";
            redirectUrl += pickedAtrList.get(pickedAtrList.size()-1).getLon() + "/";
            for (int i = 0; i < pickedAtrList.size() - 1; i++) {
                Log.i(TAG, "redirecting " + pickedAtrList.get(i).getName() + " to " + pickedAtrList.get(i+1));
                redirectUrl += pickedAtrList.get(i).getLat() +",";
                redirectUrl += pickedAtrList.get(i).getLon() + "/";
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
            Log.i(TAG, "picked attraction putting in link " + pickedAtrList.get(i).getName());
            Attraction curOrigin = pickedAtrList.get(i);
            stringBuilder.append(curOrigin.getLat());
            stringBuilder.append(C2);
            stringBuilder.append(curOrigin.getLon());
            stringBuilder.append(C7);
        }
        // Add users current location to list of origin
        stringBuilder.append(Double.toString(userLatitude));
        stringBuilder.append(C2);
        stringBuilder.append(Double.toString(userLongitude));
        stringBuilder.append("&destinations=");
        for (int i = 0; i < pickedAtrList.size(); i++) {
            Attraction curDestination = pickedAtrList.get(i);
            stringBuilder.append(curDestination.getLat());
            stringBuilder.append(C2);
            stringBuilder.append(curDestination.getLon());
            stringBuilder.append(C7);
        }
        // Add users current location to list of destination
        stringBuilder.append(Double.toString(userLatitude));
        stringBuilder.append(C2);
        stringBuilder.append(Double.toString(userLongitude));

        stringBuilder.append("&departure_time=" + DEPARTURE_TIME);
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
            Log.i(TAG, "atrRoute size " + atrRoute.get(i).getName());
            MarkerOptions origin =
                    new MarkerOptions().position(new LatLng(Double.parseDouble(atrRoute.get(i).getLat()),
                    Double.parseDouble(atrRoute.get(i).getLon()))).title(atrRoute.get(i).getName());
            MarkerOptions destination =
                    new MarkerOptions().position(new LatLng(Double.parseDouble(atrRoute.get(i + 1).getLat()),
                    Double.parseDouble(atrRoute.get(i + 1).getLon()))).title(atrRoute.get(i + 1).getName());
            // Draw the polyline on the map between origin and destination
//            Toast.makeText(RouteActivity.this,
//                    "Drawing line for origin" + origin + " detsination " + destination,
//                    Toast.LENGTH_SHORT).show();
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgressDialogWithTitle();
            }
        }, 1000);
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
                actualTotalTime += Double.parseDouble(avgStayTime.toString());
            }
        }
        Log.i(TAG, "actualTotalTime " + actualTotalTime + " user TotalTime " + totalTime);
        return actualTotalTime > Double.parseDouble(totalTime.toString());
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
                Log.i(TAG, "clicked atr is " + clickedAtr.getName());
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
            Log.i(TAG, "lat string " + curAtr.getLat() + " long string " + curAtr.getLon()
                    + " double " + Double.parseDouble(curAtr.getLat()) + " double long " + Double.parseDouble(curAtr.getLon()));
            MarkerOptions place =
                new MarkerOptions().position(new LatLng(Double.parseDouble(curAtr.getLat()),
                        Double.parseDouble(curAtr.getLon()))).title(i + ". " + curAtr.getName());
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

    // Method to show Progress bar
    private void showProgressDialogWithTitle(String title,String substring) {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //Without this user can hide loader by tapping outside screen
        progressDialog.setCancelable(false);
        progressDialog.setTitle(title);
        progressDialog.setMessage(substring);
        progressDialog.show();

    }

    // Method to hide/ dismiss Progress bar
    private void hideProgressDialogWithTitle() {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.dismiss();
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

class UndoListener implements View.OnClickListener {
    private static final String TAG = "MyUndoListener";
    private String COLLECTIONS_USERS = "testUsers";
    private String TRIPS = "trips";
    private String userID;
    private FirebaseFirestore firestore;
    private String tripName;

    UndoListener(FirebaseFirestore firestore, String userID, String tripName) {
        this.firestore = firestore;
        this.userID = userID;
        this.tripName = tripName;
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "undo clicked");
        firestore.collection(COLLECTIONS_USERS)
                .document(userID).collection(TRIPS).document(tripName)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }
}