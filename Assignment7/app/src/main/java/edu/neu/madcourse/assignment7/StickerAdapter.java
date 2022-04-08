package edu.neu.madcourse.assignment7;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {

    private List<Pair<String,String>> mData;
    private LayoutInflater mInflater;
    String receiverID, receiverName, senderID;
    TextView receiverDisplayName;
    FirebaseDatabase database;
    FirebaseAuth auth;
    CardView sendSticker;
    String senderRoom, receiverRoom;
    RecyclerView messageHistory;
    List<Messages> messagesList;
    ChatAdapter chatAdapter;
    AppCompatActivity context;

    // data is passed into the constructor
    StickerAdapter(AppCompatActivity context, List<Pair<String,String>> data, String recID) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.receiverID = recID;
    }

    // inflates the cell layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_sticker_row, parent, false);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        messagesList = new ArrayList<>();
        //receiverID = getIntent().getStringExtra("uid");
        senderID = auth.getUid();
        senderRoom = senderID + receiverID;
        receiverRoom = receiverID + senderID;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String s = mData.get(position).second;
        String emojiName = mData.get(position).first;
        Uri uri = Uri.parse(s);
        Log.e("Getting URI", "" + uri);
        Picasso.get().load(uri).into(holder.mImage);
        holder.itemView.setOnClickListener(v -> {
            Date date = new Date();
            Messages message = new Messages(s, senderID, date.getTime());
            database = FirebaseDatabase.getInstance();
            DatabaseReference receiver = database.getReference().child("user").child(receiverID);
            DatabaseReference sender = database.getReference().child("user").child(senderID);
            database.getReference().child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .push()
                    .setValue(message).addOnCompleteListener(task -> database.getReference()
                    .child("chats")
                    .child(receiverRoom)
                    .child("messages")
                    .push()
                    .setValue(message).addOnCompleteListener(task1 -> {
                        receiver.child("notificationToken").get().addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                Object token = task2.getResult().getValue();
                                if (token != null) {
                                    sender.child("username").get()
                                            .addOnCompleteListener(task3 -> {
                                                if (task3.isSuccessful()) {
                                                    Object userObj = task3.getResult().getValue();
                                                    String user = userObj == null ? "Someone" : userObj.toString().split("@")[0];
                                                    Helper.sendMessageToDevice(context,
                                                            token.toString(),
                                                            "Someone sent you a sticker",
                                                            user + " sent you a sticker",
                                                            uri.toString());
                                                }
                                                StickerAdapter.this.context.finish();
                                            });
                                } else StickerAdapter.this.context.finish();
                            } else {
                                StickerAdapter.this.context.finish();
                                Toast.makeText(context, "Error in creating new user",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                        sender.get().addOnCompleteListener(getTask -> {
                            if (!getTask.isSuccessful()) {
                                Log.e("Stat Update failed", "There was an error while updating stats");
                                return;
                            }
                            DataSnapshot snapshot = getTask.getResult();
                            Map<String, Long> history = (Map<String, Long>) snapshot.child("history").getValue();
                            if(history == null) history = new HashMap<>();
                            history.put(emojiName, history.getOrDefault(emojiName, 0L) + 1);
                            Users users = new Users(senderID,
                                    snapshot.child("username").getValue().toString(),
                                    snapshot.child("status").getValue().toString(),
                                    snapshot.child("notificationToken").getValue().toString(),
                                    history);
                            sender.setValue(users).addOnCompleteListener(setTask -> {
                                if (!setTask.isSuccessful()) {
                                    Log.e("Stat Update failed", "There was an error while updating stats");
                                }
                            });
                        });
                    }));
        });


    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImage;

        ViewHolder(View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.sticker);

        }

    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id).second;
    }

}
