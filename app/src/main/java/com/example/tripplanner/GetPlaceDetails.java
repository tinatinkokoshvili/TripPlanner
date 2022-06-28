package com.example.tripplanner;

import android.os.AsyncTask;
import android.util.Log;

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

public class GetPlaceDetails extends AsyncTask<Object, String, String> {
    private static final String TAG = "GetPlaceDetails";


    @Override
    protected String doInBackground(Object... objects) {
        //TODO
        return null;
    }

    //Parse the JSON data
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //TODO

    }
}
