package com.example.khale.popularmovies_stage2;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieTrailer {

    private String id;
    private String key;

    public MovieTrailer(JSONObject trailer) throws JSONException{
        this.id = trailer.getString("id");
        this.key = trailer.getString("key");
    }


    //Getters
    public String getId(){
        return id;
    }

    public String getKey(){
        return key;
    }
}
