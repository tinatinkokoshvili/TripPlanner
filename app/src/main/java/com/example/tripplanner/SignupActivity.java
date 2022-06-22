package com.example.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SignupActivity";
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private EditText etEmail, etFullName, etUsername, etPassword;
    private Button btnSignup;
    //private ProgressBar progressBar;
    private TextView tvUploadPic;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


//        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        mAuth.signOut();
        if (mAuth.getCurrentUser() != null) {
            finish();
            return;
        }

        etEmail = findViewById(R.id.etEmail);
        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        btnSignup = findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(this);
        tvUploadPic = findViewById(R.id.tvUpladPic);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignup:
                Log.i(TAG, "signup clicked");
                registerUser();
                break;
            default:
                break;
        }
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();


        if (email.isEmpty()) {
            etEmail.setError("Full name is required");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email format");
            etEmail.requestFocus();
            return;
        }

        if (fullName.isEmpty()) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return;
        }

        if (username.isEmpty()) {
            etUsername.setError("Full name is required");
            etUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Full name is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        //progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "createUserWithEmail:success");
                            User user = new User(email,  password, fullName, username);
                            userID = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = firestore.collection("testUsers").document(userID);
//                            Map<String, Object> userMap = new HashMap<>();
//                            userMap.put("fullName", fullName);
//                            userMap.put("email", email);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.i(TAG, "user info signup successful");
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