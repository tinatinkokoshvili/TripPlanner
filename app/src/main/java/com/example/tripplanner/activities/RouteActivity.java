package com.example.tripplanner.activities;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tripplanner.OnTaskCompleted;
import com.example.tripplanner.R;
import com.example.tripplanner.RouteGenerator;
import com.example.tripplanner.apiclient.DistanceMatrixHelper;
import com.example.tripplanner.models.Attraction;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Route;

public class RouteActivity extends AppCompatActivity implements OnTaskCompleted {
    private static final String TAG = "RouteActivity";
    private List<Attraction> pickedAtrList;
    private static final String distanceMatrixBaseUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";
    private static final String API_KEY = "AIzaSyCe2kjKuINrKzh9bvmGa-ToZiEvluGRzwU";
    private List<Attraction> atrRoute;
    private int[][] durationMatrix;
    private double userLatitude;
    private double userLongitude;

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
    }

    private void fetchDistances() {
        StringBuilder stringBuilder = new StringBuilder(distanceMatrixBaseUrl);
        stringBuilder.append("&origins=");
        for (int i = 0; i < pickedAtrList.size(); i++) {
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
}