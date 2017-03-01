package com.example.caroline.popularmovies2.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Trailer {

   @SerializedName("results")
   private List<MovieTrailer> trailerList;

    public List<MovieTrailer> getTrailerList() {
        return trailerList;
    }
    public static class MovieTrailer {

        @SerializedName("id")
        private String mTrailerId;

        @SerializedName("name")
        private String mTrailerTitle;

        @SerializedName("key")
        private String mKey;


        public String getId() {return mTrailerId;}

        public String getTrailerTitle() {return mTrailerTitle;}

        public String getKey() {return mKey;}

    }
}

