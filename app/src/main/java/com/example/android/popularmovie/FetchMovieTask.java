package com.example.android.popularmovie;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by xiao on 19/11/2015.
 */
public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>> {
    private static final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private MovieAdapter mMovieAdapter;

    // public FetchMovieTask(){
    //      }
    public void setmMovieAdapter(MovieAdapter movieAdapter){
        this.mMovieAdapter =movieAdapter;
    }
    private ArrayList<Movie> getMovieDataFromJason(String MovieJasonStr) throws JSONException {
        final String JON_RESULTS = "results";

        // Values we are going to fetch from JSON.
        final String JON_ID = "id";
        final String JON_OVERVIEW = "overview";
        final String JON_RELEASE_DATE = "release_date";
        final String JON_POSTER_PATH = "poster_path";
        final String JON_BACKDROP_PATH = "backdrop_path";
        final String JON_TITLE = "original_title";
        final String JON_RATING = "vote_average";

        String thumbBaseUrl = "http://image.tmdb.org/t/p/w185/";
        String posterBaseUrl = "http://image.tmdb.org/t/p/w185/";
        String backdropBaseUrl = "http://image.tmdb.org/t/p/w342/";

        JSONObject moviewJson = new JSONObject(MovieJasonStr);
        JSONArray movieArray = moviewJson.getJSONArray(JON_RESULTS);

        //Loop JSON Array and fetch JSON movie objects
        ArrayList<Movie> resultMovies = new ArrayList<Movie>();
        for (int i = 0; i < movieArray.length(); i++) {
            //set strings default to null
            String id = null;
            String overview = null;
            String release_date = null;
            String thumb = null;
            String poster = null;
            String back_drop =null;
            String title = null;
            String rating = null;
            //Get JSON object representing the Movie.
            JSONObject currentMovie = movieArray.getJSONObject(i);

            //get needed data from JSON text
            id = currentMovie.getString(JON_ID);
            overview = currentMovie.getString(JON_OVERVIEW);
            release_date = currentMovie.getString(JON_RELEASE_DATE);
            thumb = thumbBaseUrl + currentMovie.getString(JON_POSTER_PATH);
            poster = posterBaseUrl + currentMovie.getString(JON_POSTER_PATH);
            back_drop =backdropBaseUrl + currentMovie.getString(JON_BACKDROP_PATH);
            title = currentMovie.getString(JON_TITLE);
            rating = currentMovie.getString(JON_RATING);

            // Make a movie object and add to movie list
            Movie movie = new Movie(id, thumb, title, poster, back_drop,overview, release_date, rating);
            resultMovies.add(movie);
            Log.i(LOG_TAG, movie.toString());
        }
        return resultMovies;
    }

    @Override
    protected ArrayList<Movie> doInBackground(String... params) {

        if (params.length == 0) {
            //nothing to look up
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //Will content the raw JSON response as a string.
        String movieJsonStr = null;

        //Sorting format passed in from setting activity,
        String sortFormat = null;
        sortFormat = params[0];


        try {
            //building bloacks of  URL to to be built
            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_PARAM = "sort_by";
            final String API_PARAM = "api_key";

            //build URI
            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_PARAM, sortFormat)
                    .appendQueryParameter(API_PARAM, BuildConfig.MOVIE_DATABASE_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            Log.d(LOG_TAG, "URL: " + builtUri.toString());
            //Create the request to Moviedb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            //Reading input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                //nothing to do
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                //empty stream. No parsing
                return null;
            }
            movieJsonStr = buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            return getMovieDataFromJason(movieJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Movie> result) {
        Log.i(LOG_TAG, "Result movie:   "+ result.toString());
        if (result != null) {
            mMovieAdapter.clear();

            for (Movie item : result) {
                mMovieAdapter.add(item);
            }


        }
    }
}