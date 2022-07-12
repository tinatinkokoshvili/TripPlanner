package com.example.tripplanner.yelp_helpers;

import android.os.AsyncTask;
import android.util.Log;

import com.example.tripplanner.OnTaskCompleted;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NearbyRestaurantsHelper extends AsyncTask<Object, String, String>  {
    private static final String TAG = "NearbyRestaurantsHelper";
    private static final String YELP_API_KEY = "PMULr-WmLQYIkh0t9kjvz9c2JPIyTkGdEg6Z7j85MeaLY0th1UOzFg_v_w4T914K_cQHjP4gOIoo2inrSi9JlqSW-Rq9QGyPNXkR-YZyTfMjD4eUkJsO_mcjvo_MYnYx";
    private OnTaskCompleted listener;
    private String url;
    private InputStream is;
    private BufferedReader bufferedReader;
    private StringBuilder stringBuilder;
    private String data;

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

        Log.i(TAG, "nearby restaurants fetched " + s);
    }
}
