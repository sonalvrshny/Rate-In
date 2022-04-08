package edu.neu.madcourse.assignment7;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class StickerActivity extends AppCompatActivity {

    FirebaseDatabase database;
    FirebaseAuth auth;
    List<Pair<String,String>> stickerList;
    StickerAdapter stickerAdapter;
    RecyclerView stickerRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker);
        String recID = getIntent().getStringExtra("recID");

        stickerList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("stickers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.e("Snapshot", "" + dataSnapshot);
                    String key = dataSnapshot.getKey().toString();
                    String val = dataSnapshot.getValue().toString();
                    stickerList.add(new Pair<>(key, val));
                }
                stickerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        stickerRecycler = findViewById(R.id.sticker_recycler);
        stickerRecycler.setLayoutManager(new GridLayoutManager(this, 3));
        stickerAdapter = new StickerAdapter(this, stickerList, recID);
        stickerRecycler.setAdapter(stickerAdapter);


    }
}