package com.exsample.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

//Helper methods related to requesting and receiving art news data from USGS.
public final class QueryUtils {

    // Tag for the log messages
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;
    private static final String REQUEST_METHOD = "GET";

    //create static String constants to store key values
    private static String RESPONSE = "response";
    private static String RESULTS = "results";
    private static String SECTION_NAME = "sectionName";
    private static String WEB_TITLE = "webTitle";
    private static String WEB_DATE = "webPublicationDate";
    private static String URL = "webUrl";
    private static String TAGS = "tags";

    /**
     * This class hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils
     */
    private QueryUtils() {
    }

    // Query the USGS data set and return a list of {@link Art} objects.

    public static List<Art> fetchArtData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, String.valueOf(R.string.problem_HTTP_request), e);
        }

        // Extract relevant fields from the JSON response and create a list of Art-s
        List<Art> art = extractFeatureFromJson(jsonResponse);

        // Return the list of Art-s
        return art;
    }

    // Returns new URL object from the given string URL.
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, String.valueOf(R.string.problem_building_URL), e);
        }
        return url;
    }

    // Make an HTTP request to the given URL and return a String as the response.
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        //  URL is null --> return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setRequestMethod(REQUEST_METHOD);
            urlConnection.connect();

            // Successful request --> read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, String.valueOf(R.string.error_response) + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, String.valueOf(R.string.problem_retrieving_JSON), e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Art} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Art> extractFeatureFromJson(String artJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(artJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding art news to
        List<Art> arts = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(artJSON);

            // Extract the JSONObject associated with the key called "response"
            JSONObject art = baseJsonResponse.getJSONObject(RESPONSE);

            // Extract the JSONArray associated with the key called "results",
            JSONArray artArray = art.getJSONArray(RESULTS);

            // For each art news in the artArray, create an {@link Art} object
            for (int i = 0; i < artArray.length(); i++) {

                // Get a single art news at position i within the list
                JSONObject currentNews = artArray.getJSONObject(i);

                // Extract the value for the key called "sectionName"
                String category = currentNews.getString(SECTION_NAME);
                String title = currentNews.getString(WEB_TITLE);
                String realiseDate = currentNews.getString(WEB_DATE);
                String url = currentNews.getString(URL);

                // Extract the JSONArray associated with the key called "tags",
                JSONArray artTagsArray = currentNews.getJSONArray(TAGS);

                //String variable which hold author name and check if author name existed
                String author = null;
                if (artTagsArray.length() == 1) {
                    // Get first element in artTagsArray
                    JSONObject contributorTag = (JSONObject) artTagsArray.get(0);
                    // Get author name
                    author = contributorTag.getString(WEB_TITLE);
                }

                // Create a new {@link Art} object with the magnitude, location, time,
                // and url from the JSON response.
                Art artNews = new Art(category, title, realiseDate, url, author);

                // Add the new {@link Art} to the list of art news.
                arts.add(artNews);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, String.valueOf(R.string.problem_parsing_JSON), e);
        }

        // Return the list of art news
        return arts;
    }
}