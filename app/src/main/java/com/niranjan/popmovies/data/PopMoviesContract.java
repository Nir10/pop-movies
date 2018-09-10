package com.niranjan.popmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class PopMoviesContract {


    public static final String AUTHORITY = "com.niranjan.popmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_FAVOURITE = "favourites";

    public static final class FavouritMoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITE).build();

        public static final String TABLE_NAME = "favourite";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_MOVIE_POSTER = "poster";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_USER_RATING = "user_rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";

    }
}
