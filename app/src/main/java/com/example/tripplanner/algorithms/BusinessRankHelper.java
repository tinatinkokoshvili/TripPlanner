package com.example.tripplanner.algorithms;

import android.util.Log;

import com.example.tripplanner.models.Attraction;
import com.example.tripplanner.models.Restaurant;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class BusinessRankHelper {
    private static final String TAG = "BusinessRankHelper";
    private static List<Attraction> googleRestaurants;
    private static List<Restaurant> yelpRestaurants;
    private static int numRestaurants;

    public BusinessRankHelper(List<Attraction> googleRestaurants, List<Restaurant> yelpRestaurants) {
        this.googleRestaurants = googleRestaurants;
        this.yelpRestaurants = yelpRestaurants;
        this.numRestaurants = yelpRestaurants.size();
    }

    public static List<Restaurant> getRankedBusinesses() {
        List<Restaurant> rankedRestaurants = new LinkedList<>();
        PriorityQueue<Restaurant> sortedRestaurantsQueue = sortWithNewRating();
        while(!sortedRestaurantsQueue.isEmpty()){
            // Adding at index 0 because queue is ascending order
            rankedRestaurants.add(0, sortedRestaurantsQueue.poll());
        }
        return rankedRestaurants;
    }

    public static PriorityQueue<Restaurant> sortWithNewRating() {
        PriorityQueue<Restaurant> priorityQueue = new PriorityQueue<>(new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant o1, Restaurant o2) {
                if (o1.googleYelpRating < o2.googleYelpRating) {
                    return -1;
                } else if (o1.googleYelpRating > o2.googleYelpRating) {
                    return 1;
                }
                return 0;
            }
        });
        for (int i = 0; i < yelpRestaurants.size(); i++) {
            if (yelpRestaurants.get(i) != null) {
                double googleRating = googleRestaurants.get(i).rating;
                double yelpRating = Double.parseDouble(yelpRestaurants.get(i).rating);
                int googleNumReviews = googleRestaurants.get(i).user_ratings_total;
                int yelpNumReviews = Integer.parseInt(yelpRestaurants.get(i).review_count);
                double googlePercent = ((double) googleNumReviews) / (googleNumReviews + yelpNumReviews);
                double yelpPercent = ((double) yelpNumReviews) / (googleNumReviews + yelpNumReviews);
                double avgRating = googleRating * googlePercent + yelpRating * yelpPercent;
                yelpRestaurants.get(i).googleYelpRating = avgRating;
                priorityQueue.add(yelpRestaurants.get(i));
            }
        }
        return priorityQueue;
    }
}
