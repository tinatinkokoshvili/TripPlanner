package com.example.tripplanner.apiclient;

import android.os.AsyncTask;
import android.util.Log;

import com.example.tripplanner.OnTaskCompleted;
import com.example.tripplanner.activities.PickAttractionsActivity;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetNearbyPlaces extends AsyncTask<Object, String, String> {
    private static final String TAG = "GetNearbyPlaces";
    private OnTaskCompleted listener;
    private String url;
    private InputStream is;
    private BufferedReader bufferedReader;
    private StringBuilder stringBuilder;
    private String data;
    private static final String nextPlacesBaseUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final String placeDetailsBaseUrl = "https://maps.googleapis.com/maps/api/place/details/json?";
    private static final String API_KEY = "AIzaSyCe2kjKuINrKzh9bvmGa-ToZiEvluGRzwU";

    public GetNearbyPlaces(OnTaskCompleted listener){
        this.listener=listener;
    }


    @Override
    protected String doInBackground(Object... objects) {
        //mMap = (GoogleMap) objects[0];
        url = (String) objects[0];
        try {
            URL myurl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) myurl.openConnection();
            httpURLConnection.connect();
            is = httpURLConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(is));
            Log.i(TAG, "doing in b ackgorund");
            String line="";
            stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            data = stringBuilder.toString();
            bufferedReader.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return data;
    }

    //Parse the JSON data
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.i(TAG, "received" + s);
        try {
            JSONObject parentObject = new JSONObject(s);
            JSONArray resultsArray = parentObject.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject jsonObject = resultsArray.getJSONObject(i);
                JSONObject locationObj = jsonObject.getJSONObject("geometry").getJSONObject("location");
                String latitude = locationObj.getString("lat");
                String longitude = locationObj.getString("lng");
                String name = jsonObject.getString("name");
                String vicinity = jsonObject.getString("vicinity");
                String place_id = jsonObject.getString("place_id");
                Log.i(TAG, "latitude " + latitude + " longitude " + longitude
                    + " name " + name + " vicinity " + vicinity + " place_id " + place_id);

                //Make API calls for each place
                StringBuilder detailsStringBuilder = new StringBuilder(placeDetailsBaseUrl);
                detailsStringBuilder.append("place_id=" + place_id);
                detailsStringBuilder.append("&key=" + API_KEY);
                String detailsUrl = detailsStringBuilder.toString();
                Object detailsDataTransfer[] = new Object[1];
                detailsDataTransfer[0] = detailsUrl;
                new GetPlaceDetails(listener).execute(detailsDataTransfer);

            }
            if (parentObject.has("next_page_token")) {
                String next_page_token = parentObject.getString("next_page_token");
                if (next_page_token != null) {
                    Log.i(TAG, "next_page_token is not null");
                    StringBuilder stringBuilder = new StringBuilder(nextPlacesBaseUrl);
                    stringBuilder.append("pagetoken=" + next_page_token);
                    String url = stringBuilder.toString();
                    Object dataTransfer[] = new Object[1];
                    dataTransfer[0] = url;
                    new GetNearbyPlaces(listener).execute(dataTransfer);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
