package com.example.caroline.popularmovies2.app.models;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Review {
    @SerializedName("results")
    private List<MovieReview> reviewList;

    public List<MovieReview> getReviewList() {

        return reviewList;
    }
    public static class MovieReview {

        @SerializedName("author")
        private String author;

        @SerializedName("content")
        private String content;

        @SerializedName("id")
        private String id;


        public String getAuthor() {return author;}

        public String getContent() {return content;}

        public String getId() {return id;}

    }
}
