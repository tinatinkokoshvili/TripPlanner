package com.example.tripplanner.activities.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tripplanner.R;
import com.example.tripplanner.activities.LoginActivity;
import com.example.tripplanner.activities.ProfileActivity;
import com.example.tripplanner.activities.TripInfoActivity;
import com.example.tripplanner.activities.UpdateActivity;
import com.example.tripplanner.adapters.PastTripAdapter;
import com.example.tripplanner.algorithms.Corpus;
import com.example.tripplanner.algorithms.Document;
import com.example.tripplanner.algorithms.FriendRecommendationsHelper;
import com.example.tripplanner.algorithms.VectorSpaceModel;
import com.example.tripplanner.models.Attraction;
import com.example.tripplanner.models.Trip;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ProfileFragment";
    private FirebaseAuth fbAuth;
    private String userID;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private ImageView ivProfPagePic;
    private TextView tvProfValFullName;
    private TextView tvProfValUsername;
    private FloatingActionButton fbtnUpdateProfile;
    private Button btnLogout;

//    private List<Trip> pastTripList;
//    private RecyclerView rvPastTrips;
//    private PastTripAdapter pastTripAdapter;

    private static String USER_COLLECTION_NAME = "tripUsers";
    private static String TRIP_COLLECTION_NAME = "trips";
    HashMap<String, Document> userIdToDocumentMap;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fbAuth = FirebaseAuth.getInstance();
        userID = fbAuth.getCurrentUser().getUid();
        documentReference =
                firestore.collection("testUsers").document(userID);
        storageReference = firebaseStorage.getInstance().getReference("profile image");

        ivProfPagePic = view.findViewById(R.id.ivProfPagePic);
        tvProfValFullName = view.findViewById(R.id.tvProfValFullName);
        tvProfValUsername = view.findViewById(R.id.tvProfValUsername);
        fbtnUpdateProfile = view.findViewById(R.id.fbtnUpdateProfile);
        fbtnUpdateProfile.setOnClickListener(this);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);

//        rvPastTrips = (RecyclerView) view.findViewById(R.id.rvPastTrips);
//        pastTripList = new LinkedList<>();
//        pastTripAdapter = new PastTripAdapter(getContext(), pastTripList);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        rvPastTrips.setLayoutManager(linearLayoutManager);
//        rvPastTrips.setAdapter(pastTripAdapter);

        getDataFromDb();
    }

    public void getDataFromDb() {
        firestore.collection("testUsers").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String fullName_result = task.getResult().getString("fullName");
                            String username_result = task.getResult().getString("username");
                            String picUrl_result = task.getResult().getString("picUrl");

                            Glide.with(getContext()).load(picUrl_result).into(ivProfPagePic);
                            tvProfValFullName.setText(fullName_result);
                            tvProfValUsername.setText("@" + username_result);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Profile does not exist.", Toast.LENGTH_SHORT).show();
                    }
                });
        // Populate the friend recommendations recycler view

        userIdToDocumentMap = new HashMap<>();
        Log.i(TAG, "About to call createTripListAllUsers");
        createTripListAllUsers();


        // Populate the past trip recycler view
//        pastTripAdapter.clear();
//        firestore.collection("testUsers").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .collection("trips").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful() && task.getResult() != null) {
//                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
//                                //List<Object> objectList = (List<Object>) queryDocumentSnapshot.get("attractionsInTrip");
//                                Trip trip = queryDocumentSnapshot.toObject(Trip.class);
//                                Log.i(TAG, "Trip Name" + trip.getTripName() + " user latitude " + trip.getUserLatitude());
//
//                                Log.i(TAG, "atr " + trip.getAttractionsInTrip().get(2).getPlaceId());
//                                pastTripAdapter.add(trip);
//                            }
//                        } else {
//                            Log.e(TAG, "Error getting trips: ", task.getException());
//                        }
//                    }
//                });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fbtnUpdateProfile) {
            Intent updateInfoIntent = new Intent(getContext(), UpdateActivity.class);
            startActivity(updateInfoIntent);
        }
        if (v.getId() == R.id.btnLogout) {
            onLogout();
        }
//        if (v.getId() == R.id.btnNewTrip) {
//            Intent tripInfoIntent = new Intent(getContext(), TripInfoActivity.class);
//            startActivity(tripInfoIntent);
//        }
    }

    private void onLogout() {
        Log.i(TAG, "onClick logout button");
        fbAuth.signOut();
        Intent i = new Intent(getContext(), LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
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
        Log.i(TAG, "Started createTripListAllUsers");
        HashMap<String, List<Trip>> userIdToTripsMap = new HashMap<>();

        firestore.collection("testUsers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    for (QueryDocumentSnapshot userDocumentSnapshot : task.getResult()) {

                        String curUserId = userDocumentSnapshot.getId();
                        Log.i(TAG, curUserId);
                        firestore.collection("testUsers")
                                .document(curUserId)
                                .collection("trips").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

                                            Log.i(TAG, "task size " + task.getResult().size() + " userIdToTripsMap.size() " + userIdToTripsMap.size());
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
        populateFriendRecRecyclerView(rankedUserList);
        return rankedUserList;
    }

    private void populateFriendRecRecyclerView(ArrayList<String> friendRecs) {
        return;
    }
}