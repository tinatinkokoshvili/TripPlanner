package com.example.tripplanner.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.example.tripplanner.OnTaskCompleted;
import com.example.tripplanner.R;
import com.example.tripplanner.RouteGenerator;
import com.example.tripplanner.apiclient.DistanceMatrixHelper;
import com.example.tripplanner.models.Attraction;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.net.PlacesClient;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Route;

public class RouteActivity extends AppCompatActivity implements OnTaskCompleted, OnMapReadyCallback {
    private static final String TAG = "RouteActivity";
    private List<Attraction> pickedAtrList;
    private static final String distanceMatrixBaseUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";
    private static final String API_KEY = "AIzaSyCe2kjKuINrKzh9bvmGa-ToZiEvluGRzwU";
    private List<Attraction> atrRoute;
    private int[][] durationMatrix;
    private double userLatitude;
    private double userLongitude;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;

    private Location mLastKnownLocation;
    private LocationCallback locationCallback;
    private View mapView;

    private LatLng latLngOfPlace;

    private final float DEFAULT_ZOOM = 18;

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
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(RouteActivity.this);
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
        stringBuilder.append("&key=" + API_KEY);
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
        userLocation.latitude = Double.toString(userLatitude);
        userLocation.longitude = Double.toString(userLongitude);
        pickedAtrList.add(userLocation);
        RouteGenerator rtGenerator = new RouteGenerator(pickedAtrList, durationMatrix);
        List<Attraction> atrRoute = rtGenerator.getRouteList();
        // Display the route
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

        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton =
                    ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            //LocationButton no longer on the top of the screen, remove rule
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 40, 180);
        }

        //check if gps is enabled or not and then request user to enable it
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(RouteActivity.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(RouteActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        task.addOnFailureListener(RouteActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        //let user enable location
                        resolvable.startResolutionForResult(RouteActivity.this, 51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 51) {
            //check if user enabled location
            if (resultCode == RESULT_OK) {
                getDeviceLocation();
            }
        }
    }

    //Supressing the permission error for now, user needs to give permission to the app to use location
    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        //Fetch current location of decide
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                //Move map to the current location
                                LatLng mLastKnownLocationCoord = new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude());
                                //Set the variable so that we can pass it into pickActivity intent if searchbar not used
                                latLngOfPlace = mLastKnownLocationCoord;
                                Log.i(TAG, "latlong in getDeviceLocatoin " + latLngOfPlace.latitude + " " + latLngOfPlace.longitude);
                                mMap.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(mLastKnownLocationCoord, DEFAULT_ZOOM));
                            } else {
                                //Request for updated location
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        mLastKnownLocation = locationResult.getLastLocation();
                                        mMap.moveCamera(CameraUpdateFactory
                                                .newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(),
                                                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };
                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

                            }
                        } else {
                            Toast.makeText(RouteActivity.this, "Unable to get last location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}