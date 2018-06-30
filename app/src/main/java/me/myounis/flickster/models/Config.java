package me.myounis.flickster.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Config {

    // base url for loading images
    String imageBaseURL;
    // size of the poster to grab
    String posterSize;

    // the backdrop size
    String backdropSize;

    public Config(JSONObject response) throws JSONException
    {
        JSONObject images = response.getJSONObject("images");
        // grab the base URL from the parsing
        imageBaseURL = images.getString("secure_base_url");
        // get the posterSize as well
        JSONArray posterSizes = images.getJSONArray("poster_sizes");
        posterSize = posterSizes.optString(3, "w342");
        // parse for the background sizes
        JSONArray backdropSizeOptions = images.getJSONArray("backdrop_sizes");
        backdropSize = backdropSizeOptions.optString(1, "w780");

    }

    public String getImageUrl(String size, String path)
    {
        // concatenate all 3 of them to retrieve the image
        return String.format("%s%s%s", imageBaseURL, size, path);
    }

    public String getImageBaseURL() {
        return imageBaseURL;
    }

    public String getPosterSize() {
        return posterSize;
    }

    public String getBackdropSize() {
        return backdropSize;
    }
}
