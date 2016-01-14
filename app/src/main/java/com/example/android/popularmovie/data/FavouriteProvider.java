package com.example.android.popularmovie.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;


/**
 * Created by Xiao on 22/12/2015.
 */
public class FavouriteProvider extends ContentProvider {
    private static final String LOG_TAG = FavouriteProvider.class.getSimpleName();
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int FAVOURITE = 100;
    static final int FAVOURITE_WITH_ID = 101;

    private static final String sMovieWithIDSelection =
           MovieContract.FavouriteEntry.TABLE_NAME+"." + MovieContract.FavouriteEntry.COLUMN_MOVIE_ID + " = ? ";

    private Cursor getMovieByMovieId(Uri uri, String[] projection, String sortOrder) {
        String favouriteMovieId = MovieContract.FavouriteEntry.getFavouriteIdFromUri(uri);
        Log.i(LOG_TAG, "id in getMovieByMoiveId method: "+favouriteMovieId);
        String[] selectionArgs = new String[]{favouriteMovieId};
        String selection = sMovieWithIDSelection;
        Log.i (LOG_TAG, "selection in getMovieByMoiveID method: "+ selection);
        return mOpenHelper.getReadableDatabase().query(MovieContract.FavouriteEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher(){
        //matcher is for switch function to find right table
        //.NO_MATCH is typical way to have clean matcher
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority,MovieContract.PATH_FAVOURITE,FAVOURITE);
        matcher.addURI(authority,MovieContract.PATH_FAVOURITE + "/#", FAVOURITE_WITH_ID);
        return matcher;

    }


    @Override
    public boolean onCreate(){
        mOpenHelper = new MovieDbHelper(getContext());
        //mOpenHelper = MovieDbHelper.getInstance(getContext());
        return true;
    }
    @Override
    public String getType (Uri uri){
        final int match =sUriMatcher.match(uri);
        switch (match){
            case FAVOURITE:
                return MovieContract.FavouriteEntry.CONTENT_TYPE;
            case FAVOURITE_WITH_ID:
                return MovieContract.FavouriteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
    @Override
    public Cursor query (Uri uri,String[] projection, String selection, String[] selectionArgs,
                         String sortOrder) {
        Cursor retCursor;
        final int match = sUriMatcher.match(uri);
        Log.i(LOG_TAG, "query match is: "+match);
        switch (match) {
            case FAVOURITE_WITH_ID: {
                //retCursor = getMovieByMovieId(uri,projection,sortOrder);
                retCursor =mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavouriteEntry.TABLE_NAME,
                        projection,
                        MovieContract.FavouriteEntry.COLUMN_MOVIE_ID +"=?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                Log.i(LOG_TAG, "query cursor: " + retCursor);
                return retCursor;
            }
            case FAVOURITE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavouriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                return retCursor;
            }

            default:
                throw new UnsupportedOperationException("unknown uri: " + uri);
        }

    }
    @Override
    public Uri insert (Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        Log.i(LOG_TAG, "insert match is: "+match);
        switch (match) {
            case FAVOURITE: {
                long _id = db.insert(MovieContract.FavouriteEntry.TABLE_NAME, null, values);
                Log.i(LOG_TAG, "insert Id: "+_id);
                //insert unless it is already contained in the database
                if (_id > 0)
                    returnUri = MovieContract.FavouriteEntry.buildFavouriteUri(_id);
                else
                    throw new android.database.SQLException("Failed to inset row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }
    @Override
    public int delete(Uri uri, String selection, String[]selectionArgs){
        final SQLiteDatabase db =mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection= "1";
        switch (match){
            case FAVOURITE:
                rowsDeleted = db.delete(MovieContract.FavouriteEntry.TABLE_NAME,selection,selectionArgs);
                //reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME ='" + MovieContract.FavouriteEntry.TABLE_NAME + "'");
                break;

            case FAVOURITE_WITH_ID:
                rowsDeleted = db.delete(MovieContract.FavouriteEntry.TABLE_NAME,
                        MovieContract.FavouriteEntry._ID +"=?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                //reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        MovieContract.FavouriteEntry.TABLE_NAME + "'");
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted !=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsDeleted;
    }
    @Override
    public int update (Uri uri, ContentValues values, String selection, String[]selectionArgs){
        final SQLiteDatabase db =mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match){
            case FAVOURITE:
                rowsUpdated = db.update(MovieContract.FavouriteEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri "+ uri);
        }
        if (rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }
    @Override
    public int bulkInsert (Uri uri, ContentValues [] values){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match){
            case FAVOURITE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values){
                        long _id = db.insert(MovieContract.FavouriteEntry.TABLE_NAME,null,value);
                        if (_id !=-1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
