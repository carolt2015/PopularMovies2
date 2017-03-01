package com.example.caroline.popularmovies2.app.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;


public class MovieProvider extends ContentProvider {

    // The URI Matcher used by this content provider.

    private static final String LOG_TAG = MovieProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private SQLiteOpenHelper mOpenHelper;

    static final int MOVIES = 100;
    static final int MOVIES_WITH_ID = 101;
    static final int TRAILER = 200;
    static final int TRAILER_WITH_MOVIE_ID = 201;
    public static final int REVIEW = 300;
    public static final int REVIEW_WITH_MOVIE_ID = 301;
    public static final int FAVORITE = 400;
    public static final int FAVORITE_WITH_MOVIE_ID = 401;


    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
    // # for numbers, * for strings
        // Creating a corresponding code for each type of URI you want
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIES);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIES_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_TRAILER, TRAILER);
        matcher.addURI(authority, MovieContract.PATH_TRAILER + "/#", TRAILER_WITH_MOVIE_ID);
        matcher.addURI(authority, MovieContract.PATH_REVIEW, REVIEW);
        matcher.addURI(authority, MovieContract.PATH_REVIEW + "/#", REVIEW_WITH_MOVIE_ID);
        matcher.addURI(authority, MovieContract.PATH_FAVORITE, FAVORITE);
        matcher.addURI(authority, MovieContract.PATH_FAVORITE + "/#", FAVORITE_WITH_MOVIE_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;

            case MOVIES_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;

            case TRAILER:
                return MovieContract.TrailerEntry.CONTENT_TYPE;

            case TRAILER_WITH_MOVIE_ID:
               return MovieContract.TrailerEntry.CONTENT_ITEM_TYPE;

            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE;

            case REVIEW_WITH_MOVIE_ID:
                return MovieContract.ReviewEntry.CONTENT_ITEM_TYPE;

            case FAVORITE:
                return MovieContract.ReviewEntry.CONTENT_TYPE;

            case FAVORITE_WITH_MOVIE_ID:
                return MovieContract.ReviewEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[]
            selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIES: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case MOVIES_WITH_ID: {
                long _id = MovieContract.MovieEntry.getIdFromUri(uri);
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry._ID + " = ?",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder);

                break;
            }

            case TRAILER: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case TRAILER_WITH_MOVIE_ID: {
                long _id = MovieContract.TrailerEntry.getIdFromUri(uri);
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{Long.toString(_id)},
                        null,
                        null,
                        sortOrder);
                break;
            }
            case REVIEW: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            case REVIEW_WITH_MOVIE_ID: {
                long _id = MovieContract.ReviewEntry.getIdFromUri(uri);
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{Long.toString(_id)},
                        null,
                        null,
                        sortOrder);
                break;
            }

            case FAVORITE: {
                  cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case FAVORITE_WITH_MOVIE_ID: {
                long _id = MovieContract.FavoriteEntry.getIdFromUri(uri);
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        MovieContract.FavoriteEntry.COLUMN_FAVORITE_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder);
                break;
            }


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

     //  Insert a new record into the table pointed by the Uri
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case MOVIES:
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.MovieEntry.buildMovieWithId(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            case TRAILER:
                 _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.TrailerEntry.buildTrailerWithId(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            case REVIEW:
                _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.ReviewEntry.buildReviewWithId(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;

            case FAVORITE:
                _id = db.insert(MovieContract.FavoriteEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.FavoriteEntry.buildMovieWithId(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    //Deletes the records in the table pointed by the URI

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int rowsDeleted;

        //delete all rows and return the number of records deleted
        if (selection == null) {
            selection = "1";
        }

        switch (match) {

            case MOVIES:
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILER:
                rowsDeleted = db.delete(MovieContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEW:
                rowsDeleted = db.delete(MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITE:
                rowsDeleted = db.delete(MovieContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    //Updates a record on the table pointed by the URI.

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIES:
                rowsUpdated = db.update(
                        MovieContract.MovieEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case TRAILER:
                rowsUpdated = db.update(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case REVIEW:
                rowsUpdated = db.update(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case FAVORITE:
                rowsUpdated = db.update(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }



   // This method is used for bulk insertion into the database

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES: {
                db.beginTransaction();
                int count = 0;

                try {
                    for (ContentValues item : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, item);
                        if (_id != -1) {
                            count++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            }

            case TRAILER: {
                db.beginTransaction();
                int count = 0;

                for (ContentValues item : values) {
                    long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, item);
                    if (_id != -1) {
                        count++;
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();

                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            }

            case REVIEW: {
                db.beginTransaction();
                int count = 0;

                for (ContentValues item : values) {
                    long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, item);
                    if (_id != -1) {
                        count++;
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();

                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            }
            case FAVORITE: {
                db.beginTransaction();
                int count = 0;

                for (ContentValues item : values) {
                    long _id = db.insert(MovieContract.FavoriteEntry.TABLE_NAME, null, item);
                    if (_id != -1) {
                        count++;
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();

                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            }

            default:
                return super.bulkInsert(uri, values);

        }
    }
}