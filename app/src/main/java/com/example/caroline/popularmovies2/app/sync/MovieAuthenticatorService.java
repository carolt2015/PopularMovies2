package com.example.caroline.popularmovies2.app.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * The service which allows the sync adapter framework to access the authenticator.
 */

public class MovieAuthenticatorService extends Service {
    private MovieAuthenticator movieAuthenticator;

    @Override
    public void onCreate() {
        movieAuthenticator = new MovieAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return movieAuthenticator.getIBinder();
    }
}
