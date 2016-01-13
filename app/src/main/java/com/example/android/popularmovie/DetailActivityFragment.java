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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment  {
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private List<String> movieTrailerListKey = new ArrayList<>();
    private List<String> movieTrailerListName = new ArrayList<>();
    private List<String> movieReviewList = new ArrayList<>();
    protected String movie_id;
    protected String movie_title;
    protected String movie_poster;
    protected String movie_backdrop;
    protected String movie_release;
    protected String movie_rating;
    protected String movie_overview;
    protected Button favouriteButton;

    ArrayList<Trailer> trailers = new ArrayList<>();

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View detailView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        /**favouriteButton.findViewById(R.id.favourite_button);
        favouriteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.favourite_button) {
                    Uri uri = MovieContract.FavouriteEntry.CONTENT_URI.buildUpon().appendPath(movie_id).build();
                    Cursor favouriteCursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                    if (favouriteCursor.moveToNext() != true) {
                        ContentValues contentValues = generateContentValues();
                        Uri insertedUri = getActivity().getContentResolver().insert(MovieContract.FavouriteEntry.CONTENT_URI, contentValues);
                        long id = ContentUris.parseId(insertedUri);
                        if (id != -1) {
                            Toast.makeText(getActivity(), "Added to Favourites", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        deleteFavourite();
                        Toast.makeText(getActivity(), "Delete from favourites", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
*/
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

            ListView trailerListView = (ListView) detailView.findViewById(R.id.trailer_list_view);
            TrailerAdapter ta = new TrailerAdapter (getContext(),trailers);
            trailerListView.setAdapter(ta);

            trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startActivity(new Intent (Intent.ACTION_VIEW, Uri.parse(trailers.get(position).getKey())));
                }
            });


            //Execute FetchTrailerTask
            //FetchTrailerTask fetchTrailerTask = new FetchTrailerTask();
            //fetchTrailerTask.execute();
            //Execute FetchReviewTask
            //FetchReviewTask fetchReviewTask = new FetchReviewTask();
            //fetchReviewTask.execute();
        }
        return detailView;
    }

    @Override
    public void onStart(){
        super.onStart();
        //Execute FetchTrailerTask
        FetchTrailerTask fetchTrailerTask = new FetchTrailerTask();
        fetchTrailerTask.execute();
        //Execute FetchReviewTask
        FetchReviewTask fetchReviewTask = new FetchReviewTask();
        fetchReviewTask.execute();
        //Favourite button
        //Button favouriteButton = new Button(getActivity());
       // favouriteButton.findViewById(R.id.favourite_button).setOnClickListener(favouriteClick);
    }
    // Favourite button onClick
    /**
        final OnClickListener favouriteClick = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.favourite_button) {
                    Uri uri = MovieContract.FavouriteEntry.CONTENT_URI.buildUpon().appendPath(movie_id).build();
                    Cursor favouritecursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                    if (favouritecursor.moveToNext() != true) {
                        ContentValues contentValues = generateContentValues();
                        Uri insertedUri = getActivity().getContentResolver().insert(MovieContract.FavouriteEntry.CONTENT_URI, contentValues);
                        long id = ContentUris.parseId(insertedUri);
                        if (id != -1) {
                            Toast.makeText(getActivity(), "Added to Favourites", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        deleteFavourite();
                        Toast.makeText(getActivity(), "Delete from favourites", Toast.LENGTH_SHORT).show();
                    }
                }
            }

*/
            //insert movie to FavouriteEntry table
            private ContentValues generateContentValues() {
                ContentValues favouriteMovieValues = new ContentValues();
                favouriteMovieValues.put(MovieContract.FavouriteEntry.COLUMN_MOVIE_ID, movie_id);
                favouriteMovieValues.put(MovieContract.FavouriteEntry.COLUMN_TITLE, movie_title);

                favouriteMovieValues.put(MovieContract.FavouriteEntry.COLUMN_BACK_DROP, movie_backdrop);
                favouriteMovieValues.put(MovieContract.FavouriteEntry.COLUMN_POSTER, movie_poster);
                favouriteMovieValues.put(MovieContract.FavouriteEntry.COLUMN_OVERVIEW, movie_overview);
                favouriteMovieValues.put(MovieContract.FavouriteEntry.COLUMN_RATING, movie_rating);
                favouriteMovieValues.put(MovieContract.FavouriteEntry.COLUMN_RELEASE_DATE, movie_release);


                return favouriteMovieValues;
            }

            public void deleteFavourite() {
                getActivity().getContentResolver().delete(MovieContract.FavouriteEntry.CONTENT_URI, movie_id, null);
            }
     //   };
    // Add FetchTrailerTask
    public class FetchTrailerTask extends AsyncTask<String, Void, String> {
        private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();

        private void getTrailerDataFromJson(String trailerJsonStr) throws JSONException {
            //get the root "result" array
            JSONObject trailerObject = new JSONObject(trailerJsonStr);
            JSONArray trailerArray = trailerObject.getJSONArray("results");
            //base Url for the TrailerInfo
            final String YoutubeBaseUrl = "https://www.youtube.com/watch?v=";

            for (int i = 0; i < trailerArray.length(); i++) {
                JSONObject trailer = trailerArray.getJSONObject(i);
                if (trailer.getString("site").contentEquals("YouTube")) {
                    movieTrailerListKey.add(i, YoutubeBaseUrl + trailer.getString("key"));
                    movieTrailerListName.add(i, trailer.getString("name"));
                }
                Log.i(LOG_TAG, " YouTube URL is: " + movieTrailerListKey.get(i));
                Log.i(LOG_TAG, " YouTube Trailer name is: " + movieTrailerListName.get(i));
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
            Log.i(LOG_TAG, "Result trailer url is:   " + movieTrailerListKey);
            Log.i(LOG_TAG, "Result trailer name is:   " + movieTrailerListName);

            //ArrayList<Trailer> trailers = new ArrayList<>();
            for (int i=0; i< movieTrailerListKey.size(); i++){
                Trailer trailer = new Trailer();
                trailer.name = movieTrailerListName.get(i);
                trailer.key = movieTrailerListKey.get(i);
                trailers.add(trailer);
            }
            /**
            ListView trailerListView = (ListView) getView().findViewById(R.id.trailer_list_view);
            TrailerAdapter ta = new TrailerAdapter (getContext(),trailers);
            trailerListView.setAdapter(ta);
             */
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
                    final String BASE_URL = "http://api.themoviedb.org/3/movie/" + movie_id + "/reviews";

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
                Log.i(LOG_TAG, "Result review is:   " + movieReviewList);

                ArrayList<Review> reviews = new ArrayList<>();

                for (int i=0; i< movieReviewList.size(); i++){
                    Review review = new Review();
                    review.content =  movieReviewList.get(i);
                    reviews.add(review);
                }

                ListView reviewListView = (ListView) getView().findViewById(R.id.review_list_view);
                ReviewAdapter ra = new ReviewAdapter (getContext(),reviews);
                reviewListView.setAdapter(ra);
            }
        }


}


