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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

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
import java.util.List;


public class RouteActivity extends AppCompatActivity implements OnTaskCompleted, OnMapReadyCallback, TaskLoadedCallback, GoogleMap.OnMarkerClickListener {
    private static final String TAG = "RouteActivity";
    // In pickedAtrList, userLocation is at last index, other locations are in correct order to visit
    private List<Attraction> pickedAtrList;
    private static final String distanceMatrixBaseUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";
    private static final String directionsBaseUrl = "https://maps.googleapis.com/maps/api/directions/";
    private static final String googleMapsInGooglePlay = "https://play.google.com/store/apps/details?id=com.google.android.apps.maps";
    private static final String API_KEY = "AIzaSyCe2kjKuINrKzh9bvmGa-ToZiEvluGRzwU";
    private static final String TRAVEL_MODE = "driving";
    // In atrRoute, userLocation is at first index, other locations are in correct order to visit after index 0
    private List<Attraction> atrRoute;
    private int[][] durationMatrix;
    private double userLatitude;
    private double userLongitude;

    private View mapView;

    private final float DEFAULT_ZOOM = 12;

    private GoogleMap mMap;
    private Polyline currentPolyline;
    private Button btnOpenInMaps;
    private Button btnAddRestaurants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        userLatitude = getIntent().getDoubleExtra("latitude", 0);
        userLongitude = getIntent().getDoubleExtra("longitude", 0);
        Bundle bundle = getIntent().getExtras();
        pickedAtrList = bundle.getParcelableArrayList("data");
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
                String origin = "Mumbai";
                String destination = "Thane";
                if (origin == null || destination == null) {
                    Toast.makeText(getApplicationContext(), "Was not able to find locations.", Toast.LENGTH_SHORT).show();
                } else {
                    displayTrack(origin, destination);
                }
            }
        });
        btnAddRestaurants = findViewById(R.id.btnAddRestaurants);
        btnAddRestaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent restaurantsIntent = new Intent(RouteActivity.this, RestaurantsSelectionActivity.class);
                restaurantsIntent.putExtra("latitude", userLatitude);
                restaurantsIntent.putExtra("longitude", userLongitude);
                startActivity(restaurantsIntent);
            }
        });
    }

    private void displayTrack(String origin, String destination) {
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
        // Add current location to the list of picked attractions
        Attraction userLocation = new Attraction();
        userLocation.name = "User Location";
        userLocation.latitude = Double.toString(userLatitude);
        userLocation.longitude = Double.toString(userLongitude);
        pickedAtrList.add(userLocation);
        RouteGenerator rtGenerator = new RouteGenerator(pickedAtrList, durationMatrix);
        atrRoute = rtGenerator.getRouteList();
        // Add markers to the map
        addMarkers();
        // Add routes to the map
        Log.i(TAG, "atrRoute size " + atrRoute.size());

        for (int i = 0; i < atrRoute.size() - 1; i++) {
            Log.i(TAG, "atrRoute size " + atrRoute.get(i).name);
            MarkerOptions origin = new MarkerOptions().position(new LatLng(Double.parseDouble(atrRoute.get(i).latitude),
                    Double.parseDouble(atrRoute.get(i).longitude))).title(atrRoute.get(i).name);
            MarkerOptions destination = new MarkerOptions().position(new LatLng(Double.parseDouble(atrRoute.get(i + 1).latitude),
                    Double.parseDouble(atrRoute.get(i + 1).longitude))).title(atrRoute.get(i + 1).name);
            new FetchURL(RouteActivity.this)
                    .execute(getUrl(origin.getPosition(), destination.getPosition(), TRAVEL_MODE), TRAVEL_MODE);
        }
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
        mMap.setOnMarkerClickListener(this);

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
            Log.i(TAG, "lat string " + atrRoute.get(i).latitude + " long string " + atrRoute.get(i).longitude
                    + " double " + Double.parseDouble(atrRoute.get(i).latitude) + " double long " + Double.parseDouble(atrRoute.get(i).longitude));
            MarkerOptions place =
                new MarkerOptions().position(new LatLng(Double.parseDouble(atrRoute.get(i).latitude),
                        Double.parseDouble(atrRoute.get(i).longitude))).title(atrRoute.get(i).name);

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