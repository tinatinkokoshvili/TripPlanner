package com.example.tripplanner.api_client;

import android.os.AsyncTask;
import android.util.Log;

import com.example.tripplanner.OnTaskCompleted;
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
            Log.i(TAG, "onTaskCompleted from GetDetails " + attraction.name);
            listener.onTaskCompleted(attraction);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
