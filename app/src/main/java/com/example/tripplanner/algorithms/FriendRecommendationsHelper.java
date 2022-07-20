package com.example.tripplanner.algorithms;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.tripplanner.models.Attraction;
import com.example.tripplanner.models.Trip;
import com.example.tripplanner.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Attr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FriendRecommendationsHelper {
    private static final String TAG = "FriendRecommendationsHelper";
    private static FirebaseAuth fbAuth;
    private static FirebaseFirestore firestore;
    private static String userID;
    private static String USER_COLLECTION_NAME;
    private static String TRIP_COLLECTION_NAME;
    private int userCounter;

    public FriendRecommendationsHelper(FirebaseAuth fbAuth, FirebaseFirestore firestore, String userID,
        String userCollectionName, String tripCollectionName) {
        this.fbAuth = fbAuth;
        this.firestore = firestore;
        this.userID = userID;
        USER_COLLECTION_NAME = userCollectionName;
        TRIP_COLLECTION_NAME = tripCollectionName;
        userCounter = 0;
        createTripListAllUsers();
    }

    private HashMap<String, List<Trip>> createTripListAllUsers() {
        HashMap<String, List<Trip>> userIdToTripsMap = new HashMap<>();

        firestore.collection(USER_COLLECTION_NAME).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    for (QueryDocumentSnapshot userDocumentSnapshot : task.getResult()) {
                        String curUserId = userDocumentSnapshot.getId();

                        firestore.collection(USER_COLLECTION_NAME)
                            .document(curUserId)
                            .collection(TRIP_COLLECTION_NAME).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> tripTask) {
                                    if (tripTask.isSuccessful() && tripTask.getResult() != null) {
                                        List<Trip> trips = new ArrayList<>();
                                        for (QueryDocumentSnapshot queryDocumentSnapshot : tripTask.getResult()) {
                                            Trip currTrip = queryDocumentSnapshot.toObject(Trip.class);
                                            trips.add(currTrip);
                                        }
                                        // Add the user to the map if they have at least 1 trips
                                        userIdToTripsMap.put(curUserId, trips);

                                        // Print for debugging
                                        if (task.getResult().size() == userIdToTripsMap.size()) {
                                            Log.i(TAG, "fetching all users trip are done. size: " + userIdToTripsMap.size());
                                            for (Map.Entry entry : userIdToTripsMap.entrySet()) {
                                                List<Trip> list = (ArrayList) entry.getValue();
                                                Log.i(TAG, "userID " + entry.getKey() + " tripsize " + list.size());
                                            }
                                            createDocumentsForAllUsers(userIdToTripsMap);
                                        }

                                    } else {
                                        Log.e(TAG, "Error getting trips: ", tripTask.getException());
                                    }
                                }
                            });
                    }
                } else {
                    Log.e(TAG, "Error while getting userIDs from firebase: ", task.getException());
                }
            }
        });

        return userIdToTripsMap;
    }

    private HashMap<String, Document> createDocumentsForAllUsers(HashMap<String, List<Trip>> map) {
        HashMap<String, Document> userIdToDocumentMap= new HashMap<>();
        HashMap<String, List<Trip>> userTripMap = map;
        for (Map.Entry entry : userTripMap.entrySet()) {
            String curUserId = (String) entry.getKey();
            ArrayList<Trip> curTripList = (ArrayList<Trip>) entry.getValue();
            if (curTripList.size() > 0) {
                String curUserAtrs = "";
                for (int i = 0; i < curTripList.size(); i++) {
                    ArrayList<Attraction> atrList = (ArrayList<Attraction>) curTripList.get(i).getAttractionsInTrip();
                    for (int j = 0; j < atrList.size(); j++) {
                        // If we want to recommend based on attractions and not words, then add ","
                        // instead of just space
                        curUserAtrs += atrList.get(j).getName() + " ";
                    }
                }
                Document userDocument = new Document(curUserId, curUserAtrs);
                userIdToDocumentMap.put(curUserId, userDocument);
            }
        }
        // Print for debugging
        for (Map.Entry entry : userIdToDocumentMap.entrySet()) {
            Document doc = (Document) entry.getValue();
            Log.i(TAG, "userID " + entry.getKey() + doc.toString());
        }
        return userIdToDocumentMap;
    }
}