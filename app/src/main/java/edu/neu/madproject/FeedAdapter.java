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

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
    private Context feedActivity;
    private List<Reviews> reviewsList;

    public FeedAdapter(FeedActivity feedActivity, List<Reviews> reviewsList) {
        this.feedActivity = feedActivity;
        this.reviewsList = reviewsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(feedActivity).inflate(R.layout.feed_card_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reviews review = reviewsList.get(position);
        holder.reviewImage.setImageURI(Uri.parse(review.getImageURL()));
        holder.reviewTitle.setText(review.getTitle());
        holder.reviewRating.setRating(review.getRating());
        holder.reviewUser.setText(review.getUsername());
        holder.reviewContent.setText(review.getContent());

        holder.itemView.setOnClickListener(view -> {
            Intent intent  = new Intent(feedActivity, WriteReviewActivity.class);
            feedActivity.startActivity(intent);
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
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            reviewImage=itemView.findViewById(R.id.review_image);
            reviewTitle=itemView.findViewById(R.id.review_title);
            reviewRating=itemView.findViewById(R.id.review_rating);
            reviewUser=itemView.findViewById(R.id.review_user);
            reviewContent=itemView.findViewById(R.id.review_content);
        }
    }
}
