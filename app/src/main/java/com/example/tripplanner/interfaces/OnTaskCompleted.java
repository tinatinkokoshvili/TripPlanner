package com.example.tripplanner.interfaces;

import com.example.tripplanner.models.Attraction;
import com.example.tripplanner.models.Restaurant;

public interface OnTaskCompleted {
    void onTaskCompleted(Attraction attr);
    void onDurationTaskCompleted(int[][] durationMatrix);
    void onRestaurantTaskCompleted(Attraction attraction, Restaurant restaurant, int numOfTotalRestaurants);
    void addNullToYelpList();
}
