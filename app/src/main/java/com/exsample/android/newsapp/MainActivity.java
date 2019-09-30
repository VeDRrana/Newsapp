package com.exsample.android.newsapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<Art>> {

    // Tag for the log messages
    public static final String LOG_TAG = MainActivity.class.getName();

    //URL for art news data from the USGS dataset
    private static final String USGS_REQUEST_URL =
            "http://content.guardianapis.com/search";

    /**
     * Constant value for the art loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int LOADER_ID = 1;

    //Adapter for the list of arts news
    private ArtAdapter mAdapter;

    // TextView that is displayed when the list is empty
    private TextView mEmptyTextView;
    private ListView artListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content of the activity to use the activity_main.xml layout file
        setContentView(R.layout.activity_main);

        // Find a reference to the ListView in the layout
        artListView = findViewById(R.id.list_view);

        //Set empty view
        mEmptyTextView = findViewById(R.id.empty_view);
        artListView.setEmptyView(mEmptyTextView);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // Fetch data if there is a network connection
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter
            loaderManager.initLoader(LOADER_ID, null, this);
        } else {
            // display error and hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.progress_bar);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with error message
            mEmptyTextView.setText(R.string.no_connection);
        }
    }

    private void updateUI(List<Art> arts) {
        // Create a new {@link ArrayAdapter} of arts news
        mAdapter = new ArtAdapter(this, arts);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        if (artListView != null) {
            artListView.setAdapter(mAdapter);
            artListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    // Find the current art news that was clicked on
                    Art currentArt = mAdapter.getItem(position);

                    // Convert the String URL into a URI object (to pass into the Intent constructor)
                    Uri artUri = Uri.parse(currentArt.getUrl());

                    // Create a new intent to view the movie URI
                    Intent urlIntent = new Intent(Intent.ACTION_VIEW, artUri);

                    // Send the intent to launch a new activity
                    startActivity(urlIntent);
                }
            });
        }
    }

    //https://content.guardianapis.com/search?order-by=newest&show-tags=contributor&page-size=5&q=art&api-key=c73a9c7a-7a04-4e44-bc2f-118e5080f5e2
    @Override
    public Loader<List<Art>> onCreateLoader(int i, Bundle bundle) {

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minArtNews = sharedPrefs.getString(
                getString(R.string.settings_min_art_news_key),
                getString(R.string.settings_min_art_news_default));
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value.
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("page-size", minArtNews);
        uriBuilder.appendQueryParameter("q", "art");
        uriBuilder.appendQueryParameter("api-key", "c73a9c7a-7a04-4e44-bc2f-118e5080f5e2");

        // Return the completed uri
        return new ArtLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Art>> loader, List<Art> art) {
        // Hide loading indicator
        View loadingIndicator = findViewById(R.id.progress_bar);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display string no_art_news
        mEmptyTextView.setText(R.string.no_art_news);

        // If there is a valid list of Art-s, then add them to the adapter's data set.
        if (art != null && !art.isEmpty())
            updateUI(art);

    }

    @Override
    public void onLoaderReset(Loader<List<Art>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    // This method initialize the contents of the Activity's options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    // This method is called whenever an item in the options menu is selected.
    public boolean onOptionsItemSelected(MenuItem item) {
        // Returns the unique ID for the menu item
        int id = item.getItemId();
        // Open the SettingsActivity via an intent
        if (id == R.id.settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
