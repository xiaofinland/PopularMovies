package com.example.android.popularmovie;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.popularmovie.data.MovieContract;

/**
 * Created by Xiao on 10/01/2016.
 */
public class FavouriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private FavouriteAdapter mFavouriteAdapter;
    private GridView mGridView;

    private static final int CURSOR_LOADER_ID = 0;


    public FavouriteFragment(){

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        Cursor c =
                getActivity().getContentResolver().query(MovieContract.FavouriteEntry.CONTENT_URI,
                        new String[]{MovieContract.FavouriteEntry._ID},
                        null,
                        null,
                        null);

        // initialize loader
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate fragment_main layout
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        // initialize our FlavorAdapter
        mFavouriteAdapter = new FavouriteAdapter(getActivity(), null, 0, CURSOR_LOADER_ID);
        // initialize mGridView to the GridView in fragment_main.xml
        mGridView = (GridView) rootView.findViewById(R.id.favourite_grid_view);
        // set mGridView adapter to our CursorAdapter
        mGridView.setAdapter(mFavouriteAdapter);


        return rootView;
    }

    // Attach loader to our flavors database query
    // run when loader is initialized
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        return new CursorLoader(getActivity(),
                MovieContract.FavouriteEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
    }

    // Set the cursor in our CursorAdapter once the Cursor is loaded
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mFavouriteAdapter.swapCursor(data);


    }

    // reset CursorAdapter on Loader Reset
    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        mFavouriteAdapter.swapCursor(null);
    }
}
