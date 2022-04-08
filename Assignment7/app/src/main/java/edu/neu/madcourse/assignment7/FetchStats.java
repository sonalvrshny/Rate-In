package edu.neu.madcourse.assignment7;

public class FetchStats {
    String image;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    Long count;
    public FetchStats(){

    }

    public FetchStats(String image,Long count){
        this.image=image;
        this.count=count;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
