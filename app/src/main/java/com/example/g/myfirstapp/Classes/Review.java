package com.example.g.myfirstapp.Classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

/**
 * Created by Sneider on 20/04/2018.
 */

public class Review implements Parcelable{

    private float rating;
    private String username;
    private String userId;
    private String comment;
    private String userPhotoURL;
    private String placeName;
    private String tags;
    private String dishPhotoURL;
    private String dishName;
    private String placeAddress;
    private String placeId;

    //D/>>>>>>>>>>>documento: Al62ZaqV6soVlEGFPmXb => {
    // rating=5.0,
    // username=cami,
    // userId=8YTNCOlqxcRtHGctEeJ8UxftorH3,
    // comment=buena,
    // userPhotoURL=null,
    // placeName=Cigarrería Las Américas,
    // tags={Taste=true},
    // dishPhotoURL=https://firebasestorage.googleapis.com/v0/b/areview-30ddd.appspot.com/o/images%2Fdishes%2F8YTNCOlqxcRtHGctEeJ8UxftorH3%2Fandroid.graphics.Bitmap%4050efc0f?alt=media&token=c58f291f-e18b-40fd-90c6-903f7f5fc526,
    // dishName=carne,
    // placeAddress=Calle 69d, Bogotá, Colombia,
    // placeId=ChIJi2lHua6eP44Rsk6AxQ1--co}

    public Review(float rating, String username, String userId, String comment, String userPhotoURL, String placeName, String tags, String dishPhotoURL, String dishName, String placeAddress, String placeId) {
        this.rating = rating;
        this.username = username;
        this.userId = userId;
        this.comment = comment;
        this.userPhotoURL = userPhotoURL;
        this.placeName = placeName;
        this.tags = tags;
        this.dishPhotoURL = dishPhotoURL;
        this.dishName = dishName;
        this.placeAddress = placeAddress;
        this.placeId = placeId;
    }

    public Review(Map review)
    {   
        String reviewValue=review.get("rating").toString();
        rating = Float.parseFloat(reviewValue);
        username = (String) review.get("username");
        userId = (String) review.get("userId");
        comment = (String) review.get("comment");
        userPhotoURL = "aun no hay";
        placeName = (String) review.get("placeName");
        tags = review.get("tags").toString();
        dishPhotoURL = (String) review.get("dishPhotoURL");
        dishName = (String) review.get("dishName");
        placeAddress = (String) review.get("placeAddress");
        placeId = (String) review.get("placeId");

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserPhotoURL() {
        return userPhotoURL;
    }

    public void setUserPhotoURL(String userPhotoURL) {
        this.userPhotoURL = userPhotoURL;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getDishPhotoURL() {
        return dishPhotoURL;
    }

    public void setDishPhotoURL(String dishPhotoURL) {
        this.dishPhotoURL = dishPhotoURL;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    protected Review(Parcel in) {
        rating = in.readFloat();
        username = in.readString();
        userId = in.readString();
        comment = in.readString();
        userPhotoURL = in.readString();
        placeName = in.readString();
        tags = in.readString();
        dishPhotoURL = in.readString();
        dishName = in.readString();
        placeAddress = in.readString();
        placeId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(rating);
        parcel.writeString(username);
        parcel.writeString(userId);
        parcel.writeString(comment);
        parcel.writeString(userPhotoURL);
        parcel.writeString(placeName);
        parcel.writeString(tags);
        parcel.writeString(dishPhotoURL);
        parcel.writeString(dishName);
        parcel.writeString(placeAddress);
        parcel.writeString(placeId);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
