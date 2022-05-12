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
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;


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
//        password_login = findViewById(R.id.password_login);
        progressBar = findViewById(R.id.progressBar_login);

        // firebase instance
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        if (SharedPrefUtils.getEmail(this) != null && !SharedPrefUtils.getEmail(this).equals("")) {
            Log.d("SharedPref", SharedPrefUtils.getEmail(this));
            Intent intent = new Intent(LoginActivity.this, FeedActivity.class);
//            intent.putExtra("user", username_login.getText().toString());
//            intent.putExtra("prev", "auth");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Calendar cal = Calendar.getInstance();
            Helper.scheduleNotification(this, auth.getUid(), cal);
            startActivity(intent);
        }

        // clicking on login button
        login_button.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String username = username_login.getText().toString();
//            String password = password_login.getText().toString();
            String password="password";
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
                String usernameDB = username + "@gmail.com";
                auth.signInWithEmailAndPassword(usernameDB, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SharedPrefUtils.saveEmail(username, this);
                        SharedPrefUtils.savePassword(password, this);
                        Intent intent = new Intent(LoginActivity.this, FeedActivity.class);
                        // Intent intent = new Intent(LoginActivity.this, CategoriesActivity.class);
                        intent.putExtra("user", usernameDB);
                        intent.putExtra("prev", "auth");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        Calendar cal = Calendar.getInstance();
                        Helper.scheduleNotification(this, auth.getUid(), cal);
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
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