package com.example.tripplanner.models;

import java.util.List;

public class Trip {
    private String tripName;
    private String userLatitude;
    private String userLongitude;
    private String radius;
    private double totalTripTime;
    private double avgStayTime;
    private double actualTotalTime;
    // Attractions are ordered correctly
    private List<Attraction> attractionsInTrip;

    public Trip() {}

    public Trip(String tripName, String userLatitude, String userLongitude,
                String radius, double totalTripTime, double avgStayTime, double actualTotalTime,
                List<Attraction> attractionsInTrip) {
        this.tripName = tripName;
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
        this.radius = radius;
        this.totalTripTime = totalTripTime;
        this.avgStayTime = avgStayTime;
        this.attractionsInTrip = attractionsInTrip;
        this.actualTotalTime = actualTotalTime;
    }

    public String getTripName() {
        return tripName;
    }

    public String getUserLatitude() {
        return userLatitude;
    }

    public String getUserLongitude() {
        return userLongitude;
    }

    public String getRadius() {
        return radius;
    }

    public double getAvgStayTime() {
        return avgStayTime;
    }

    public double getTotalTripTime() {
        return totalTripTime;
    }

    public double getActualTotalTime() {
        return actualTotalTime;
    }

    public List<Attraction> getAttractionsInTrip() {
        return attractionsInTrip;
    }
}