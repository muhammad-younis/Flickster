package me.myounis.flickster;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import me.myounis.flickster.models.Movie;

public class MovieListActivity extends AppCompatActivity {


    // constants
    // base URL for the API, used whenever we want to grab data
    public final static String BASE_API_URL = "https://api.themoviedb.org/3";
    // name for the parameter key
    public final static String API_KEY_PARAM = "api_key";

    // tag for logging calls
    public final static String TAG = "MovieListActivity";
    // instance fields
    AsyncHttpClient client;
    // base url for loading images
    String imageBaseURL;
    // size of the poster to grab
    String posterSize;

    // make a list of movies
    ArrayList<Movie> movies;

    // the recycler view
    RecyclerView rvMovies;
    // the adapter wired to the recycler view
    MovieAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        // initialize the client
        client = new AsyncHttpClient();
        // initialize the list of movies
        movies = new ArrayList<>();
        // initialize adapter
        adapter = new MovieAdapter(movies);

        // resolve the recycler view and connect a layout manager and adapter
        rvMovies = (RecyclerView) findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);

        // get the configuration when the app is created
        getConfiguration();

    }

    // get the list of currently playing movies from API
    private void getNowPlaying()
    {
        // make the URL
        String url =  BASE_API_URL + "/movie/now_playing";
        // set up the request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key)); // API_KEY is always required

        // this represents an asynchronous process, the client sends a
        // request for information, and only executes the specified
        // function when the response is received
        // this line only sends out the request and the lines beneath
        // it get executed in the meantime (like multithreading)
        client.get(url, params, new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try
                {
                    JSONArray results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++)
                    {
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);
                        // notify adapter that a row was added
                        adapter.notifyItemInserted(movies.size() - 1);

                    }
                    Log.i(TAG, String.format("Loaded %s movies", results.length()));
                }
                catch (JSONException e)
                {
                    logError("Failed getting now_playing data", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed getting now_playing data", throwable, true);
            }
        });
    }

    // get the configuration from the API
    private void getConfiguration()
    {
        // make the URL
        String url =  BASE_API_URL + "/configuration";
        // set up the request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key)); // API_KEY is always required

        // this represents an asynchronous process, the client sends a
        // request for information, and only executes the specified
        // function when the response is received
        // this line only sends out the request and the lines beneath
        // it get executed in the meantime (like multithreading)
        client.get(url, params, new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response)
            {
                try
                {
                    JSONObject images = response.getJSONObject("images");
                    // grab the base URL from the parsing
                    imageBaseURL = images.getString("secure_base_url");
                    // get the posterSize as well
                    JSONArray posterSizes = images.getJSONArray("poster_sizes");
                    posterSize = posterSizes.optString(3, "w342");
                    Log.i(TAG, String.format("Loaded configuration"));
                    // get the list of movies that are now playing
                    getNowPlaying();
                }
                catch (JSONException e)
                {
                    logError("Failed parsing base URL", e, true);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t)
            {
                logError("Failed getting configuration", t, true);
            }
        });
    }

    // error handler!
    private void logError(String message, Throwable error, boolean alertUser)
    {
        // always log the error
        Log.e(TAG, message, error);
        // alert the user so the errors are not silent
        if (alertUser)
        {
            // show a long toast with the error message
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
