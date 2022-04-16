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
    private String category;

    public Reviews() {
    }

    public Reviews(String content, String imageURL, float rating, String title, String username, String category) {
        this.imageURL = imageURL;
        this.title = title;
        this.rating = rating;
        this.username = username;
        this.content = content;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
