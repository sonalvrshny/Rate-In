package edu.neu.madcourse.assignment7;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth auth;
    RecyclerView RecyclerView;
    UserAdapter userAdapter;
    FirebaseDatabase database;
    ArrayList<Users> usersList;
    ImageView logoutBtn, statsBtn;
    boolean backPressed = false;

    @Override
    public void onBackPressed() {
        if (backPressed) {
            logOut();
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Press back again to logout", Toast.LENGTH_LONG).show();
            backPressed = true;
            new Handler().postDelayed(() -> {
                backPressed = false;
            }, 1500);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        logoutBtn = findViewById(R.id.img_logout);
        auth = FirebaseAuth.getInstance();
        statsBtn=findViewById(R.id.img_stats);
        database = FirebaseDatabase.getInstance();
        usersList = new ArrayList<>();
        String removeUsername = getIntent().getStringExtra("user");
        DatabaseReference reference = database.getReference().child("user");
        reference.addValueEventListener(new ValueEventListener() {   

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    assert users != null;
                    if (!users.username.equals(removeUsername)) {
                        usersList.add(users);
                    }

                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        RecyclerView = findViewById(R.id.RecyclerView);
        RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(HomeActivity.this, usersList);
        RecyclerView.setAdapter(userAdapter);
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(HomeActivity.this, CreateAccountActivity.class));
        }
        logoutBtn.setOnClickListener(view -> {
//            Helper.sendMessageToDevice(this, "eogegtpYR7mUyyLF9ZLMff:APA91bFX3UBC_fE0q-Jh2QSZKAOaK_m9HhGM2FfTrSf3BBNFmxTqLwdUVJGUILM0dFCGjr2rVVsONwm7ZWSTnfAWlUXuMAR5csT-UKVMPWt_795HJw6xrb2erfsYvQ3XwNgfHMjLVsPa",
//                    "Hi", "HIQQQQ", "https://firebasestorage.googleapis.com/v0/b/assignment7-10bc7.appspot.com/o/stickers%2Fhappy.png?alt=media&token=f4ff9319-3d3f-483f-bae0-a287139cfbf9");
            logOut();
        });
        Log.d("debug stats","clicked");


    }


    public void onClick(View view)
    {
        Intent intent=new Intent(HomeActivity.this, StatsActivity.class);
        startActivity(intent);
    }
    private void logOut() {
        DatabaseReference reference = database.getReference().child("user");
        DatabaseReference user = reference.child(auth.getUid());
        user.child("notificationToken").setValue(null);
        auth.signOut();
        this.finish();
    }
}
