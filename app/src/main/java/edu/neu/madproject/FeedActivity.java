package edu.neu.madproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
    boolean backPressed = false;
    @Override
    public void onBackPressed() {
        if (backPressed){// || this.prevPage == null || this.prevPage.equals("category")) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.search_menu, menu);
//        MenuItem menuItem = menu.findItem(R.id.img_search);
//        SearchView searchView = (SearchView) menuItem.getActionView();

        SearchView searchView = findViewById(R.id.img_search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                feedAdapter.getFilter().filter(newText);
                Log.d(TAG, newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void openReview(View view){

}
}