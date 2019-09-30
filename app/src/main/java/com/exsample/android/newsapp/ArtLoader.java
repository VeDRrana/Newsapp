package com.exsample.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Loads a list of earthquakes by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class ArtLoader extends AsyncTaskLoader<List<Art>> {

    // Tag for log messages
    private static final String LOG_TAG = ArtLoader.class.getName();

    // Query URL
    private String mUrl;

    /**
     * Constructs a new {@link ArtLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public ArtLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    // This is on a background thread.
    @Override
    public List<Art> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<Art> art = QueryUtils.fetchArtData(mUrl);
        return art;
    }
}

