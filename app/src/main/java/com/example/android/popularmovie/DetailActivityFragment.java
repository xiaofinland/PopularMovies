package com.example.android.popularmovie;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.android.popularmovie.data.MovieContract;
import com.squareup.picasso.Picasso;

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
import java.util.List;
import java.util.Properties;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private List<String> movieTrailerList = new ArrayList<>();
    private List<String> movieReviewList = new ArrayList<>();
    protected String movie_id;
    protected String movie_title;
    protected String movie_poster;
    protected String movie_backdrop;
    protected String movie_release;
    protected String movie_rating;
    protected String movie_overview;
    private ToggleButton toggleButton;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View detailView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        //pass ID
        if (intent != null && intent.hasExtra("MOVIE_ID")) {
            movie_id = intent.getStringExtra("MOVIE_ID");
            Log.i(LOG_TAG, "movie id is: " + movie_id);
        }

        //pass title
        if (intent != null && intent.hasExtra("MOVIE_TITLE")) {
            movie_title = intent.getStringExtra("MOVIE_TITLE");
            ((TextView) detailView.findViewById(R.id.movie_title_text))
                    .setText(movie_title);

            // set activity title
            getActivity().setTitle(movie_title);

            //pass poster image
            movie_poster = intent.getStringExtra("MOVIE_POSTER");
            Log.i(LOG_TAG, "poster URL: " + movie_poster);
            ImageView poster = (ImageView) detailView.findViewById(R.id.poster_image_view);
            Picasso
                    .with(getActivity())
                    .load(movie_poster)
                    .fit()
                    .into(poster);
            //pass Backdrop image
            movie_backdrop = intent.getStringExtra("MOVIE_BACKDROP");
            Log.i(LOG_TAG, "Backdrop URL: " + movie_backdrop);
            ImageView backdrop = (ImageView) detailView.findViewById(R.id.backdrop_image_view);
            Picasso
                    .with(getContext())
                    .load(movie_backdrop)
                    .fit()
                    .into(backdrop);

            //pass release date
            movie_release = intent.getStringExtra("MOVIE_RELEASE");
            ((TextView) detailView.findViewById(R.id.movie_releas_date_text))
                    .setText(movie_release);
            //pass rating
            movie_rating = intent.getStringExtra("MOVIE_RATING");
            ((TextView) detailView.findViewById(R.id.movie_rating_text))
                    .setText(movie_rating);
            //pass overview
            movie_overview = intent.getStringExtra("MOVIE_OVERVIEW");
            ((TextView) detailView.findViewById(R.id.movie_overview_text))
                    .setText(movie_overview);
            //Execute FetchTrailerTask
            FetchTrailerTask fetchTrailerTask = new FetchTrailerTask();
            fetchTrailerTask.execute();
            //Execute FetchReviewTask
            FetchReviewTask fetchReviewTask = new FetchReviewTask();
            fetchReviewTask.execute();
        }
        return detailView;
    }
    // Favourite button onClick
    public void favouriteClick (View view){
        boolean isOn;

        if (isOn = true){
            insertFavourite();

        }else {
            deleteFavourite();
        }
    }
    //insert movie to FavouriteEntry table
    public void insertFavourite () {
        ContentValues favouriteMovieValues = new ContentValues();
        favouriteMovieValues.put(MovieContract.FavouriteEntry.COLUMN_MOVIE_ID, movie_id);
        favouriteMovieValues.put(MovieContract.FavouriteEntry.COLUMN_TITLE,movie_title);
        favouriteMovieValues.put(MovieContract.FavouriteEntry.COLUMN_BACK_DROP, movie_backdrop);
        favouriteMovieValues.put(MovieContract.FavouriteEntry.COLUMN_OVERVIEW, movie_overview);
        favouriteMovieValues.put(MovieContract.FavouriteEntry.COLUMN_POSTER,movie_poster);
        favouriteMovieValues.put(MovieContract.FavouriteEntry.COLUMN_RELEASE_DATE, movie_release);
        favouriteMovieValues.put(MovieContract.FavouriteEntry.COLUMN_RATING,movie_rating);
        getActivity().getContentResolver().insert(MovieContract.FavouriteEntry.CONTENT_URI, favouriteMovieValues);
    }
    public void deleteFavourite(){
        getActivity().getContentResolver().delete(MovieContract.FavouriteEntry.CONTENT_URI, movie_id, null);

    }
    // Add FetchTrailerTask
    public class FetchTrailerTask extends AsyncTask<String, Void, String> {
        private void getTrailerDataFromJson(String trailerJsonStr) throws JSONException {
            //get the root "result" array
            JSONObject trailerObject = new JSONObject(trailerJsonStr);
            JSONArray trailerArray = trailerObject.getJSONArray("results");
            //base Url for the TrailerInfo
            final String YoutubeBaseUrl = "https://www.youtube.com/watch?v=";

            for (int i = 0; i < trailerArray.length(); i++) {
                JSONObject trailer = trailerArray.getJSONObject(i);
                if (trailer.getString("site").contentEquals("YouTube")) {
                    movieTrailerList.add(i, YoutubeBaseUrl + trailer.getString("key"));
                }
                Log.i(LOG_TAG, " YouTube URL is: " + movieTrailerList.get(i));
            }
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // contain the raw JSON response as as string
            String trailerDataStr = null;
            try {
                //construct URL
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + movie_id + "/videos";

                Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter("api_key", BuildConfig.MOVIE_DATABASE_API_KEY)
                        .build();

                URL url = new URL(buildUri.toString());
                Log.e(LOG_TAG, "Trailer url is " + url);

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
                trailerDataStr = buffer.toString();

            } catch (IOException e) {
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
                getTrailerDataFromJson(trailerDataStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(LOG_TAG, "Result trailer is:   " + movieTrailerList);

            ArrayList<Trailer> trailers = new ArrayList<>();

            ListView trailerListView = (ListView) getView().findViewById(R.id.trailer_list_view);
            TrailerAdapter ta = new TrailerAdapter (getContext(),trailers);
            trailerListView.setAdapter(ta);
            }
        }

        // Add FetchReviewTask
        public class FetchReviewTask extends AsyncTask<String, Void, String> {
            private final String LOG_TAG = FetchReviewTask.class.getSimpleName();

            private void getReviewDataFromJson(String reviewJsonStr) throws JSONException {
                //get the root "result" array
                JSONObject reviewObject = new JSONObject(reviewJsonStr);
                JSONArray reviewArray = reviewObject.getJSONArray("results");

                for (int i = 0; i < reviewArray.length(); i++) {
                    JSONObject review = reviewArray.getJSONObject(i);
                    movieReviewList.add(i, review.getString("content"));
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                // contain the raw JSON response as as string
                String reviewDataStr = null;
                try {
                    //construct URL
                    final String BASE_URL = "http://api.themoviedb.org/3/movie/" + "movie_id" + "/reviews";

                    Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                            .appendQueryParameter("api_key", BuildConfig.MOVIE_DATABASE_API_KEY)
                            .build();

                    URL url = new URL(buildUri.toString());
                    Log.e(LOG_TAG, "Review url is " + url);

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
                    reviewDataStr = buffer.toString();

                } catch (IOException e) {
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
                    getReviewDataFromJson(reviewDataStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(String result) {
                Log.i(LOG_TAG, "Result trailer is:   " + movieTrailerList);

                ArrayList<Review> reviews = new ArrayList<>();

                ListView reviewListView = (ListView) getView().findViewById(R.id.review_list_view);
                ReviewAdapter ra = new ReviewAdapter (getContext(),reviews);
                reviewListView.setAdapter(ra);
            }
        }


}


