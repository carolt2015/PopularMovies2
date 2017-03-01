package com.example.caroline.popularmovies2.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for movie data.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    static final int DATABASE_VERSION = 3;
    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE "
                + MovieContract.MovieEntry.TABLE_NAME
                + "(" + MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY,"
                + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL,"
                + MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL,"
                + MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL,"
                + MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL,"
                + MovieContract.MovieEntry.COLUMN_RATING + " REAL NOT NULL,"
                + MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL,"
                + MovieContract.MovieEntry.COLUMN_POPULAR + " REAL NOT NULL,"
                + MovieContract.MovieEntry.COLUMN_FAVORITE + " INTEGER DEFAULT 0,"
                + " UNIQUE ("
                + MovieContract.MovieEntry.COLUMN_TITLE + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE "
                + MovieContract.TrailerEntry.TABLE_NAME
                + "(" + MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY,"
                + MovieContract.TrailerEntry.COLUMN_TRAILER_ID + " TEXT NOT NULL,"
                + MovieContract.TrailerEntry.COLUMN_TITLE + " TEXT NOT NULL,"
                + MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY + " TEXT NOT NULL,"
                + MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL,"
                + "UNIQUE (" + MovieContract.TrailerEntry.COLUMN_TRAILER_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE "
                + MovieContract.ReviewEntry.TABLE_NAME
                + "(" + MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY,"
                + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL,"
                + MovieContract.ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL,"
                + MovieContract.ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL,"
                + MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL,"
                + " UNIQUE (" + MovieContract.ReviewEntry.COLUMN_REVIEW_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE "
                + MovieContract.FavoriteEntry.TABLE_NAME
                + "(" + MovieContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY,"
                + MovieContract.FavoriteEntry.COLUMN_FAVORITE + " INTEGER,"
                + MovieContract.FavoriteEntry.COLUMN_FAVORITE_MOVIE_ID + " INTEGER,"
                + "UNIQUE (" + MovieContract.FavoriteEntry.COLUMN_FAVORITE_MOVIE_ID + ") ON CONFLICT REPLACE);";


        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_TRAILER_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoriteEntry.TABLE_NAME);
        onCreate(db);
    }
}
