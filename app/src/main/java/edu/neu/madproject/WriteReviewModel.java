package edu.neu.madproject;

public class WriteReviewModel {
    private String imageURL;
    private String title;
    private Float rating;
    private String category;
    private String content;
    private String username;
    private String uid;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public WriteReviewModel(){

    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;



    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public WriteReviewModel(String imageURL, String title, Float rating, String category, String content, String username, String uid){
        this.imageURL = imageURL;
        this.title=title;
        this.rating=rating;
        this.category=category;
        this.content=content;
        this.username=username;
        this.uid=uid;

    }
}
