package com.example.tripplanner.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tripplanner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.LinkedList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ProfileActivity";
    private FirebaseAuth fbAuth;
    private String userID;
    UploadTask uploadTask;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    ImageView ivProfPagePic;
    TextView tvProfValFullName;
    TextView tvProfValUsername;
    FloatingActionButton fbtnUpdateProfile;
    Button btnLogout;
    Button btnNewTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fbAuth = FirebaseAuth.getInstance();
        userID = fbAuth.getCurrentUser().getUid();
        documentReference =
                firestore.collection("testUsers").document(userID);
        storageReference = firebaseStorage.getInstance().getReference("profile image");

        ivProfPagePic = findViewById(R.id.ivProfPagePic);
        tvProfValFullName = findViewById(R.id.tvProfValFullName);
        tvProfValUsername = findViewById(R.id.tvProfValUsername);
        fbtnUpdateProfile = findViewById(R.id.fbtnUpdateProfile);
        fbtnUpdateProfile.setOnClickListener(this);
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);
        btnNewTrip = findViewById(R.id.btnNewTrip);
        btnNewTrip.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firestore.collection("testUsers").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String fullName_result = task.getResult().getString("fullName");
                            String username_result = task.getResult().getString("username");
                            String picUrl_result = task.getResult().getString("picUrl");

                            Glide.with(ProfileActivity.this).load(picUrl_result).into(ivProfPagePic);
                            tvProfValFullName.setText(fullName_result);
                            tvProfValUsername.setText("@" + username_result);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Profile does not exist.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fbtnUpdateProfile) {
            Intent updateInfoIntent = new Intent(this, UpdateActivity.class);
            startActivity(updateInfoIntent);
        }
        if (v.getId() == R.id.btnLogout) {
            onLogout();
        }
        if (v.getId() == R.id.btnNewTrip) {
            Intent attractionsIntent = new Intent(this, AttractionsActivity.class);
            startActivity(attractionsIntent);
        }
    }

    private void onLogout() {
        Log.i(TAG, "onClick logout button");
        fbAuth.signOut();
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}