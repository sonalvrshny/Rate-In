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

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Pair<String, String>> mData;
    private LayoutInflater mInflater;
    FirebaseDatabase database;
    AppCompatActivity context;

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
//        view.
        database = FirebaseDatabase.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String url = mData.get(position).second;
        String emojiName = mData.get(position).first;
        Uri uri = Uri.parse(url);
        Log.e("Getting URI", "" + uri);
        Picasso.get().load(uri).into(holder.categoryImage);
        holder.categoryLabel.setText(emojiName);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryAdapter.this.context, FeedActivity.class);
//            Intent intent = new Intent(SignupActivity.this, CategoriesActivity.class);
            intent.putExtra("category", emojiName);
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
