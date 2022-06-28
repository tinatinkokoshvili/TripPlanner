package com.example.tripplanner.models;

import com.example.tripplanner.models.Attraction;

import java.util.ArrayList;
import java.util.List;

public class Attractions {
    static List<Attraction> attractionList;
    static List<String> placeIds;

    Attractions() {
        attractionList = new ArrayList<>();
        placeIds = new ArrayList<>();
    }

    Attractions(List<Attraction> attList, List<String> placeIds) {
        attractionList = attList;
        this.placeIds = placeIds;
    }

    protected static void addPlaceId(String placeId) {
        placeIds.add(placeId);
    }

    protected static void clear() {
        attractionList = new ArrayList<>();
        placeIds = new ArrayList<>();
    }






}
