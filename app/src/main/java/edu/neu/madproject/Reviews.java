package edu.neu.madproject;

import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class Reviews {
    private String imageURL;
    private String title;
    private float rating;
    private String username;
    private String content;

    public Reviews() {
    }

    public Reviews(String content, String imageURL, float rating, String title, String username) {
        this.imageURL = imageURL;
        this.title = title;
        this.rating = rating;
        this.username = username;
        this.content = content;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
