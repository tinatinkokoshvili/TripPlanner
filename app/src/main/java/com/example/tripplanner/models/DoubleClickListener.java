package com.example.tripplanner.models;

import android.os.SystemClock;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public abstract class DoubleClickListener implements GoogleMap.OnMarkerClickListener {

    // The time in which the second tap should be done in order to qualify as
    // a double click
    private static final String TAG = "DoubleClickListener";
    private static final long DEFAULT_QUALIFICATION_SPAN = 2000;
    private long doubleClickQualificationSpanInMillis;
    private long timestampLastClick;
    private String markerId;
    private LatLng markerPosition;

    public DoubleClickListener() {
        doubleClickQualificationSpanInMillis = DEFAULT_QUALIFICATION_SPAN;
        timestampLastClick = 0;
    }

    public DoubleClickListener(long doubleClickQualificationSpanInMillis) {
        this.doubleClickQualificationSpanInMillis = doubleClickQualificationSpanInMillis;
        timestampLastClick = 0;
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if((SystemClock.elapsedRealtime() - timestampLastClick) < doubleClickQualificationSpanInMillis) {
            markerId = marker.getId();
            markerPosition = marker.getPosition();
            onDoubleClick();
        }
        timestampLastClick = SystemClock.elapsedRealtime();
        return false;
    }
    public LatLng getMarkerPosition() {
        return markerPosition;
    }

    public abstract void onDoubleClick();
}
