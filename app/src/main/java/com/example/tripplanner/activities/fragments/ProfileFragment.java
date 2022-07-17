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

import java.util.LinkedList;
import java.util.List;

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
    private Button btnNewTrip;

    private List<Trip> pastTripList;
    private RecyclerView rvPastTrips;
    private PastTripAdapter pastTripAdapter;

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
        btnNewTrip = view.findViewById(R.id.btnNewTrip);
        btnNewTrip.setOnClickListener(this);

        rvPastTrips = (RecyclerView) view.findViewById(R.id.rvPastTrips);
        pastTripList = new LinkedList<>();
        pastTripAdapter = new PastTripAdapter(getContext(), pastTripList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvPastTrips.setLayoutManager(linearLayoutManager);
        rvPastTrips.setAdapter(pastTripAdapter);

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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fbtnUpdateProfile) {
            Intent updateInfoIntent = new Intent(getContext(), UpdateActivity.class);
            startActivity(updateInfoIntent);
        }
        if (v.getId() == R.id.btnLogout) {
            onLogout();
        }
        if (v.getId() == R.id.btnNewTrip) {
            Intent tripInfoIntent = new Intent(getContext(), TripInfoActivity.class);
            startActivity(tripInfoIntent);
        }
    }

    private void onLogout() {
        Log.i(TAG, "onClick logout button");
        fbAuth.signOut();
        Intent i = new Intent(getContext(), LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}