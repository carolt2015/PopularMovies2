package com.example.caroline.popularmovies2.app.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class MovieSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static MovieSyncAdapter movieSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (movieSyncAdapter == null) {
                movieSyncAdapter = new MovieSyncAdapter(getApplicationContext(), true);
            }
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return movieSyncAdapter.getSyncAdapterBinder();
    }
}
