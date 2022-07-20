package com.example.tripplanner.yelp_helpers;

import android.os.AsyncTask;
import android.util.Log;

import com.example.tripplanner.interfaces.OnTaskCompleted;
import com.example.tripplanner.models.Attraction;
import com.example.tripplanner.models.Restaurant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RestaurantDetailsHelper extends AsyncTask<Object, String, String> {
    private static final String TAG = "RestaurantDetailsHelper";
    private static final String YELP_API_KEY = "PMULr-WmLQYIkh0t9kjvz9c2JPIyTkGdEg6Z7j85MeaLY0th1UOzFg_v_w4T914K_cQHjP4gOIoo2inrSi9JlqSW-Rq9QGyPNXkR-YZyTfMjD4eUkJsO_mcjvo_MYnYx";
    private static final String businessReviewsBase = "https://api.yelp.com/v3/businesses/";
    private OnTaskCompleted listener;
    private String url;
    private InputStream is;
    private BufferedReader bufferedReader;
    private StringBuilder stringBuilder;
    private String data;
    private Attraction attraction;
    private int totalNumOfRestaurants;

    public RestaurantDetailsHelper(Attraction atr, OnTaskCompleted listener, int totalNumOfRestaurants){
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

        Log.i(TAG, "restaurant details fetched " + s);
        Log.i(TAG, "number of total restaurants " + totalNumOfRestaurants);
        try {
            JSONObject parentObject = new JSONObject(s);
            // Parse JSON to create a restaurant
            Restaurant restaurant = Restaurant.createFromJson(parentObject);
            listener.onRestaurantTaskCompleted(attraction, restaurant, totalNumOfRestaurants);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
