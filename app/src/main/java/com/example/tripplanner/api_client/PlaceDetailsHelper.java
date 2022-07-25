package com.example.tripplanner.api_client;

import android.os.AsyncTask;
import android.util.Log;

import com.example.tripplanner.interfaces.OnTaskCompleted;
import com.example.tripplanner.models.Attraction;
import com.example.tripplanner.yelp_helpers.RestaurantMatchHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PlaceDetailsHelper extends AsyncTask<Object, String, String> {
    private static final String TAG = "GetPlaceDetails";
    private OnTaskCompleted listener;
    private String url;
    private InputStream is;
    private BufferedReader bufferedReader;
    private StringBuilder stringBuilder;
    private String data;
    private int totalNumOfAtr;

    private static final String yelpMatchBase = "https://api.yelp.com/v3/businesses/matches?";
    
    public PlaceDetailsHelper(OnTaskCompleted listener, int totalNumOfAtr){
        this.listener=listener;
        this.totalNumOfAtr = totalNumOfAtr;
    }

    @Override
    protected String doInBackground(Object... objects) {
        url = (String) objects[0];
        try {
            URL myurl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) myurl.openConnection();
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

    //Parse the JSON data
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        //Create an attraction object
        try {
            JSONObject parentObject = new JSONObject(s);
            JSONObject resultObject = parentObject.getJSONObject("result");
            Attraction attraction = Attraction.createFromJson(resultObject);
            Log.i(TAG, "onTaskCompleted from GetDetails " + s);
            if (attraction.isRestaurant) {
                // Make a call to Yelp API
                StringBuilder restaurantStringBuilder = new StringBuilder(yelpMatchBase);
                restaurantStringBuilder.append("name=" + attraction.name);
                restaurantStringBuilder.append("&address1=" + attraction.address1);
                restaurantStringBuilder.append("&city=" + attraction.city);
                restaurantStringBuilder.append("&state=" + attraction.short_state);
                restaurantStringBuilder.append("&country=" + attraction.country);
                String matchUrl = restaurantStringBuilder.toString();
                Object restaurantDetailsDataTransfer[] = new Object[1];
                restaurantDetailsDataTransfer[0] = matchUrl;
                new RestaurantMatchHelper(attraction, listener, totalNumOfAtr).execute(restaurantDetailsDataTransfer);
            } else {
                listener.onTaskCompleted(attraction);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
