package com.example.android.popularmovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



/**
 * Created by Xiao on 22/12/2015.
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;

    public static final String DATABASE_NAME ="movie.db";

    public MovieDbHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate (SQLiteDatabase sqLiteDatabase){
        // Create a table to hold favourites.  A favourite consists of the string supplied in the movie_id
        final String SQL_CREATE_FAVOURITE_TABLE = "CREATE TABLE " + MovieContract.FavouriteEntry.TABLE_NAME + " (" +
                MovieContract.FavouriteEntry._ID + "INTEGER PRIMARY KEY," +
                MovieContract.FavouriteEntry.COLUMN_MOVIE_ID + "REAL NOT NULL" +
                MovieContract.FavouriteEntry.COLUMN_TITLE +"REAL NOT NULL"+
                MovieContract.FavouriteEntry.COLUMN_THUMB + "REAL NOT NULL" +
                MovieContract.FavouriteEntry.COLUMN_BACK_DROP + "REAL NOT NULL" +
                MovieContract.FavouriteEntry.COLUMN_POSTER + "REAL NOT NULL"+
                MovieContract.FavouriteEntry.COLUMN_OVERVIEW + "REAL NOT NULL" +
                MovieContract.FavouriteEntry.COLUMN_RATING + "REAL NOT NULL" +
                MovieContract.FavouriteEntry.COLUMN_RELEASE_DATE + "REAL NOT NULL" +
                MovieContract.FavouriteEntry.COLUMN_REVIEW + "REAL NOT NULL" +
                MovieContract.FavouriteEntry.COLUMN_TRAILER + "REAL NOT NULL" +
                " )";


        sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavouriteEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}