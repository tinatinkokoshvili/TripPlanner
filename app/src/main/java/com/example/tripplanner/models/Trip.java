package com.example.tripplanner.models;

import java.util.List;

public class Trip {
    private String tripName;
    private String userLatitude;
    private String userLongitude;
    private int radius;
    private double totalTripTime;
    private double avgStayTime;
    // Attractions are ordered correctly, userLocation not included at all
    private List<Attraction> attractionsInTrip;

    public Trip() {}

    public Trip(String tripName, String userLatitude, String userLongitude,
                int radius, double totalTripTime, double avgStayTime, List<Attraction> attractionsInTrip) {
        this.tripName = tripName;
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
        this.radius = radius;
        this.totalTripTime = totalTripTime;
        this.avgStayTime = avgStayTime;
        this.attractionsInTrip = attractionsInTrip;
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

    public int getRadius() {
        return radius;
    }

    public double getAvgStayTime() {
        return avgStayTime;
    }

    public List<Attraction> getAttractionsInTrip() {
        return attractionsInTrip;
    }
}