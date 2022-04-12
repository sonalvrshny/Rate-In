package edu.neu.madproject;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategoriesActivity extends AppCompatActivity {

    FirebaseDatabase database;
    List<Pair<String,String>> categoryList;
    CategoryAdapter categoriesAdapter;
    RecyclerView categoriesRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        categoryList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("categories");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String key = dataSnapshot.getKey();
                    String val = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                    categoryList.add(new Pair<>(key, val));
                }
                categoriesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        categoriesRecycler = findViewById(R.id.categories_recycler);
        categoriesRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        categoriesAdapter = new CategoryAdapter(this, categoryList);
        categoriesRecycler.setAdapter(categoriesAdapter);


    }
}