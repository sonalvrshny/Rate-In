package edu.neu.madproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> implements Filterable {
    private Context feedActivity;
    private List<Reviews> reviewsList;
    private List<Reviews> reviewsListAll;

    public FeedAdapter(FeedActivity feedActivity, List<Reviews> reviewsList) {
        this.feedActivity = feedActivity;
        this.reviewsList = reviewsList;
        this.reviewsListAll = new ArrayList<>(reviewsList);
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
        String url=review.getImageURL();
        Uri uri = Uri.parse(url);
        Picasso.get().load(uri).into(holder.reviewImage);
        holder.reviewTitle.setText(review.getTitle());
        holder.reviewRating.setRating(review.getRating());
        holder.reviewUser.setText(review.getUsername());
        holder.reviewContent.setText(review.getContent());
        holder.reviewCategory.setText(review.getCategory());

        holder.itemView.setOnClickListener(view -> {
            Intent intent  = new Intent(feedActivity, ReadReviewActivity.class);
            intent.putExtra("image", url);
            intent.putExtra("title",review.getTitle());
            intent.putExtra("category",review.getCategory());
            intent.putExtra("rating",review.getRating());
            intent.putExtra("username",review.getUsername());
            intent.putExtra("content",review.getContent());
            feedActivity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Reviews> filteredList = new ArrayList<>();
            if (charSequence.toString().isEmpty()) {
                filteredList.addAll(reviewsListAll);
            } else {
                for (Reviews review : reviewsList) {
                    String title = review.getTitle();
                    if (title.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredList.add(review);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            reviewsList.clear();
            reviewsList.addAll((Collection<? extends Reviews>) filterResults.values);
            notifyDataSetChanged();
        }
    };


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
