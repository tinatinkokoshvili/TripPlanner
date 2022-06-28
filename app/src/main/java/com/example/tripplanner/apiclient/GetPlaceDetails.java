package com.example.tripplanner.apiclient;

import android.os.AsyncTask;
import android.util.Log;

import com.example.tripplanner.models.Attraction;

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
    String url;
    InputStream is;
    BufferedReader bufferedReader;
    StringBuilder stringBuilder;
    String data;

    @Override
    protected String doInBackground(Object... objects) {
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
            Attraction attraction = Attraction.fromJson(resultObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }
}