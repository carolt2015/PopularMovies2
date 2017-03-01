package com.example.caroline.popularmovies2.app;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.caroline.popularmovies2.app.data.MovieContract;
import com.example.caroline.popularmovies2.app.data.MovieDbHelper;
import com.example.caroline.popularmovies2.app.data.MovieProvider;


public class TestMovieProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestMovieProvider.class.getSimpleName();

    public void deleteAllRecordsFromDB() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);

        db.close();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();
        // We define the component name based on the package name from the context and the
        // MovieProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: MovieProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: MovieProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType() {
        // content://com.example.android.sunshine.app/Movie/
        String type = mContext.getContentResolver().getType(MovieContract.MovieEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/Movie
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, type);



    }

    public void testBasicMovieQueries() {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createMovieValues();
        long MovieRowId = TestUtilities.insertMovieValues(mContext);

        // Test the basic content provider query
        Cursor MovieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicMovieQueries, Movie query", MovieCursor, testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Movie Query did not properly set NotificationUri",
                    MovieCursor.getNotificationUri(), MovieContract.MovieEntry.CONTENT_URI);
        }
    }


    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createMovieValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, tco);
        Uri MovieUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, testValues);

        // Did our content observer get called?  Students:  If this fails, your insert Movie
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long MovieRowId = ContentUris.parseId(MovieUri);

        // Verify we got a row back.
        assertTrue(MovieRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating MovieEntry.",
                cursor, testValues);

        // Fantastic.  Now that we have a Movie, add some Movie!
        ContentValues MovieValues = TestUtilities.createMovieValues();
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, tco);

        Uri MovieInsertUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, MovieValues);
        assertTrue(MovieInsertUri != null);

        // Did our content observer get called?  Students:  If this fails, your insert Movie
        // in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor MovieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating MovieEntry insert.",
                MovieCursor, MovieValues);

        // Add the Movie values in with the Movie data so that we can make
        // sure that the join worked and we actually get all the values back
        MovieValues.putAll(testValues);


    }

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Movie table during delete", 0, cursor.getCount());
        cursor.close();


    }

    /*
        Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
        you have implemented delete functionality there.
     */
    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our Movie delete.
        TestUtilities.TestContentObserver MovieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, MovieObserver);

        deleteAllRecordsFromProvider();

        // Students: If either of these fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        MovieObserver.waitForNotificationOrFail();
        MovieObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(MovieObserver);
        mContext.getContentResolver().unregisterContentObserver(MovieObserver);
    }

    public void testUpdateMovie() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createMovieValues();

        Uri MovieUri = mContext.getContentResolver().
                insert(MovieContract.MovieEntry.CONTENT_URI, values);
        long MovieRowId = ContentUris.parseId(MovieUri);

        // Verify we got a row back.
        assertTrue(MovieRowId != -1);
        Log.d(LOG_TAG, "New row id: " + MovieRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(MovieContract.MovieEntry._ID, MovieRowId);
        updatedValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Fight Club");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor MovieCursor = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        MovieCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                MovieContract.MovieEntry.CONTENT_URI, updatedValues, MovieContract.MovieEntry._ID + "= ?",
                new String[]{Long.toString(MovieRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // Students: If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        MovieCursor.unregisterContentObserver(tco);
        MovieCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,   // projection
                MovieContract.MovieEntry._ID + " = " + MovieRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateMovie.  Error validating Movie entry update.",
                cursor, updatedValues);

        cursor.close();
    }


    static private final int BULK_INSERT_RECORDS_TO_INSERT = 20;

    static ContentValues[] createBulkInsertMovieValues() {

        ContentValues[] returnMovieValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];


        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues movieValues = new ContentValues();
            final String POSTER_PATH = "/adw6Lq9FiC9zjYEpOqfq03ituwp.jpg";
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, 15);
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Fight Club");
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, POSTER_PATH);
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "A ticking-time-bomb insomniac and a slippery soap salesman channel primal male aggression into a shocking new form of therapy. Their concept catches on, with underground fight clubs forming in every town, until an eccentric gets in the way and ignites an out-of-control spiral toward oblivion.");
            movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, 8.1);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "1999-10-14");
            movieValues.put(MovieContract.MovieEntry.COLUMN_POPULAR, 8.5);

            returnMovieValues[i] = movieValues;

        }
        return returnMovieValues;
    }
    public void testBulkInsert() {
        ContentValues[] bulkInsertContentValues = createBulkInsertMovieValues();

        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, movieObserver);



         int insertCount = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, bulkInsertContentValues);

        movieObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(movieObserver);
        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

         Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null
         );

        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);
        cursor.moveToFirst();

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating MovieEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }

        cursor.close();
    }

}


