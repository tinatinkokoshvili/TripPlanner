package com.example.tripplanner.yelp_helpers;

import android.os.AsyncTask;
import android.util.Log;

import com.example.tripplanner.OnTaskCompleted;
import com.example.tripplanner.models.Attraction;

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

public class RestaurantMatchHelper extends AsyncTask<Object, String, String> {
    private static final String TAG = "RestaurantMatchHelper";
    private static final String businessDetailsBase = "https://api.yelp.com/v3/businesses/";
    private static final String YELP_API_KEY = "PMULr-WmLQYIkh0t9kjvz9c2JPIyTkGdEg6Z7j85MeaLY0th1UOzFg_v_w4T914K_cQHjP4gOIoo2inrSi9JlqSW-Rq9QGyPNXkR-YZyTfMjD4eUkJsO_mcjvo_MYnYx";
    private OnTaskCompleted listener;
    private String url;
    private InputStream is;
    private BufferedReader bufferedReader;
    private StringBuilder stringBuilder;
    private String data;
    private Attraction attraction;
    private int totalNumOfRestaurants;

    public RestaurantMatchHelper(Attraction atr, OnTaskCompleted listener, int totalNumOfRestaurants){
        this.attraction = atr;
        this.listener = listener;
        this.totalNumOfRestaurants = totalNumOfRestaurants;
    }

    @Override
    protected String doInBackground(Object... objects) {
        url = (String) objects[0];
        try {
            URL myurl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) myurl.openConnection();
            httpURLConnection.setRequestProperty("Authorization", "Bearer " + YELP_API_KEY);
            httpURLConnection.connect();
            is = httpURLConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(is));
            Log.i(TAG, "doing in background");
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

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Log.i(TAG, "restaurant data fetched " + s);
        // Get restaurant ID and pass google restaurant, yelp parsed restaurant, totalNumOfRestaurants to restaurantDetailsHelper
        try {
            JSONObject parentObject = new JSONObject(s);
            JSONArray businessesArray = parentObject.getJSONArray("businesses");
            if (businessesArray.length() == 0) {
                // yelp match null, call listener onTaskCompleted to add attraction and null to the lists in listener
                //TODO
            } else {
                String restaurantId = businessesArray.getJSONObject(0).getString("id");
                Log.i(TAG, "id " + restaurantId);
                // Call Yelp API to get details about the restaurant
                StringBuilder stringBuilder = new StringBuilder(businessDetailsBase);
                stringBuilder.append(restaurantId);
                String detailsUrl = stringBuilder.toString();
                Object restaurantDetailsDataTransfer[] = new Object[1];
                restaurantDetailsDataTransfer[0] = detailsUrl;
                new RestaurantDetailsHelper(attraction, listener, totalNumOfRestaurants).execute(restaurantDetailsDataTransfer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
