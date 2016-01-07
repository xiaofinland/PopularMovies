package com.example.android.popularmovie.data;

        import android.content.ContentResolver;
        import android.content.ContentUris;
        import android.net.Uri;
        import android.provider.BaseColumns;

/**
 * Created by Xiao on 22/12/2015.
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY ="com.example.android.popularmovie.app";
    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)

    public static final String PATH_FAVOURITE = "favourite";


    //Inner class that defines the table contents of the favourite table
    public static final class FavouriteEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+ "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITE;

        public static final  String TABLE_NAME = "favourite";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_THUMB = "thumb";
        public static final String COLUMN_BACK_DROP = "back_drop";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_REVIEW = "review";
        public static final String COLUMN_TRAILER = "trailer";

        public static Uri buildFavouriteUri (Long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
