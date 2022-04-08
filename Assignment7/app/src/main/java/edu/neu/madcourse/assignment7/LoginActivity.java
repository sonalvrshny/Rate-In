package edu.neu.madcourse.assignment7;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    TextView txtsignup, txtsignin;
    EditText txtusername;
    FirebaseAuth auth;
    ProgressBar progressDialog;
    String username;
    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtsignup = findViewById(R.id.signup);
        txtusername = findViewById(R.id.username);
        txtsignin = findViewById(R.id.signin);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        progressDialog = findViewById(R.id.progressBar2);
        txtsignup.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
            LoginActivity.this.finish();
        });
        txtsignin.setOnClickListener(v -> {
            progressDialog.setVisibility(View.VISIBLE);
            username = txtusername.getText().toString().trim();
            final String finalUsername = username;
            String password = "password";
            if (TextUtils.isEmpty(username)) {
                Toast.makeText(LoginActivity.this, "Invalid username", Toast.LENGTH_LONG).show();
            } else if (username.length() > 10) {
                Toast.makeText(LoginActivity.this, "Please enter username of less than 10 character", Toast.LENGTH_LONG).show();
            } else {
                username += "@gmail.com";
                auth.signInWithEmailAndPassword(username, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
//                                progressDialog.setVisibility(View.INVISIBLE);
                        reference.get().addOnCompleteListener(getTask -> {
                            if (!getTask.isSuccessful()) {
                                progressDialog.setVisibility(View.INVISIBLE);
                                Toast.makeText(LoginActivity.this, "Error in fetching token", Toast.LENGTH_LONG).show();
                                return;
                            }
                            Map<String, Long> history = (Map) getTask.getResult().child("history").getValue();
                            FirebaseMessaging.getInstance().getToken()
                                    .addOnCompleteListener(task1 -> {
                                        if (!task1.isSuccessful()) {
                                            progressDialog.setVisibility(View.INVISIBLE);
                                            Toast.makeText(LoginActivity.this, "Error in fetching token", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        String status = "Hey there I'm using EmoChat";
                                        Users users = new Users(auth.getUid(), finalUsername, status, task1.getResult(), history);
                                        reference.setValue(users).addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                progressDialog.setVisibility(View.INVISIBLE);

                                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                intent.putExtra("user", finalUsername);
                                                startActivity(intent);
                                                Toast.makeText(LoginActivity.this, "User loggedIn Successfully", Toast.LENGTH_LONG).show();

                                            } else {
                                                progressDialog.setVisibility(View.INVISIBLE);
                                                Toast.makeText(LoginActivity.this, "Error in loggedIn user", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    });
                        });

                    } else {
                        progressDialog.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, "User doesn't exist", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
