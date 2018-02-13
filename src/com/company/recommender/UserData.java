package com.company.recommender;

public class UserData {


    private int userId;
    private int itemId;
    private int rating;
    private int timeStamp;


    public UserData(int userId, int itemId, int rating, int timeStamp) {
        this.userId = userId;
        this.itemId = itemId;
        this.rating = rating;
        this.timeStamp = timeStamp;
    }


    public int getUserId() {
        return userId;
    }


    public int getItemId() {
        return itemId;
    }


    public int getRating() {
        return rating;
    }


    public int getTimeStamp() {
        return timeStamp;
    }

}
