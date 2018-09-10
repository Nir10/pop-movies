package com.niranjan.popmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class PopMoviesContentProvider extends ContentProvider {

    public static final String TAG = PopMoviesContract.class.getSimpleName();
    public static final int FAVOURITES = 100;
    public static final int FAVOURITES_ID =101;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(PopMoviesContract.AUTHORITY,PopMoviesContract.PATH_FAVOURITE,FAVOURITES);
        uriMatcher.addURI(PopMoviesContract.AUTHORITY,PopMoviesContract.PATH_FAVOURITE+"/#",FAVOURITES_ID);

        return uriMatcher;
    }

    private PopMoviesDbHelper mPopMoviesDbHelper;
    @Override
    public boolean onCreate() {
        Context context = getContext();
        mPopMoviesDbHelper = new PopMoviesDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = mPopMoviesDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        Cursor returnCursor;

        switch (match){
            case FAVOURITES:
                returnCursor = db.query(PopMoviesContract.FavouritMoviesEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
                break;
            case FAVOURITES_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = PopMoviesContract.FavouritMoviesEntry._ID+"=?";
                String[] mSelectionArgs = new String[]{id};
                returnCursor = db.query(PopMoviesContract.FavouritMoviesEntry.TABLE_NAME,
                        null,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        null);
                break;
            default: throw new UnsupportedOperationException("Invalid Uri");
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return returnCursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mPopMoviesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        Log.d(TAG,"Uri match id ="+match);
        Uri returnUri;
        switch (match){
            case FAVOURITES:
                long id = db.insert(PopMoviesContract.FavouritMoviesEntry.TABLE_NAME,
                        null,
                        values);
                if(id>0){
                    returnUri = ContentUris.withAppendedId(PopMoviesContract.FavouritMoviesEntry.CONTENT_URI,id);
                } else {
                    throw new UnsupportedOperationException("Failed to insert new row +"+uri);
                }
                break;
            default: throw new UnsupportedOperationException("Invalid Uri "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase db = mPopMoviesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int favouriteDeleted;

        switch(match){
            case FAVOURITES_ID:
                String id = uri.getPathSegments().get(1);
                favouriteDeleted = db.delete(PopMoviesContract.FavouritMoviesEntry.TABLE_NAME,
                        PopMoviesContract.FavouritMoviesEntry._ID+"=?",
                        new String[] {id});
                break;
            default: throw new UnsupportedOperationException("Invalid uri +"+uri);
        }

        if(favouriteDeleted != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return favouriteDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
