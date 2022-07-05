package com.example.tripplanner.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.example.tripplanner.R;
import com.example.tripplanner.models.Attraction;

import android.util.Log;
import android.widget.ImageView;

import org.parceler.Parcels;

public class AtrDetailsActivity extends AppCompatActivity {
    private static final String TAG = "AtrDetailsActivity";
    private ImageView ivAtrPhoto;
    private Attraction attraction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atr_details);

        attraction = (Attraction) Parcels.unwrap(getIntent().getExtras().getParcelable(Attraction.class.getSimpleName()));
        ivAtrPhoto = findViewById(R.id.ivAtrDetailsPhoto);
        Log.i(TAG, "Details bitmap " +  attraction.photo);
        ivAtrPhoto.setImageBitmap(attraction.photo);
    }
}