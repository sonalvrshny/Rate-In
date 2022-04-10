package edu.neu.madproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    String TAG = "LoginActivityDebug";

    EditText username_login;
    EditText password_login;
    TextView signup_create_account, login_button;

    ProgressBar progressBar;

    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // find view by id
        signup_create_account = findViewById(R.id.signup);
        login_button = findViewById(R.id.signin);
        username_login = findViewById(R.id.username_login);
        password_login = findViewById(R.id.password_login);
        progressBar = findViewById(R.id.progressBar_login);

        // firebase instance
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // clicking on login button
        login_button.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String username = username_login.getText().toString();
            String password = password_login.getText().toString();

            // username field blank
            if (TextUtils.isEmpty(username)) {
                // Toast.makeText(SignupActivity.this, "Please enter a username", Toast.LENGTH_LONG).show();
                username_login.setError("Please enter username");
                username_login.requestFocus();
                progressBar.setVisibility(View.INVISIBLE);
            }
            // password field blank
            else if (TextUtils.isEmpty(password)) {
                // Toast.makeText(SignupActivity.this, "Please enter a password", Toast.LENGTH_LONG).show();
                password_login.setError("Please enter password");
                password_login.requestFocus();
                progressBar.setVisibility(View.INVISIBLE);
            }
            else {
                // sign in with username and password provided
                username += "@gmail.com";
                String usernameDB = username;
                auth.signInWithEmailAndPassword(username, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(LoginActivity.this, FeedActivity.class);
                        intent.putExtra("user", usernameDB);
                        startActivity(intent);
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Error logging in", Toast.LENGTH_SHORT).show();
                    }
                });
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        // start the SignupActivity page when creating a new account
        signup_create_account.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            LoginActivity.this.finish();
        });
    }
}