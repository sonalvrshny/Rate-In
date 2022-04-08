package edu.neu.madcourse.assignment7;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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

public class ChatActivity extends AppCompatActivity {

    String receiverID, receiverName, senderID;
    TextView receiverDisplayName;
    FirebaseDatabase database;
    FirebaseAuth auth;
    CardView sendSticker;
    String senderRoom, receiverRoom;
    RecyclerView messageHistory;
    List<Messages> messagesList;
    ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        messagesList = new ArrayList<>();
        receiverID = getIntent().getStringExtra("uid");
        senderID = auth.getUid();
        senderRoom = senderID + receiverID;
        receiverRoom = receiverID + senderID;
        receiverName = getIntent().getStringExtra("name");
        receiverDisplayName = findViewById(R.id.receiver_name);
        receiverDisplayName.setText(receiverName.split("@")[0]);

        sendSticker = findViewById(R.id.enter_sticker_button);
        messageHistory = findViewById(R.id.messageHistory_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageHistory.setLayoutManager(linearLayoutManager);
        chatAdapter = new ChatAdapter(ChatActivity.this, messagesList);
        messageHistory.setAdapter(chatAdapter);

        DatabaseReference ref = database.getReference().child("user").child(senderID);
        DatabaseReference chatRef = database.getReference().child("chats").child(senderRoom).child("messages");
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Messages messages = snapshot1.getValue(Messages.class);
                    messagesList.add(messages);
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendSticker.setOnClickListener(v -> {
            // should open list of stickers that can be sent
            // clicking on any of the stickers sends it to receiver
            Intent intent = new Intent(ChatActivity.this, StickerActivity.class);
            intent.putExtra("recID", receiverID);
            startActivity(intent);


        });

    }
}