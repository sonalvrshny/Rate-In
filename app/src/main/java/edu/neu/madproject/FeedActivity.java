package edu.neu.madproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class FeedActivity extends AppCompatActivity {
    private static final String PREV_PAGE = "PREV_PAGE";
    String TAG = "FeedActivityDebug";
    FirebaseAuth auth;
    FirebaseDatabase database;
    RecyclerView recyclerView;
    FeedAdapter feedAdapter;
    List<Reviews> reviewList;
    FloatingActionButton floatingActionButton;
    Button writeReview;
    boolean backPressed = false;
    String prevPageCat = null;
    String prevPage = null;
    @Override
    public void onBackPressed() {
        if (backPressed || (this.prevPage != null && this.prevPage.equals("category_ignore"))) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Press back again to logout", Toast.LENGTH_LONG).show();
            backPressed = true;
            new Handler().postDelayed(() -> {
                backPressed = false;
            }, 1500);
        }
    }

    public void backButton(View view) {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putInt("some_int", 0);

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, NavFragment.class, bundle)
                    .commit();
        }

        this.prevPageCat = getIntent().getStringExtra("category");
        this.prevPage = getIntent().getStringExtra("prev");
        if (savedInstanceState != null && savedInstanceState.containsKey(PREV_PAGE)
                && (this.prevPage == null || this.prevPage.equals(""))) {
            this.prevPage = savedInstanceState.getString(PREV_PAGE);
        }
        Log.d(TAG, "category clicked on " + prevPage);
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
        //Intent intent = new Intent(FeedActivity.this, AccountActivity.class);
//                        Intent intent = new Intent(LoginActivity.this, CategoriesActivity.class);

        DatabaseReference reference = database.getReference().child("reviews");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewList.clear();
                if (prevPageCat != null) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Reviews review = dataSnapshot.getValue(Reviews.class);
                        if (prevPageCat.equalsIgnoreCase(review.getCategory())) {
                            reviewList.add(review);
                        }
                    }
                }
                else {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Log.d(TAG, dataSnapshot.getValue().toString());
                        Reviews review = dataSnapshot.getValue(Reviews.class);
                        reviewList.add(review);
                    }
                }


                Collections.sort(reviewList, (reviews, t1) -> {
                    String category1 = reviews.getCategory();
                    String category2 = t1.getCategory();
                    final long[] catCount1 = {0};
                    final long[] catCount2 = {0};
                    DatabaseReference reference1 = database.getReference().child("userHistory").child(Objects.requireNonNull(auth.getUid())).child("reads");
                    reference1.get().addOnCompleteListener(task -> {
                       if(!task.isSuccessful()){
                           Log.e("Sorting Failed","Fetching feed category count to sort feed failed");
                       }
                       DataSnapshot dataSnapshot = task.getResult();

                       if(dataSnapshot.hasChild(category1)) {
                           catCount1[0] = (long) dataSnapshot.child(category1).getValue();
                           System.out.println(catCount1[0]);
                       }
                       if(dataSnapshot.hasChild((category2))) {
                           catCount2[0] = (long) dataSnapshot.child(category2).getValue();
                           System.out.println(catCount2[0]);
                       }
                    });

                    if(catCount1[0]>catCount2[0]) {

                        return 1;
                    }
                    
                    return 0;
                });

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

    // Handling Orientation Changes on Android
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(PREV_PAGE, this.prevPage);
        super.onSaveInstanceState(outState);
    }






public void openReview(View view){

}
}