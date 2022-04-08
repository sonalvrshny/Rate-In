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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {
    TextView txtSignin, txtSignUp;
    EditText txtusername;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressBar progressDialog;      // Removing ProgressdDialog as it is deprecated.
    List<Map<String, String>> history;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        txtSignin = findViewById(R.id.signin);
        txtSignUp = findViewById(R.id.signup);
        txtusername = findViewById(R.id.username);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        progressDialog = findViewById(R.id.progressBar);
        //progressDialog=new ProgressDialog(this);
        //progressDialog.setMessage("Please wait...");
        txtSignin.setOnClickListener(v -> {
            startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class));
            CreateAccountActivity.this.finish();
        });
        txtSignUp.setOnClickListener(v -> {
            progressDialog.setVisibility(View.VISIBLE);
            String uname = txtusername.getText().toString().trim();
            String password = "password";
            String status = "Hey there I'm using EmoChat";
            if (TextUtils.isEmpty(uname)) {
                Toast.makeText(CreateAccountActivity.this, "Invalid username", Toast.LENGTH_LONG).show();
                progressDialog.setVisibility(View.INVISIBLE);
            } else if (uname.length() > 10) {
                Toast.makeText(CreateAccountActivity.this, "Please enter username of less than 10 character", Toast.LENGTH_LONG).show();
                progressDialog.setVisibility(View.INVISIBLE);
            } else {
                uname += "@gmail.com";
                String username = uname;
                Log.d("Debug", username + " " + password);
                auth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
                        Log.d("Debug", auth.getUid() + " " + username);

                        database.getReference().child("stickers").get().addOnCompleteListener(
                                task3 -> {
                                    if (!task3.isSuccessful()) {
                                        progressDialog.setVisibility(View.INVISIBLE);
                                        Toast.makeText(CreateAccountActivity.this, "Error in creating user", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    Map<String, String> snap = (Map) task3.getResult().getValue();
                                    List<String> emojiList = new ArrayList<>(snap.keySet());
                                    FirebaseMessaging.getInstance().getToken()
                                            .addOnCompleteListener(task1 -> {
                                                if (!task1.isSuccessful()) {
                                                    progressDialog.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(CreateAccountActivity.this, "Error in fetching token", Toast.LENGTH_LONG).show();
                                                    return;
                                                }
                                                Users users = new Users(auth.getUid(), username, status, task1.getResult(), emojiList);
                                                reference.setValue(users).addOnCompleteListener(task2 -> {
                                                    if (task2.isSuccessful()) {
                                                        progressDialog.setVisibility(View.INVISIBLE);
                                                        Intent intent = new Intent(CreateAccountActivity.this, HomeActivity.class);
                                                        intent.putExtra("user", username);
                                                        startActivity(intent);
                                                        Toast.makeText(CreateAccountActivity.this, "User Created Successfully", Toast.LENGTH_LONG).show();

                                                    } else {
                                                        progressDialog.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(CreateAccountActivity.this, "Error in creating new user", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            });
                                }
                        );
                    } else {
                        progressDialog.setVisibility(View.INVISIBLE);
                        Toast.makeText(CreateAccountActivity.this, "Error in creating user", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}