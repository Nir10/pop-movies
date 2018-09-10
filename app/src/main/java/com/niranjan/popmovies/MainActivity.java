package com.niranjan.popmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;


import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;


import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.niranjan.popmovies.data.PopMoviesContract;
import com.niranjan.popmovies.databinding.ActivityMainBinding;
import com.niranjan.popmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements
        MoviesAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<String> {

    final static String TAG = MainActivity.class.getSimpleName();

    ArrayList<Movie> movieList;
    MoviesAdapter mAdapter;

    Toast mToast;

    public static final String MOVIES_URL_EXTRA="movieListUrl";

    private static final int MOVIES_LOADER_ID = 22;
    private static final int FAVOURITE_MOVIES_LOADER_ID = 2;

    private boolean isFavSelected = false;

    ActivityMainBinding mBinding;

    // AsyncTaskLoader callback to get list of favourite movies from sqlite database
    private LoaderManager.LoaderCallbacks<Cursor> mFavouriteLoaderListener
            = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(final int id,final Bundle bundle) {
            return new AsyncTaskLoader<Cursor>(MainActivity.this) {

                Cursor movieCursorCache = null;
                //URL moviesListUrl = null;
                @Override
                protected void onStartLoading() {
                    super.onStartLoading();

                    if(movieCursorCache == null) {
                        mBinding.pbLoadingIndicator.setVisibility(View.VISIBLE);
                        movieList.clear();
                        mAdapter.setMovieList(null);
                        showMoviesListView();
                        forceLoad();
                    } else{
                        deliverResult(movieCursorCache);
                    }
                }

                @Override
                public Cursor loadInBackground() {

                    /*if( bundle == null){
                        return null;
                    }
                    String moviesStringUrl = bundle.getString(MOVIES_URL_EXTRA);

                    if(moviesStringUrl == null || TextUtils.isEmpty(moviesStringUrl)){
                        return null;
                    }
                    String moviesListResult = null;*/

                    Cursor cursor = null;
                    try {
                        cursor  = getContentResolver().query(PopMoviesContract.FavouritMoviesEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                null);

                    } catch (Exception e) {
                        Log.e(TAG, "Failed to asynchronously load data.");
                        e.printStackTrace();
                    }

                    return cursor;
                }

                @Override
                public void deliverResult(Cursor data) {
                    movieCursorCache = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            isFavSelected = true;
            mBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
            if((cursor.getCount() !=0) && (cursor!=null)){

                for(int i=0;i<cursor.getCount();i++){
                    cursor.moveToNext();

                    int idIndex = cursor.getColumnIndex(PopMoviesContract.FavouritMoviesEntry._ID);
                    int titleIndex = cursor.getColumnIndex(PopMoviesContract.FavouritMoviesEntry.COLUMN_TITLE);
                    int posterIndex = cursor.getColumnIndex(PopMoviesContract.FavouritMoviesEntry.COLUMN_MOVIE_POSTER);
                    int synopsisIndex = cursor.getColumnIndex(PopMoviesContract.FavouritMoviesEntry.COLUMN_SYNOPSIS);
                    int userRatingIndex = cursor.getColumnIndex(PopMoviesContract.FavouritMoviesEntry.COLUMN_USER_RATING);
                    int releaseDateIndex = cursor.getColumnIndex(PopMoviesContract.FavouritMoviesEntry.COLUMN_RELEASE_DATE);

                    // Determine the values of the wanted data
                    final int id = cursor.getInt(idIndex);
                    String title = cursor.getString(titleIndex);
                    String poster = cursor.getString(posterIndex);
                    String synopsis = cursor.getString(synopsisIndex);
                    String userRating = cursor.getString(userRatingIndex);
                    String releaseDate = cursor.getString(releaseDateIndex);

                    movieList.add(new Movie(id,title,poster,synopsis,userRating,releaseDate));
                    mAdapter.setMovieList(movieList);
                    showMoviesListView();
                }
            } else {
                showErrorMessage();
            }

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mToast = new Toast(this);

        GridLayoutManager layoutManager;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(MainActivity.this, 2);
            mBinding.rvMovieList.setLayoutManager(layoutManager);
        } else {
            layoutManager = new GridLayoutManager(MainActivity.this, 3);
            mBinding.rvMovieList.setLayoutManager(layoutManager);
        }
        mBinding.rvMovieList.setHasFixedSize(true);
        mAdapter = new MoviesAdapter(MainActivity.this,MainActivity.this);
        mBinding.rvMovieList.setAdapter(mAdapter);

        if(savedInstanceState == null || !savedInstanceState.containsKey("movies") ||
                !savedInstanceState.containsKey("isFavMovieSelected")){
            movieList = new ArrayList<>();
            makePopularMoviesListRequest();
            isFavSelected = false;
            getSupportActionBar().setTitle(getString(R.string.sort_most_popular));
        } else {
            movieList = savedInstanceState.getParcelableArrayList("movies");
            isFavSelected = savedInstanceState.getBoolean("isFavMovieSelected");
            if(movieList.size() > 0) {
                mAdapter.setMovieList(movieList);
            } else {
                showErrorMessage();
            }
            getSupportActionBar().setTitle(savedInstanceState.getString("title"));
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies",movieList);
        outState.putBoolean("isFavMovieSelected",isFavSelected);
        outState.putString("title",getSupportActionBar().getTitle().toString());
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onListItemClick(Movie movie) {
        Context context = MainActivity.this;
        Class movieDetailsActivity = MovieDetailsActivity.class;

        Intent startMovieDetailsActivityIntent = new Intent(context,movieDetailsActivity);

        startMovieDetailsActivityIntent.putExtra(Intent.EXTRA_TEXT,movie);
        startActivity(startMovieDetailsActivityIntent);

    }

    /**
     * This method will get list of POPULAR Movies from MovieDB server
     */
    public void makePopularMoviesListRequest(){

        URL popularMoviesListUrl = NetworkUtils.buildPopularMoviesURL();

        Bundle bundle = new Bundle();
        bundle.putString(MOVIES_URL_EXTRA,popularMoviesListUrl.toString());

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader loader = loaderManager.getLoader(MOVIES_LOADER_ID);
        LoaderManager.LoaderCallbacks callbacks = MainActivity.this;

        if(loader == null){
            loaderManager.initLoader(MOVIES_LOADER_ID,bundle,callbacks);
        } else {
            loaderManager.restartLoader(MOVIES_LOADER_ID, bundle, this);
        }
        isFavSelected = false;
    }

    /**
     * This method will get list of TOP RATED Movies from MovieDB server
     */
    public void makeTopRatedMoviesListRequest(){

        URL topRatedMoviesListUrl = NetworkUtils.buildTopRatedMoviesURL();
        Bundle bundle = new Bundle();
        bundle.putString(MOVIES_URL_EXTRA,topRatedMoviesListUrl.toString());

        LoaderManager.LoaderCallbacks callbacks = MainActivity.this;
        getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, bundle, this);
        isFavSelected = false;

    }


    //Implement Movie loader
    @Override
    public Loader<String> onCreateLoader(int i,final Bundle bundle) {
        return new AsyncTaskLoader<String>(this) {

            public String movieStringCache = null;
            URL moviesListUrl = null;
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if(bundle == null){
                    return;
                }

                    if(movieStringCache == null) {
                        mBinding.pbLoadingIndicator.setVisibility(View.VISIBLE);
                        movieList.clear();
                        mAdapter.setMovieList(null);
                        showMoviesListView();
                        forceLoad();
                    } else{
                        deliverResult(movieStringCache);
                    }
            }

            @Override
            public String loadInBackground() {

                if( bundle == null){
                    return null;
                }
                String moviesStringUrl = bundle.getString(MOVIES_URL_EXTRA);

                if(moviesStringUrl == null || TextUtils.isEmpty(moviesStringUrl)){
                    return null;
                }
                String moviesListResult = null;

                try{
                    moviesListUrl = new URL(moviesStringUrl);
                    moviesListResult = NetworkUtils.getResponseFromHttpUrl(moviesListUrl);
                } catch(IOException e){
                    e.printStackTrace();
                    return null;
                }

                return moviesListResult;
            }

            @Override
            public void deliverResult(String data) {

                movieStringCache = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String s) {

        mBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);

        if(s!= null && s != "") {
            //json parsing happens here...
            try {

                JSONObject jsonObject = new JSONObject(s);

                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jObj = jsonArray.getJSONObject(i);

                    Log.d(TAG," movie title = "+jObj.getString("title"));

                    movieList.add(new Movie(Integer.parseInt(jObj.getString("id")),
                            jObj.getString("title"),
                            jObj.getString("poster_path"),
                            jObj.getString("overview"),
                            jObj.getString("vote_average"),
                            jObj.getString("release_date")));
                }

                showMoviesListView();
                mAdapter.setMovieList(movieList);
            } catch(JSONException e){
                e.printStackTrace();
            }

        } else {
            showErrorMessage();
        }

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        mAdapter.setMovieList(null);

    }

    /**
     * This method will make the View for the Movies list data visible and
     * hide the error message.
     */
    public void showMoviesListView(){

        mBinding.rvMovieList.setVisibility(View.VISIBLE);
        mBinding.tvErrorMessage.setVisibility(View.INVISIBLE);

    }

    /**
     * This method will make the error message visible and hide the Movies
     * list View.
     */
    public void showErrorMessage(){

        if(isFavSelected){
            mBinding.tvErrorMessage.setText(getString(R.string.fav_error_message));
        } else {
            mBinding.tvErrorMessage.setText(getString(R.string.error_message));
        }
        mBinding.tvErrorMessage.setVisibility(View.VISIBLE);
        mBinding.rvMovieList.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatwasclickedId = item.getItemId();

        if(itemThatwasclickedId == R.id.action_most_popular){
            mAdapter.setMovieList(null);
            makePopularMoviesListRequest();
            getSupportActionBar().setTitle(getString(R.string.sort_most_popular));
        } else if(itemThatwasclickedId == R.id.action_top_rated){
            mAdapter.setMovieList(null);
            makeTopRatedMoviesListRequest();
            getSupportActionBar().setTitle(getString(R.string.sort_highest_rated));
        } else if(itemThatwasclickedId == R.id.action_favourite){
            getFavouriteMovies();
            getSupportActionBar().setTitle(getString(R.string.sort_favourite));
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * This method will call cursor loader to get list favourite movies from
     * Sqlite database through content provider.
     */
    void getFavouriteMovies(){

        if(getSupportLoaderManager().getLoader(FAVOURITE_MOVIES_LOADER_ID) == null){
            getSupportLoaderManager().initLoader(FAVOURITE_MOVIES_LOADER_ID,
                    null, mFavouriteLoaderListener);
        } else{
            getSupportLoaderManager().restartLoader(FAVOURITE_MOVIES_LOADER_ID,
                    null, mFavouriteLoaderListener);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isFavSelected){
            getSupportActionBar().setTitle(getString(R.string.sort_favourite));
            getFavouriteMovies();
        }
    }
}
