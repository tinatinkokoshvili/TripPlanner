package com.example.tripplanner.algorithms;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.tripplanner.models.Attraction;
import com.example.tripplanner.models.Trip;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

public class FriendRecommendationsHelper {
    private static final String TAG = "FriendRecommendationsHelper";
    private static FirebaseAuth fbAuth;
    private static FirebaseFirestore firestore;
    private static String userID;
    private static String USER_COLLECTION_NAME;
    private static String TRIP_COLLECTION_NAME;
    HashMap<String, Document> userIdToDocumentMap;

    public FriendRecommendationsHelper(FirebaseAuth fbAuth, FirebaseFirestore firestore, String userID,
        String userCollectionName, String tripCollectionName) {
        this.fbAuth = fbAuth;
        this.firestore = firestore;
        this.userID = userID;
        USER_COLLECTION_NAME = userCollectionName;
        TRIP_COLLECTION_NAME = tripCollectionName;
        userIdToDocumentMap = new HashMap<>();
        createTripListAllUsers();
    }

    private ArrayList<String> createRankedSimilarUsers() {
        ArrayList<Document> documents = new ArrayList<Document>();
        TreeMap<String, Double> cosineSimilarityToFriendMap = new TreeMap<>();

        for (Map.Entry entry : userIdToDocumentMap.entrySet()) {
                documents.add((Document) entry.getValue());
        }
        Corpus corpus = new Corpus(documents);
        VectorSpaceModel vectorSpace = new VectorSpaceModel(corpus);
        Document userDocument = userIdToDocumentMap.get(userID);

        for (int i = 0; i < documents.size(); i++) {
            Document friendDocument = documents.get(i);
            double cosSimilarity = vectorSpace.cosineSimilarity(friendDocument, userDocument);
            cosineSimilarityToFriendMap.put(friendDocument.getUserId(), cosSimilarity);
        }
        PriorityQueue<String> userPriorityQueue = new PriorityQueue<String>(userIdToDocumentMap.size(), new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                //if (cosineSimilarityToFriendMap.containsKey(o1) && cosineSimilarityToFriendMap.containsKey(o2)) {
                if (cosineSimilarityToFriendMap.get(o1) < cosineSimilarityToFriendMap.get(o2)) {
                    return 1;
                } else if (cosineSimilarityToFriendMap.get(o1) > cosineSimilarityToFriendMap.get(o2)) {
                    return -1;
                }
                // }
                return 0;
            }
        });
        for (Map.Entry entry : userIdToDocumentMap.entrySet()) {
                userPriorityQueue.add((String) entry.getKey());
        }
        ArrayList<String> rankedUserIds = new ArrayList<>();
        while (!userPriorityQueue.isEmpty()) {
            rankedUserIds.add(userPriorityQueue.poll());
        }
        // Print for debugging
        for (int i = 0; i < rankedUserIds.size(); i++) {
            Log.i(TAG, rankedUserIds.get(i) + " cos sim " + cosineSimilarityToFriendMap.get(rankedUserIds.get(i)));
        }

        return rankedUserIds;
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
                                            getRankedUserId(userIdToTripsMap);
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

    private ArrayList<String> getRankedUserId(HashMap<String, List<Trip>> map) {
        HashMap<String, List<Trip>> userTripMap = map;
        for (Map.Entry entry : userTripMap.entrySet()) {
            String curUserId = (String) entry.getKey();
            ArrayList<Trip> curTripList = (ArrayList<Trip>) entry.getValue();
            if (curTripList.size() > 0) {
                String curUserAtrs = "";
                for (int i = 0; i < curTripList.size(); i++) {
                    ArrayList<Attraction> atrList = (ArrayList<Attraction>) curTripList.get(i).getAttractionsInTrip();
                    // Starting from 1 to avoid adding user location each time because it
                    // does not really change anything
                    for (int j = 1; j < atrList.size(); j++) {
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
        ArrayList<String> rankedUserList = createRankedSimilarUsers();
        Log.i(TAG, "rankedUserList size " + rankedUserList.size());
        return rankedUserList;
    }
}