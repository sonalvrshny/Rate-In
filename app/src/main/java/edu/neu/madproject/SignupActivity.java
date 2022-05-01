package edu.neu.madproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;


public class SignupActivity extends AppCompatActivity {
    String TAG = "SignupActivityDebug";

    TextView login_already_account, signup_button;
    EditText username_signup;
    EditText password_signup;

    ProgressBar progressBar;
    
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // find view by id
        login_already_account = findViewById(R.id.login);
        signup_button = findViewById(R.id.signup);
        username_signup = findViewById(R.id.username_signup);
//        password_signup = findViewById(R.id.password_signup);
        progressBar = findViewById(R.id.progressBar_signup);

        // firebase instance
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // clicking on sign up
        signup_button.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String username = username_signup.getText().toString().trim();
//            String password = password_signup.getText().toString();
            String password="password";
            // invalid username
            if (TextUtils.isEmpty(username)) {
                // Toast.makeText(SignupActivity.this, "Please enter a username", Toast.LENGTH_LONG).show();
                username_signup.setError("Please enter username");
                username_signup.requestFocus();
                progressBar.setVisibility(View.INVISIBLE);
            }
            // invalid password
            else if (TextUtils.isEmpty(password)) {
                // Toast.makeText(SignupActivity.this, "Please enter a password", Toast.LENGTH_LONG).show();
                password_signup.setError("Please enter password");
                password_signup.requestFocus();
                progressBar.setVisibility(View.INVISIBLE);
            }
            // minimum length of password should be 6
            else if (password.length() < 6) {
                // Toast.makeText(SignupActivity.this, "Password should be longer than 6 char", Toast.LENGTH_LONG).show();
                password_signup.setError("Password should be longer than 6 char");
                password_signup.requestFocus();
                progressBar.setVisibility(View.INVISIBLE);
            }
            // valid credentials, create account with username and password
            else {
                // add username with password to firebase
                String usernameDB = username + "@gmail.com";
                Log.d(TAG, username + " " + password);

                auth.createUserWithEmailAndPassword(usernameDB, password).addOnCompleteListener(task ->  {
//                    DatabaseReference reference = database.getReference().child("user").child(Objects.requireNonNull(auth.getUid()));
                    Log.d(TAG, "task success " + task.isSuccessful());
                    if (task.isSuccessful()) {
                        SharedPrefUtils.saveEmail(username, this);
                        SharedPrefUtils.savePassword(password, this);
                        reference = database.getReference().child("user").child(Objects.requireNonNull(auth.getUid()));
                        Log.d(TAG, "Created an account with credentials");
                        Users user = new Users(auth.getUid(), usernameDB);
                        DatabaseReference reference = database.getReference().child("user").child(Objects.requireNonNull(auth.getUid()));
                        Log.d(TAG, "task success " + reference);
                        reference.setValue(user).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
//                        Intent intent = new Intent(SignupActivity.this, FeedActivity.class);
                                Intent intent = new Intent(SignupActivity.this, CategoriesActivity.class);
                                intent.putExtra("user", usernameDB);
                                intent.putExtra("prev", "auth");
                                startActivity(intent);
                                Toast.makeText(SignupActivity.this, "User created successfully!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(SignupActivity.this, "Error creating user account", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        Toast.makeText(SignupActivity.this, "Account already exists", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            progressBar.setVisibility(View.INVISIBLE);
        });

        // start the LoginActivity page when already a member
        login_already_account.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            SignupActivity.this.finish();
        });
    }
}