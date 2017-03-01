package com.example.caroline.popularmovies2.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the movie database.
 */

public class MovieContract {
    private MovieContract() {
    }

    // The "Content authority" is a name for the entire content provider
    public static final String CONTENT_AUTHORITY = "com.example.caroline.popularmovies2.app";

    // Using CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_REVIEW = "review";
    public static final String PATH_FAVORITE = "favorites";

    /* Inner class that defines the table contents of the MOVIE table */
    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";
        //columns
        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_TITLE = "original_title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RATING = "vote_average";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POPULAR = "popularity";
        public static final String COLUMN_FAVORITE = "favorite";


        public static Uri buildMovieWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIdFromUri(Uri uri) {
            return ContentUris.parseId(uri);
        }

    }


    /* Inner class that defines the table contents of the TRAILER table */

    public static final class TrailerEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        public static final String TABLE_NAME = "trailer";
        //columns
        public static final String COLUMN_TRAILER_ID = "id";
        public static final String COLUMN_TITLE = "name";
        public static final String COLUMN_YOUTUBE_KEY = "key";
        public static final String COLUMN_MOVIE_ID = "movie_id";


        public static long getIdFromUri(Uri uri) {

            return ContentUris.parseId(uri);
        }

        public static Uri buildTrailerWithId(long movieId) {
            return ContentUris.withAppendedId(CONTENT_URI, movieId);
        }

    }


    /* Inner class that defines the table contents of the REVIEW table */

    public static final class ReviewEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static final String TABLE_NAME = "reviews";
        //columns
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_REVIEW_ID = "id";
        public static final String COLUMN_MOVIE_ID = "movie_id";


        public static Uri buildReviewWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIdFromUri(Uri uri) {

            return ContentUris.parseId(uri);
        }

    }

    /* Inner class that defines the table contents of the FAVORITE table */

    public static final class FavoriteEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;

        public static final String TABLE_NAME = "favorites";
        //columns
        public static final String COLUMN_FAVORITE_MOVIE_ID = "movie_id";
        public static final String COLUMN_FAVORITE = "favorite";


        public static Uri buildMovieWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIdFromUri(Uri uri) {
            return ContentUris.parseId(uri);
        }

    }

}
