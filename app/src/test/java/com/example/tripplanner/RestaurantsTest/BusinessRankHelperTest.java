package com.example.tripplanner.RestaurantsTest;

import org.junit.Test;

import static org.junit.Assert.*;

import androidx.annotation.VisibleForTesting;

import com.example.tripplanner.algorithms.BusinessRankHelper;
import com.example.tripplanner.models.Attraction;
import com.example.tripplanner.models.Restaurant;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class BusinessRankHelperTest {

    @Test
    public void sortWithNewRatingTest() {
        List<Attraction> googleRestaurants = new LinkedList<>();
        List<Restaurant> yelpRestaurants = new LinkedList<>();
        Attraction firstGoogleRestaurant = new Attraction();
        firstGoogleRestaurant.rating = 5;
        firstGoogleRestaurant.user_ratings_total = 1045;
        firstGoogleRestaurant.name = "first";
        googleRestaurants.add(firstGoogleRestaurant);
        Attraction secondGoogleRestaurant = new Attraction();
        secondGoogleRestaurant.rating = 3;
        secondGoogleRestaurant.user_ratings_total = 800;
        secondGoogleRestaurant.name = "second";
        googleRestaurants.add(secondGoogleRestaurant);

        Restaurant firstYelpRestaurant = new Restaurant();
        firstYelpRestaurant.rating = "5";
        firstYelpRestaurant.review_count = "234";
        firstYelpRestaurant.name = "first";
        yelpRestaurants.add(firstYelpRestaurant);
        Restaurant secondYelpRestaurant = new Restaurant();
        secondYelpRestaurant.rating = "3";
        secondYelpRestaurant.review_count = "6030";
        secondYelpRestaurant.name = "second";
        yelpRestaurants.add(secondYelpRestaurant);

        BusinessRankHelper businessRankHelper = new BusinessRankHelper(googleRestaurants, yelpRestaurants);
        PriorityQueue<Restaurant> result = businessRankHelper.sortWithNewRating();
        assertEquals("second", result.poll().name);
        assertEquals("first", result.poll().name);
    }

    @Test
    public void emptyLists() {
        List<Attraction> googleRestaurants = new LinkedList<>();
        List<Restaurant> yelpRestaurants = new LinkedList<>();

        BusinessRankHelper businessRankHelper = new BusinessRankHelper(googleRestaurants, yelpRestaurants);
        PriorityQueue<Restaurant> result = businessRankHelper.sortWithNewRating();
        assertEquals(0, result.size());
    }

    @Test
    public void getRankedBusinessesTest() {
        List<Attraction> googleRestaurants = new LinkedList<>();
        List<Restaurant> yelpRestaurants = new LinkedList<>();
        Attraction firstGoogleRestaurant = new Attraction();
        firstGoogleRestaurant.rating = 5;
        firstGoogleRestaurant.user_ratings_total = 1045;
        firstGoogleRestaurant.name = "first";
        googleRestaurants.add(firstGoogleRestaurant);
        Attraction secondGoogleRestaurant = new Attraction();
        secondGoogleRestaurant.rating = 3;
        secondGoogleRestaurant.user_ratings_total = 800;
        secondGoogleRestaurant.name = "second";
        googleRestaurants.add(secondGoogleRestaurant);

        Restaurant firstYelpRestaurant = new Restaurant();
        firstYelpRestaurant.rating = "5";
        firstYelpRestaurant.review_count = "234";
        firstYelpRestaurant.name = "first";
        yelpRestaurants.add(firstYelpRestaurant);
        Restaurant secondYelpRestaurant = new Restaurant();
        secondYelpRestaurant.rating = "3";
        secondYelpRestaurant.review_count = "6030";
        secondYelpRestaurant.name = "second";
        yelpRestaurants.add(secondYelpRestaurant);

        BusinessRankHelper businessRankHelper = new BusinessRankHelper(googleRestaurants, yelpRestaurants);
        List<Restaurant> rankedRestaurant = businessRankHelper.getRankedBusinesses();
        assertEquals("first", rankedRestaurant.get(0).name);
        assertEquals("second", rankedRestaurant.get(1).name);
    }

}
