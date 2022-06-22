package com.example.tripplanner;

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

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ProfileActivity";
    private FirebaseAuth mAuth;
    private String userID;
    UploadTask uploadTask;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    ImageView ivProfPagePic;
    TextView tvProfValFullName;
    TextView tvProfValUsername;
    FloatingActionButton fbtnNewTrip;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        documentReference =
                firestore.collection("testUsers").document(userID);
        storageReference = firebaseStorage.getInstance().getReference("profile image");

        ivProfPagePic = findViewById(R.id.ivProfPagePic);
        tvProfValFullName = findViewById(R.id.tvProfValFullName);
        tvProfValUsername = findViewById(R.id.tvProfValUsername);
        fbtnNewTrip = findViewById(R.id.fbtnNewTrip);
        fbtnNewTrip.setOnClickListener(this);
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);
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
        if (v.getId() == R.id.fbtnNewTrip) {
            Intent updateInfoIntent = new Intent(this, SignupActivity.class);
            startActivity(updateInfoIntent);
        }
        if (v.getId() == R.id.btnLogout) {
            onLogout();
        }
    }

    private void onLogout() {
        Log.i(TAG, "onClick logout button");
        mAuth.signOut();
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}