package com.example.tripplanner.activities.fragments;

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

import com.example.tripplanner.R;
import com.example.tripplanner.adapters.PastTripAdapter;
import com.example.tripplanner.models.Trip;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PastTripsFragment extends Fragment {
    private static final String TAG = "PastTripsFragment";
    private FirebaseAuth fbAuth;
    private String userID;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private RecyclerView rvPastTrips;
    private PastTripAdapter pastTripAdapter;
    private List<Trip> pastTripList;

    public PastTripsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_past_trips, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fbAuth = FirebaseAuth.getInstance();
        userID = fbAuth.getCurrentUser().getUid();
        rvPastTrips = (RecyclerView) view.findViewById(R.id.rvPastTrips);
        pastTripList = new LinkedList<>();
        pastTripAdapter = new PastTripAdapter(getActivity(), pastTripList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvPastTrips.setLayoutManager(linearLayoutManager);
        rvPastTrips.setAdapter(pastTripAdapter);

        // Populate the past trip recycler view
        pastTripAdapter.clear();
        List<Trip> trips = new ArrayList<>();

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
                                            for (QueryDocumentSnapshot queryDocumentSnapshot : tripTask.getResult()) {
                                                Trip currTrip = queryDocumentSnapshot.toObject(Trip.class);
                                                trips.add(currTrip);
                                                pastTripAdapter.add(currTrip);
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
    }
}