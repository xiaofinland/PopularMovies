package com.example.android.popularmovie;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovie.data.MovieContract;

/**
 * Created by Xiao on 22/12/2015.
 */
public class FavouriteAdapter extends CursorAdapter {
    private static final String LOG_TAG = FavouriteAdapter.class.getSimpleName();
    private Context mContext;
    private static int sLoaderID;

    public static class ViewHolder {
        public final ImageView favouriteGrid;

        public ViewHolder (View view){
            favouriteGrid = (ImageView)view.findViewById(R.id.favourite_image_item);
        }
    }
    public FavouriteAdapter(Context context, Cursor c, int flags, int loaderID){
        super(context,c,flags);
        Log.d(LOG_TAG, "FavouriteAdaper");
        mContext=context;
        sLoaderID=loaderID;
    }
    @Override
    public View newView (Context context,Cursor cursor,ViewGroup parent){
        int layoutId = R.layout.favourite_image_item;

        Log.d (LOG_TAG,  "in new view");

        View view = LayoutInflater.from(context).inflate(layoutId,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }
    @Override
    public void bindView (View view,Context context,Cursor cursor){
        ViewHolder viewHolder = (ViewHolder)view.getTag();
        Log.d (LOG_TAG, "in bind view");
        int thumbIndex = cursor.getColumnIndex(MovieContract.FavouriteEntry.COLUMN_THUMB);
        int thumbImage =cursor.getInt(thumbIndex);
        Log.i(LOG_TAG,"thumbIndex:  "+thumbIndex);
        Log.i(LOG_TAG,"thumbImage:  "+thumbImage);
        viewHolder.favouriteGrid.setImageResource(thumbImage);
    }
}


