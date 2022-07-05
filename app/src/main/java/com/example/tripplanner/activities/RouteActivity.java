package com.example.tripplanner.activities;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tripplanner.OnTaskCompleted;
import com.example.tripplanner.R;
import com.example.tripplanner.RouteGenerator;
import com.example.tripplanner.apiclient.DistanceMatrixHelper;
import com.example.tripplanner.apiclient.NearbyPlacesHelper;
import com.example.tripplanner.models.Attraction;
import com.example.tripplanner.models.Graph;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class RouteActivity extends AppCompatActivity implements OnTaskCompleted {
    private static final String TAG = "RouteActivity";
    private List<Attraction> pickedAtrList;
    private static final String distanceMatrixBaseUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";
    private static final String API_KEY = "AIzaSyCe2kjKuINrKzh9bvmGa-ToZiEvluGRzwU";
    private List<Attraction> atrRoute;
    private int matrixCellCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        matrixCellCounter = 0;
        Bundle bundle = getIntent().getExtras();
        pickedAtrList = bundle.getParcelableArrayList("data");

//        Intent intent = getIntent();
//        pickedAtrList = (List<Attraction>) intent.getSerializableExtra("list");
        Log.i(TAG, "first atr " + pickedAtrList.get(0).name);
        Log.i(TAG, "second atr " + pickedAtrList.get(1).name);
        Log.i(TAG, "picked attraction" + pickedAtrList.size());
        fetchDistances();
//        RouteGenerator rtGenerator = new RouteGenerator(pickedAtrList);
//        atrRoute = rtGenerator.getRouteList();

    }

    private void fetchDistances() {
        for (int i = 0; i < pickedAtrList.size(); i++) {
            for (int j = 0; j < pickedAtrList.size(); j++) {
                StringBuilder stringBuilder = new StringBuilder(distanceMatrixBaseUrl);
                stringBuilder.append("&origins=");
                Attraction origin = pickedAtrList.get(i);
                Attraction destination = pickedAtrList.get(j);
                Log.i(TAG, "origin " + origin.name + " destination " + destination.name);
                stringBuilder.append(origin.latitude);
                stringBuilder.append("%2C");
                stringBuilder.append(origin.longitude);
                stringBuilder.append("&destinations=");
                stringBuilder.append(destination.latitude);
                stringBuilder.append("%2C");
                stringBuilder.append(destination.longitude);
                stringBuilder.append("&key=" + API_KEY);
                String url = stringBuilder.toString();
                Object dataTransfer[] = new Object[1];
                dataTransfer[0] = url;
                new DistanceMatrixHelper(this).execute(dataTransfer);
            }
        }
    }


    @Override
    public void onTaskCompleted(Attraction attr) {
        return;
    }

    @Override
    public void onDistanceTaskCompleted(int distanceCell) {
        matrixCellCounter++;

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