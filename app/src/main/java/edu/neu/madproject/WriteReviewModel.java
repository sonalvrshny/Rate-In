package edu.neu.madproject;

public class WriteReviewModel {
    private String imageURL;
    private String title;
    private Float rating;
    private String category;
    private String content;

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

    public WriteReviewModel(){

    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;



    }

    public WriteReviewModel(String imageURL, String title, Float rating, String category,String content){
        this.imageURL = imageURL;
        this.title=title;
        this.rating=rating;
        this.category=category;
        this.content=content;

    }
}
