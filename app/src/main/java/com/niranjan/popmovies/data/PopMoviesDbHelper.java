package com.niranjan.popmovies.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class PopMoviesDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "popmovies.db";
    public static final int DATABASE_VERSION = 1;

    public PopMoviesDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE "+PopMoviesContract.FavouritMoviesEntry.TABLE_NAME+"("+
                PopMoviesContract.FavouritMoviesEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                PopMoviesContract.FavouritMoviesEntry.COLUMN_TITLE+ " TEXT NOT NULL, "+
                PopMoviesContract.FavouritMoviesEntry.COLUMN_MOVIE_POSTER+" TEXT NOT NULL, "+
                PopMoviesContract.FavouritMoviesEntry.COLUMN_SYNOPSIS+" TEXT NOT NULL, "+
                PopMoviesContract.FavouritMoviesEntry.COLUMN_RELEASE_DATE+ " DATE NOT NULL, "+
                PopMoviesContract.FavouritMoviesEntry.COLUMN_USER_RATING+ " TEXT NOT NULL "+
                ")";
                db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+ PopMoviesContract.FavouritMoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
