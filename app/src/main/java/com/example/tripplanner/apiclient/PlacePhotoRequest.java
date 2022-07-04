package com.example.tripplanner.apiclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.example.tripplanner.OnTaskCompleted;
import com.example.tripplanner.models.Attraction;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PlacePhotoRequest extends AsyncTask<Object, String, String> {
    private static final String TAG = "PlacePhotoRequest";
    private OnTaskCompleted listener;
    private String url;
    private Attraction attraction;
    private InputStream is;
    private BufferedReader bufferedReader;
    private StringBuilder stringBuilder;
    private String data;

    public PlacePhotoRequest(OnTaskCompleted listener){
        this.listener=listener;
    }

    @Override
    protected String doInBackground(Object... objects) {
        url = (String) objects[0];
        attraction = (Attraction) objects[1];
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

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.i(TAG, "picture " + s);
        //TODO paarse response to get photo
//        byte[] imageAsBytes = Base64.decode(s, Base64.NO_WRAP);
//        InputStream inputStream = new ByteArrayInputStream(imageAsBytes);
//        Bitmap bpPhoto = BitmapFactory.decodeStream(inputStream);
        //Bitmap bpPhoto = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        //add it to attraction
       // attraction.photo = bpPhoto;
      //  listener.onTaskCompleted(attraction);
    }
}
