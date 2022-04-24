package edu.neu.madproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyReviewsAdapter extends RecyclerView.Adapter<MyReviewsAdapter.ViewHolder>{

    private Context myReviewsActivity;
    private List<Reviews> reviewsList;
    FirebaseAuth auth;
    FirebaseDatabase database;

    public MyReviewsAdapter(Context myReviewsActivity, List<Reviews> reviewsList) {
        this.myReviewsActivity = myReviewsActivity;
        this.reviewsList = reviewsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(myReviewsActivity).inflate(R.layout.feed_card_row,parent,false);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        return new MyReviewsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reviews review = reviewsList.get(position);
        String url=review.getImageURL();
        Uri uri = Uri.parse(url);
        Picasso.get().load(uri).into(holder.reviewImage);
        holder.reviewTitle.setText(review.getTitle());
        holder.reviewRating.setRating(review.getRating());
        holder.reviewUser.setText(review.getUsername());
        holder.reviewContent.setText(review.getContent());
        holder.reviewCategory.setText(review.getCategory());
        holder.itemView.setOnClickListener(view -> {
            Intent intent  = new Intent(myReviewsActivity, ReadReviewActivity.class);
            intent.putExtra("image", url);
            intent.putExtra("title",review.getTitle());
            intent.putExtra("category",review.getCategory());
            intent.putExtra("rating",review.getRating());
            intent.putExtra("username",review.getUsername());
            intent.putExtra("content",review.getContent());
            myReviewsActivity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView reviewImage;
        TextView reviewTitle;
        RatingBar reviewRating;
        TextView reviewUser;
        TextView reviewContent;
        TextView reviewCategory;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            reviewImage=itemView.findViewById(R.id.review_image);
            reviewTitle=itemView.findViewById(R.id.review_title);
            reviewRating=itemView.findViewById(R.id.review_rating);
            reviewUser=itemView.findViewById(R.id.review_user);
            reviewContent=itemView.findViewById(R.id.review_content);
            reviewCategory=itemView.findViewById(R.id.review_category);
        }
    }
}
