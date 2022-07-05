package com.example.tripplanner.apiclient;

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

public class DistanceMatrixHelper extends AsyncTask<Object, String, String> {
    private static final String TAG = "DistanceMatrixHelper";
    private OnTaskCompleted listener;
    private String url;
    private InputStream is;
    private BufferedReader bufferedReader;
    private StringBuilder stringBuilder;
    private String data;

    public DistanceMatrixHelper(OnTaskCompleted listener) {
        this.listener = listener;
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
        Log.i(TAG, "received json " + s);
       // int[][] distanceMatrix = new int[][];
        //listener.onTaskCompleted();
    }
}
