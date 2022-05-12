package edu.neu.madproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

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
    Map<String,Long> dummy = new HashMap<>();
    private androidx.appcompat.widget.SearchView searchView;
    /*@Override


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
    }*/

    public void backButton(View view) {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        searchView=findViewById(R.id.searchTitle);
        searchView.clearFocus();


        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterText(newText);
                return true;
            }
        });

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
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentWriteReview = new Intent(FeedActivity.this, WriteReviewActivity.class);
                startActivity(intentWriteReview);
            }
        });
        reviewList = new ArrayList<>();
        recyclerView = findViewById(R.id.feed_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedAdapter = new FeedAdapter(FeedActivity.this, reviewList);
        recyclerView.setAdapter(feedAdapter);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        //Intent intent = new Intent(FeedActivity.this, AccountActivity.class);
//                        Intent intent = new Intent(LoginActivity.this, CategoriesActivity.class);

        DatabaseReference reference = database.getReference().child("reviews");
        DatabaseReference reference1 = database.getReference().child("userHistory").child(Objects.requireNonNull(auth.getUid())).child("reads");


        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dummy = (Map<String, Long>) dataSnapshot.getValue();
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
                        } else {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Log.d(TAG, dataSnapshot.getValue().toString());
                                Reviews review = dataSnapshot.getValue(Reviews.class);
                                reviewList.add(review);
                            }
                        }
                        Helper.sortForBest(reviewList, dummy);

                        feedAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                feedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        } else {
            ContextCompat.startForegroundService(this, new Intent(this, GPSService.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && permissions.length == 2
                && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)
                && permissions[1].equals(Manifest.permission.ACCESS_COARSE_LOCATION)
                && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            ContextCompat.startForegroundService(this, new Intent(this, GPSService.class));
        }
    }

    private void filterText(String s) {
        List<Reviews> filterList=new ArrayList<>();
        for (Reviews reviews:reviewList){
            if(reviews.getTitle().toLowerCase().contains(s.toLowerCase())){
                filterList.add(reviews);
            }
        }
        if(filterList.isEmpty()){
            Snackbar.make(findViewById(R.id.FeedsLayout), "No data found",
                    Snackbar.LENGTH_SHORT)
                    .show();
        }else{
                feedAdapter.setFilteredList(filterList);
        }
    }

    // Handling Orientation Changes on Android
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(PREV_PAGE, this.prevPage);
        super.onSaveInstanceState(outState);
    }


}