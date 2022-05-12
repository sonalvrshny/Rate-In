package edu.neu.madproject;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Pair<String, String>> mData;
    private LayoutInflater mInflater;
    FirebaseDatabase database;
    AppCompatActivity context;
    FirebaseAuth auth;

    // data is passed into the constructor
    CategoryAdapter(AppCompatActivity context, List<Pair<String, String>> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the cell layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.category_card, parent, false);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String url = mData.get(position).second;
        String catName = mData.get(position).first;
        Uri uri = Uri.parse(url);
        Log.e("Getting URI", "" + uri);
        Picasso.get().load(uri).into(holder.categoryImage);
        holder.categoryLabel.setText(catName);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryAdapter.this.context, FeedActivity.class);
//            Intent intent = new Intent(SignupActivity.this, CategoriesActivity.class);
            DatabaseReference sender = database.getReference().child("userHistory").child(auth.getUid());
            intent.putExtra("category", catName);
            intent.putExtra("prev", "category_ignore");
            sender.get().addOnCompleteListener(getTask -> {
                if (!getTask.isSuccessful()) {
                    Log.e("Stat Update failed", "There was an error while updating stats");
                    return;
                }
                DataSnapshot snapshot = getTask.getResult();
                Map<String, Long> history = (Map<String, Long>) snapshot.child("reads").getValue();
                Map<String, Long> wHistory = (Map<String, Long>) snapshot.child("writes").getValue();
                Map<String, Long> wTHistory = (Map<String, Long>) snapshot.child("tagWrites").getValue();
                Map<String, Long> tHistory = (Map<String, Long>) snapshot.child("tagReads").getValue();
                if(history == null) history = new HashMap<>();
                if(wHistory == null) wHistory = new HashMap<>();
                if(wTHistory == null) wTHistory = new HashMap<>();
                if(tHistory == null) tHistory = new HashMap<>();
                history.put(catName, history.getOrDefault(catName, 0L) + 1);
                UsersStats stats = new UsersStats(history, wHistory, tHistory, wTHistory);
                sender.setValue(stats).addOnCompleteListener(setTask -> {
                    if (!setTask.isSuccessful()) {
                        Log.e("Stat Update failed", "There was an error while updating stats");
                    }
                });
            });
            CategoryAdapter.this.context.startActivity(intent);
        });
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;
        TextView categoryLabel;

        ViewHolder(View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.category);
            categoryLabel = itemView.findViewById(R.id.category_name);
        }

    }

}
