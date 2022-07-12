package com.example.tripplanner.api_client;

import android.os.AsyncTask;
import android.util.Log;

import com.example.tripplanner.OnTaskCompleted;
import com.example.tripplanner.models.Attraction;
import com.example.tripplanner.yelp_helpers.RestaurantDetailsHelper;

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
    private String placePhotoBaseUrl = "https://maps.googleapis.com/maps/api/place/photo?";
    private static final String API_KEY = "AIzaSyCe2kjKuINrKzh9bvmGa-ToZiEvluGRzwU";

    private static final String reqBaseBusiness = "https://api.yelp.com/v3/businesses/search?";
    private static final String yelpMatchBase = "https://api.yelp.com/v3/businesses/matches?";
    private static final String YELP_API_KEY = "PMULr-WmLQYIkh0t9kjvz9c2JPIyTkGdEg6Z7j85MeaLY0th1UOzFg_v_w4T914K_cQHjP4gOIoo2inrSi9JlqSW-Rq9QGyPNXkR-YZyTfMjD4eUkJsO_mcjvo_MYnYx";

    public PlaceDetailsHelper(OnTaskCompleted listener){
        this.listener=listener;
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
                String detailsUrl = restaurantStringBuilder.toString();
                Object restaurantDetailsDataTransfer[] = new Object[1];
                restaurantDetailsDataTransfer[0] = detailsUrl;
                new RestaurantDetailsHelper(attraction).execute(restaurantDetailsDataTransfer);
            }

            // Find the right one and call
//            StringBuilder restaurantStringBuilder = new StringBuilder("https://api.yelp.com/v3/businesses/");
////        restaurantStringBuilder.append("latitude=" + 37.420941);
////        restaurantStringBuilder.append("&longitude=" + -122.0933529);
////        restaurantStringBuilder.append("&radius" + 0);
//            restaurantStringBuilder.append("3MLCZ99s5KcnAIgNpz8gag");
//            String detailsUrl = restaurantStringBuilder.toString();
//            Object restaurantDetailsDataTransfer[] = new Object[1];
//            restaurantDetailsDataTransfer[0] = detailsUrl;
//            new RestaurantDetailsHelper().execute(restaurantDetailsDataTransfer);
            listener.onTaskCompleted(attraction);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
