package com.example.tripplanner.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tripplanner.R;
import com.example.tripplanner.models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private EditText etEmail, etFullName, etSignUpUsername, etSignUpPassword;
    private Button btnSignup;
    private ProgressBar progressBar;
    private TextView tvUploadPic;
    private String userID;

    private Uri imageUri;
    private static final int PICK_IMAGE = 1;
    UploadTask uploadTask;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    ImageView ivPicture;
    ImageView ivUploadPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            Toast.makeText(SignupActivity.this, "User is already signed in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        storageReference = firebaseStorage.getInstance().getReference("profile image");

        ivPicture = findViewById(R.id.ivPicture);
        ivUploadPic = findViewById(R.id.ivUploadPic);
        etEmail = findViewById(R.id.etEmail);
        etFullName = findViewById(R.id.etFullName);
        etSignUpUsername = findViewById(R.id.etSignUpUsername);
        etSignUpPassword = findViewById(R.id.etSignUpPassword);
        btnSignup = findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "signup clicked");
                registerUser();
            }
        });
        tvUploadPic = findViewById(R.id.tvUploadPic);
        progressBar = findViewById(R.id.progressbar_cp);

    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String username = etSignUpUsername.getText().toString().trim();
        String password = etSignUpPassword.getText().toString().trim();

        if (!isValidInput(email, fullName, username, password)) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        final StorageReference reference =
                storageReference.child(System.currentTimeMillis() + "." + getFileExt(imageUri));
        uploadTask = reference.putFile(imageUri);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    // Authenticating user
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.i(TAG, "createUserWithEmail:success");
                                        User user = new User(email,  password, fullName, username, downloadUri.toString());
                                        userID = mAuth.getCurrentUser().getUid();
                                        // Add user data to database
                                        DocumentReference documentReference =
                                                firestore.collection("testUsers").document(userID);
                                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.i(TAG, "user info signup successful");
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                                startActivity(intent);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "user info signup failed");
                                            }
                                        });
                                    }
                                }
                            });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "picture upload failed");
            }
        });
    }

    private boolean isValidInput(String email, String fullName, String username, String password) {
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email format");
            etEmail.requestFocus();
            return false;
        }

        if (fullName.isEmpty()) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return false;
        }

        if (username.isEmpty()) {
            etSignUpUsername.setError("Username is required");
            etSignUpUsername.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etSignUpPassword.setError("Password is required");
            etSignUpPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etSignUpPassword.setError("Password must be at least 6 characters");
            etSignUpPassword.requestFocus();
            return false;
        }

        if (imageUri == null) {
            Toast.makeText(SignupActivity.this, "Please, upload image!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void chooseImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE || resultCode == RESULT_OK || data != null ||
            data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(ivPicture);
            ivUploadPic.setVisibility(View.GONE);
        }
    }

    //Detects the extension of the uploaded file
    private String getFileExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}