package edu.neu.madproject;

import android.os.Bundle;

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

        DatabaseReference reference = database.getReference().child("reviews");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Reviews review = dataSnapshot.getValue(Reviews.class);
                    assert review != null;
                    if(review.getUsername().equals(username)){
                        reviewList.add(review);
                    }
                }

                myReviewsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}