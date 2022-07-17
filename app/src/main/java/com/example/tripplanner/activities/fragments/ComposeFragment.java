package com.example.tripplanner.activities.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.tripplanner.R;
import com.example.tripplanner.activities.AttractionsActivity;
import com.example.tripplanner.activities.TripInfoActivity;

public class ComposeFragment extends Fragment {
    private EditText etTripName;
    private EditText etRadius;
    private EditText etToTalTime;
    private EditText etAvgStayTime;
    private Button btnCreateTrip;
    private String tripName;
    private String radius;
    private String totalTime;
    private String avgStayTime;

    private static String DEFAULT_RADIUS = "6000";
    private static String DEFAULT_TotalTime = "10.5";
    private static String DEFAULT_AVGSTAYTIME = "1"; //In hours

    public ComposeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etTripName = view.findViewById(R.id.etTripName);
        etRadius = view.findViewById(R.id.etRadius);
        etToTalTime = view.findViewById(R.id.etToTalTime);
        etAvgStayTime = view.findViewById(R.id.etAvgStayTime);
        btnCreateTrip = view.findViewById(R.id.btnCreateTrip);
        btnCreateTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputIsValid()) {
                    goToAttractionsActivity();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    private boolean inputIsValid() {
        tripName = etTripName.getText().toString().trim();
        radius = etRadius.getText().toString().trim();
        totalTime = etToTalTime.getText().toString().trim();
        avgStayTime = etAvgStayTime.getText().toString().trim();

        if (tripName.isEmpty()) {
            etTripName.setError("Trip Name is required");
            etTripName.requestFocus();
            return false;
        }

        // Check that totalTime is nonempty and integer
        if (radius.isEmpty()) {
            radius = DEFAULT_RADIUS;
            return false;
        } else if (!radius.matches("[0-9]+")) {
            etRadius.setError("Distance has to be an integer");
            etRadius.requestFocus();
            return false;
        }

        // Check that totalTime is nonempty and double or integer
        if (totalTime.isEmpty()) {
            totalTime = DEFAULT_TotalTime;
            return false;
        } else if (!totalTime.matches("([0-9]*)\\.([0-9]*)") && !totalTime.matches("[0-9]+")) {
            etToTalTime.setError("Total Time has to be a double.");
            etToTalTime.requestFocus();
            return false;
        }

        // Check that avgStayTime is nonempty and double or integer
        if (avgStayTime.isEmpty()) {
            avgStayTime = DEFAULT_AVGSTAYTIME;
            return false;
        } else if (avgStayTime.isEmpty() ||
                (!avgStayTime.matches("([0-9]*)\\.([0-9]*)") && !avgStayTime.matches("[0-9]+"))) {
            etAvgStayTime.setError("Invalid Average Stay Time");
            etAvgStayTime.requestFocus();
            return false;
        }
        return true;
    }

    private void goToAttractionsActivity() {
        Intent attractionsIntent = new Intent(getContext(), AttractionsActivity.class);
        attractionsIntent.putExtra("tripName", tripName);
        attractionsIntent.putExtra("radius", radius);
        attractionsIntent.putExtra("totalTime", totalTime);
        attractionsIntent.putExtra("avgStayTime", avgStayTime);
        startActivity(attractionsIntent);
    }
}