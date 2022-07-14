package com.example.tripplanner.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.tripplanner.R;

public class TripInfoActivity extends AppCompatActivity {
    private EditText etTripName;
    private EditText etRadius;
    private EditText etToTalTime;
    private EditText etAvgStayTime;
    private Button btnCreateTrip;
    private String tripName;
    private String radius;
    private String totalTime;
    private String avgStayTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_info);

        etTripName = findViewById(R.id.etTripName);
        etRadius = findViewById(R.id.etRadius);
        etToTalTime = findViewById(R.id.etToTalTime);
        etAvgStayTime = findViewById(R.id.etAvgStayTime);
        btnCreateTrip = findViewById(R.id.btnCreateTrip);
        btnCreateTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputIsValid()) {
                    goToAttractionsActivity();
                }
            }
        });
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
        if (radius.isEmpty() || !radius.matches("[0-9]+")) {
            etRadius.setError("Invalid distance");
            etRadius.requestFocus();
            return false;
        }

        // Check that totalTime is nonempty and double or integer
        if (totalTime.isEmpty() ||
                (!totalTime.matches("([0-9]*)\\.([0-9]*)") && !totalTime.matches("[0-9]+"))) {
            etToTalTime.setError("Invalid Total Time");
            etToTalTime.requestFocus();
            return false;
        }

        // Check that avgStayTime is nonempty and double or integer
        if (avgStayTime.isEmpty() ||
                (!avgStayTime.matches("([0-9]*)\\.([0-9]*)") && !avgStayTime.matches("[0-9]+"))) {
            etAvgStayTime.setError("Invalid Average Stay Time");
            etAvgStayTime.requestFocus();
            return false;
        }
        return true;
    }

    private void goToAttractionsActivity() {
        Intent attractionsIntent = new Intent(TripInfoActivity.this, AttractionsActivity.class);
        attractionsIntent.putExtra("tripName", tripName);
        attractionsIntent.putExtra("radius", radius);
        attractionsIntent.putExtra("totalTime", totalTime);
        attractionsIntent.putExtra("avgStayTime", avgStayTime);
        startActivity(attractionsIntent);
    }
}