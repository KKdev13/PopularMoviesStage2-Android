package com.example.khale.popularmovies_stage2;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieReview {

    private String id;
    private String author;
    private String content;

    public MovieReview(JSONObject review) throws JSONException{
        this.id = review.getString("id");
        this.author = review.getString("author");
        this.content = review.getString("content");
    }

    //Getters
    public String getId() {
        return id;
    }
    public String getAuthor(){
        return author;
    }
    public String getContent(){
        return content;
    }


}
