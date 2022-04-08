package edu.neu.madcourse.assignment7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StatsActivity extends AppCompatActivity {
RecyclerView statsRecycler;
FirebaseDatabase database;
    List<Pair<String,Long>> stickerList;
StatsViewAdapter statsViewAdapter;
HashMap<String,String> stickersUrl;
    String senderID;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        auth = FirebaseAuth.getInstance();
        senderID = auth.getUid();
        database = FirebaseDatabase.getInstance();
        Log.d("debug stats","clicked called");
        stickerList = new ArrayList<>();
        stickersUrl=new HashMap<>();
        DatabaseReference stickers=database.getReference().child("stickers");
        stickers.get().addOnCompleteListener(task1->{
            for (DataSnapshot dataSnapshot1 : task1.getResult().getChildren()) {
                String key = dataSnapshot1.getKey().toString();
                String val = dataSnapshot1.getValue().toString();
                stickersUrl.put(key,val);

            }

        DatabaseReference reference = database.getReference().child("user").child(senderID).child("history");
        reference.get().addOnCompleteListener(task -> {
            for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                String key = dataSnapshot.getKey().toString();
                Long val = (Long) dataSnapshot.getValue();
                stickerList.add(new Pair<>(key, val));
            }
                  statsViewAdapter.notifyDataSetChanged();
        });
        });
        statsRecycler=findViewById(R.id.recyler_view);
        statsRecycler.setHasFixedSize(true);
        statsRecycler.setLayoutManager(new LinearLayoutManager(this));
        statsViewAdapter = new StatsViewAdapter(this, stickerList,stickersUrl);
        statsRecycler.setAdapter(statsViewAdapter);
    }


}