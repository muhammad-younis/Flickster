package me.myounis.flickster;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

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



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        client = new AsyncHttpClient();
        // get the configuration when the app is created
        getConfiguration();
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
                try {
                    JSONObject images = response.getJSONObject("images");
                    // grab the base URL from the parsing
                    imageBaseURL = images.getString("secure_base_url");
                    // get the posterSize as well
                    JSONArray posterSizes = images.getJSONArray("poster_sizes");
                    posterSize = posterSizes.optString(3, "w342");
                } catch (JSONException e){
                    logError("Failed parsing base URL", e, true);
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t)
            {
                logError("Failed getting configuratinon", t, true);
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
