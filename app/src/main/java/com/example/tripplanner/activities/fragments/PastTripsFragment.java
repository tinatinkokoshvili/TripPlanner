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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tripplanner.R;
import com.example.tripplanner.activities.ProfileActivity;
import com.example.tripplanner.adapters.PastTripAdapter;
import com.example.tripplanner.models.Trip;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.LinkedList;
import java.util.List;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fbAuth = FirebaseAuth.getInstance();
        userID = fbAuth.getCurrentUser().getUid();

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

        rvPastTrips = (RecyclerView) view.findViewById(R.id.rvPastTrips);
        pastTripList = new LinkedList<>();
        pastTripAdapter = new PastTripAdapter(getContext(), pastTripList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvPastTrips.setLayoutManager(linearLayoutManager);
        rvPastTrips.setAdapter(pastTripAdapter);

        // Populate the past trip recycler view
        pastTripAdapter.clear();
        firestore.collection("testUsers").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("trips").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                //List<Object> objectList = (List<Object>) queryDocumentSnapshot.get("attractionsInTrip");
                                Trip trip = queryDocumentSnapshot.toObject(Trip.class);
                                Log.i(TAG, "Trip Name" + trip.getTripName() + " user latitude " + trip.getUserLatitude());

                                Log.i(TAG, "atr " + trip.getAttractionsInTrip().get(2).getPlaceId());
                                pastTripAdapter.add(trip);
                            }
                        } else {
                            Log.e(TAG, "Error getting trips: ", task.getException());
                        }
                    }
                });
    }
}