package edu.neu.madproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MyReviewsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MyReviewsAdapter myReviewsAdapter;
    List<Reviews> reviewList;
    String username;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reviews);

        reviewList = new ArrayList<>();

        username = getIntent().getStringExtra("username");
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        recyclerView = findViewById(R.id.myReviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myReviewsAdapter = new MyReviewsAdapter(MyReviewsActivity.this, reviewList);
        recyclerView.setAdapter(myReviewsAdapter);




    }
}