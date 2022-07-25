package com.example.tripplanner.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tripplanner.R;
import com.example.tripplanner.activities.fragments.ProfileFragment;
import com.example.tripplanner.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UpdateActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UpdateActivity";
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private String userID;
    UploadTask uploadTask;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Uri newImageUri;
    private String newImage;
    private String newUsername;
    private String newFullName;
    private String oldEmail;
    private String oldPassword;
    private static final int PICK_IMAGE = 1;

    EditText etUpdateFullName;
    EditText etUpdateUsername;
    ImageView ivUploadNewPic;
    Button btnUpdateInfo;
    ImageView ivNewPicture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(UpdateActivity.this, "No one is already signed in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        storageReference = firebaseStorage.getInstance().getReference("profile image");

        etUpdateFullName = findViewById(R.id.etUpdateFullName);
        etUpdateUsername = findViewById(R.id.etUpdateUsername);
        ivUploadNewPic = findViewById(R.id.ivUploadNewPic);
        btnUpdateInfo = findViewById(R.id.btnUpdateInfo);
        btnUpdateInfo.setOnClickListener(this);
        ivNewPicture = findViewById(R.id.ivNewPicture);

        firestore.collection("testUsers").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String fullName_result = task.getResult().getString("fullName");
                            String username_result = task.getResult().getString("username");
                            String picUrl_result = task.getResult().getString("picUrl");

                            etUpdateFullName.setText(fullName_result);
                            etUpdateUsername.setText(username_result);
                            newFullName = fullName_result;
                            newUsername = username_result;
                            newImage = picUrl_result;
                            oldEmail = task.getResult().getString("email");
                            oldPassword = task.getResult().getString("password");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateActivity.this, "Profile does not exist.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnUpdateInfo) {
            updateInfo();
        }
    }

    private void updateInfo() {
        String fullName = etUpdateFullName.getText().toString().trim();
        String username = etUpdateUsername.getText().toString().trim();

        if (fullName.isEmpty()) {
            etUpdateFullName.setError("Full name is required");
            etUpdateFullName.requestFocus();
            return;
        }

        if (username.isEmpty()) {
            etUpdateUsername.setError("Full name is required");
            etUpdateUsername.requestFocus();
            return;
        }

        if (newImageUri != null) {
            final StorageReference reference =
                    storageReference.child(System.currentTimeMillis() + "." + getFileExt(newImageUri));
            uploadTask = reference.putFile(newImageUri);
        }
        newFullName = fullName;
        newUsername = username;
        // Updating data, email and password stay the same
        User user = new User(oldEmail,  oldPassword, newFullName, newUsername, newImage);
        userID = mAuth.getCurrentUser().getUid();
        // Upload Data
        DocumentReference documentReference =
                firestore.collection("testUsers").document(userID);

        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.i(TAG, "user info signup successful");
                Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "user info signup failed");
            }
        });
    }

    public void chooseNewImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE || resultCode == RESULT_OK || data != null ||
                data.getData() != null) {
            newImageUri = data.getData();
            newImage = data.getData().toString();
            Glide.with(this).load(newImageUri).into(ivNewPicture);
            ivUploadNewPic.setVisibility(View.GONE);
        }
    }

    //Detects the extension of the uploaded file
    private String getFileExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}