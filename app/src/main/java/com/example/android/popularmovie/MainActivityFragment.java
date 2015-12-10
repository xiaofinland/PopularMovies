package com.example.android.popularmovie;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment  {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private GridView mGridView;
    private MovieAdapter mMovieAdapter;
    //Keys for Intent to detail activity
    public static final String MOVIE_TITLE = "MOVIE_TITLE";
    public static final String MOVIE_POSTER = "MOVIE_POSTER";
    //public static final String MOVIE_THUMB = "MOVIE_THUMB";
    public static final String MOVIE_BACKDROP = "MOVIE_BACKDROP";
    public static final String MOVIE_RELEASE = "MOVIE_RELEASE";
    public static final String MOVIE_RATING = "MOVIE_RATING";
    public static final String MOVIE_OVERVIEW = "MOVIE_OVERVIEW";

    //ArrayList of movies
    public ArrayList<Movie> movies = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public MainActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.movies_gridView);
        mMovieAdapter = new MovieAdapter(getActivity(), movies);
        mGridView.setAdapter(mMovieAdapter);

        Log.i(LOG_TAG, "movies" + movies);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(MOVIE_TITLE, movies.get(position).title)
                        .putExtra(MOVIE_POSTER, movies.get(position).poster)
                        .putExtra(MOVIE_BACKDROP, movies.get(position).back_drop)
                        .putExtra(MOVIE_RELEASE, movies.get(position).release_date)
                        .putExtra(MOVIE_RATING, movies.get(position).rating)
                        .putExtra(MOVIE_OVERVIEW, movies.get(position).overview);
                startActivity(detailIntent);
            }
        });

        return rootView;
    }

    public void updateMovies() {
        FetchMovieTask fetchMovieTask = new FetchMovieTask();
        fetchMovieTask.setmMovieAdapter(mMovieAdapter);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortPref = prefs.getString(
                getString(R.string.pref_sorting_key),
                getString(R.string.pref_sorting_default));
        fetchMovieTask.execute(sortPref);
        Log.i(LOG_TAG, "Sort Pref:  "+sortPref);
    }

    public void onStart() {
        super.onStart();
        updateMovies();
    }
}

