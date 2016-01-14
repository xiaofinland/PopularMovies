package com.example.android.popularmovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.example.android.popularmovie.data.MovieContract.FavouriteEntry;

/**
 * Created by Xiao on 22/12/2015.
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 21;

    public static final String DATABASE_NAME ="movie.db";

    public MovieDbHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate (SQLiteDatabase sqLiteDatabase){

        final String SQL_CREATE_FAVOURITE_TABLE = "CREATE TABLE " +
                FavouriteEntry.TABLE_NAME + " (" +
                FavouriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavouriteEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                FavouriteEntry.COLUMN_TITLE +" TEXT NOT NULL, "+
                FavouriteEntry.COLUMN_THUMB + " TEXT NOT NULL, " +
                FavouriteEntry.COLUMN_BACK_DROP + " TEXT NOT NULL, " +
                FavouriteEntry.COLUMN_POSTER + " TEXT NOT NULL, "+
                FavouriteEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                FavouriteEntry.COLUMN_RATING + " REAL NOT NULL, " +
                FavouriteEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL); " ;


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