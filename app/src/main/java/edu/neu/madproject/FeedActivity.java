package edu.neu.madproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {
    String TAG = "FeedActivityDebug";
    FirebaseAuth auth;
    FirebaseDatabase database;
    RecyclerView recyclerView;
    FeedAdapter feedAdapter;
    List<Reviews> reviewList;
    FloatingActionButton floatingActionButton;
    Button writeReview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        floatingActionButton=(FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentWriteReview = new Intent(FeedActivity.this, WriteReviewActivity.class);
                startActivity(intentWriteReview);
            }
        });
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reviewList = new ArrayList<>();

        DatabaseReference reference = database.getReference().child("reviews");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.d(TAG, dataSnapshot.getValue().toString());
                    Reviews review = dataSnapshot.getValue(Reviews.class);
                    reviewList.add(review);
                }
                feedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView = findViewById(R.id.feed_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedAdapter = new FeedAdapter(FeedActivity.this, reviewList);
        recyclerView.setAdapter(feedAdapter);

    }




public void openReview(View view){

}
}