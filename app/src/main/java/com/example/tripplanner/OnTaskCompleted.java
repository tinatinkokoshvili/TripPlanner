package com.example.tripplanner;

import com.example.tripplanner.models.Attraction;

public interface OnTaskCompleted {
    void onTaskCompleted(Attraction attr);
    void onDistanceTaskCompleted(int distanceMatrix);
}
