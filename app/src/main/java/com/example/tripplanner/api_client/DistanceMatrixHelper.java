package com.example.tripplanner.api_client;

import android.os.AsyncTask;
import android.util.Log;

import com.example.tripplanner.interfaces.OnTaskCompleted;

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

public class DistanceMatrixHelper extends AsyncTask<Object, String, String> {
    private static final String TAG = "DistanceMatrixHelper";
    private OnTaskCompleted listener;
    private String url;
    private InputStream is;
    private BufferedReader bufferedReader;
    private StringBuilder stringBuilder;
    private String data;

    private static final String ROWS = "rows";
    private static final String ELEMENTS = "elements";
    private static final String DURATION_IN_TRAFFIC = "duration_in_traffic";
    private static final String DURATION = "duration";
    private static final String VALUE = "value";

    public DistanceMatrixHelper(OnTaskCompleted listener) {
        this.listener = listener;
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

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.i(TAG, "received json " + s);
        // Parse json to create the duration_in_traffic matrix
        try {
            JSONObject parentObject = new JSONObject(s);
            JSONArray rowsArray = parentObject.getJSONArray(ROWS);
            int[][] durationMatrix = new int[rowsArray.length()][rowsArray.length()];
            for (int i = 0; i < rowsArray.length(); i++) {
                JSONArray elements = rowsArray.getJSONObject(i).getJSONArray(ELEMENTS);
                for (int j = 0; j < elements.length(); j++) {
                    JSONObject trip = elements.getJSONObject(j);
                    if (trip.has(DURATION_IN_TRAFFIC)) {
                        durationMatrix[i][j] = trip.getJSONObject(DURATION_IN_TRAFFIC).getInt(VALUE);
                    } else {
                        durationMatrix[i][j] = trip.getJSONObject(DURATION).getInt(VALUE);
                    }
                    Log.i(TAG, "cell filled " + durationMatrix[i][j]);
                }
            }
            listener.onDurationTaskCompleted(durationMatrix);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
