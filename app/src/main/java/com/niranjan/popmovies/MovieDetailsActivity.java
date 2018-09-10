package com.niranjan.popmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;


import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.niranjan.popmovies.data.PopMoviesContract;
import com.niranjan.popmovies.data.PopMoviesDbHelper;
import com.niranjan.popmovies.databinding.ActivityMovieDetailsBinding;
import com.niranjan.popmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MovieDetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<String>,
        MovieTrailersAdapter.ListTrailerItemClickListener {

    private static String TAG = MovieDetailsActivity.class.getSimpleName();

    Movie movie;

    //loader id's
    private static final int TRAILER_LOADER = 1;
    private static final int REVIEWS_LOADER = 2;

    //adapter for trailers and reviews
    MovieTrailersAdapter mTrailersAdapter;
    MovieReviewsAdapter mReviewsAdapter;

    //list containing trailers and reviews
    ArrayList<MovieTrailer> movieTrailersList;
    ArrayList<MovieReview> movieReviewsList;


    boolean FAVOURITE = true;
    boolean NOT_FAVOURITE = false;
    boolean IS_FAVOURITE = false;

    boolean ARE_TRAILERS_AVAILABLE = true;
    boolean ARE_REVIEWS_AVAILABLE = true;

    SQLiteDatabase mDb;

    ActivityMovieDetailsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);

        setSupportActionBar(mBinding.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PopMoviesDbHelper popMoviesDbHelper = new PopMoviesDbHelper(this);
        mDb = popMoviesDbHelper.getWritableDatabase();

        Bundle bundle = getIntent().getExtras();

        if(bundle != null) {
            movie = (Movie) bundle.getParcelable(Intent.EXTRA_TEXT);

            String release_date = null;
            String splitArray[]= movie.releaseDate.split("-");
            release_date = splitArray[0];

            mBinding.detailDesc.tvTitle.setText(movie.title);
            mBinding.detailDesc.tvOverview.setText(movie.overview);
            mBinding.detailDesc.tvAverageRating.setText(movie.averageRating+"/10");
            mBinding.detailDesc.tvReleaseDate.setText(release_date);
            Picasso.with(this).load(NetworkUtils.MOVIE_POSTER_BASE_URL + movie.posterPath).
                    into(mBinding.detailDesc.ivPoster);

            checkFavourite();

            //set up recyclerview to display MOVIE TRAILERS
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                    LinearLayoutManager.HORIZONTAL,false);
            mBinding.detailDesc.rvMovieTrailers.setHasFixedSize(true);
            mBinding.detailDesc.rvMovieTrailers.setLayoutManager(linearLayoutManager);
            mTrailersAdapter = new MovieTrailersAdapter(this,this);
            mBinding.detailDesc.rvMovieTrailers.setAdapter(mTrailersAdapter);

            //set up recyclerview to display MOVIE REVIEWS
            mBinding.detailDesc.rvMovieReviews.setLayoutManager(new LinearLayoutManager(this));
            mBinding.detailDesc.rvMovieReviews.setHasFixedSize(true);
            mReviewsAdapter = new MovieReviewsAdapter(this,null);
            mBinding.detailDesc.rvMovieReviews.setAdapter(mReviewsAdapter);


            if(savedInstanceState != null &&
                    savedInstanceState.containsKey("movieTrailer") &&
                    savedInstanceState.containsKey("movieReview")){

                ARE_TRAILERS_AVAILABLE = savedInstanceState.getBoolean("trailerAvailabilty");
                ARE_REVIEWS_AVAILABLE = savedInstanceState.getBoolean("reviewAvailabilty");

                if(ARE_TRAILERS_AVAILABLE){
                    movieTrailersList = savedInstanceState.getParcelableArrayList("movieTrailer");
                    mTrailersAdapter.setMovieTrailersList(movieTrailersList);
                    showTrailerList();
                } else {
                    showTrailerErrorMessage();
                }

                if(ARE_REVIEWS_AVAILABLE){
                    movieReviewsList = savedInstanceState.getParcelableArrayList("movieReview");
                    mReviewsAdapter.setMovieReviewsList(movieReviewsList);
                    showReviewList();

                } else {
                    showReviewErrorMessage();
                }
            } else {
                movieTrailersList = new ArrayList<>();
                movieReviewsList = new ArrayList<>();

                Loader trailerLoader = getSupportLoaderManager().getLoader(TRAILER_LOADER);
                Loader reviewLoader = getSupportLoaderManager().getLoader(REVIEWS_LOADER);

                //fetch movie trailers
                if(trailerLoader == null) {
                    getSupportLoaderManager().initLoader(TRAILER_LOADER, null, this);
                } else {
                    getSupportLoaderManager().restartLoader(TRAILER_LOADER,null,this);
                }
                //fetch movie reviews
                if(reviewLoader == null){
                    getSupportLoaderManager().initLoader(REVIEWS_LOADER,null,this);
                } else {
                    getSupportLoaderManager().restartLoader(REVIEWS_LOADER, null, this);
                }

            }

        }

        mBinding.detailDesc.ivFavouriteMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(IS_FAVOURITE) {
                    if(removeFavourite()){
                        mBinding.detailDesc.ivFavouriteMovie.
                                setImageResource(R.drawable.ic_star_border);
                        IS_FAVOURITE = NOT_FAVOURITE;
                    }
                } else {

                    if(addFavourite() != null){
                        mBinding.detailDesc.ivFavouriteMovie.setImageResource(R.drawable.ic_star);
                        IS_FAVOURITE = FAVOURITE;
                    }
                }
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movieTrailer",movieTrailersList);
        outState.putParcelableArrayList("movieReview",movieReviewsList);
        outState.putBoolean("trailerAvailabilty",ARE_TRAILERS_AVAILABLE);
        outState.putBoolean("reviewAvailabilty",ARE_REVIEWS_AVAILABLE);

        super.onSaveInstanceState(outState);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<String> onCreateLoader(final int i,final Bundle bundle) {
        return new AsyncTaskLoader<String>(this) {

            String mTrailerStringCache = null;
            String mReviewsStringCache = null;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if(i == 1){
                    movieTrailersList.clear();
                    mBinding.detailDesc.pbTrailer.setVisibility(View.VISIBLE);
                    //trailer loader
                    if(mTrailerStringCache == null){
                        forceLoad();
                    } else {
                        deliverResult(mTrailerStringCache);
                    }

                } else if(i == 2){
                    //review loader
                    movieReviewsList.clear();
                    mBinding.detailDesc.pbReview.setVisibility(View.VISIBLE);
                    if(mReviewsStringCache == null){
                        forceLoad();
                    } else {
                        deliverResult(mReviewsStringCache);
                    }
                }
            }

            @Override
            public String loadInBackground() {
                URL url = null;

                String result = null;
                try{
                    if (i == 1) {
                        url = NetworkUtils.buildMovieTrailersURL(movie.id);
                    } else if(i == 2){
                        url = NetworkUtils.buildMovieReviewsURL(movie.id);
                    }
                    result = NetworkUtils.getResponseFromHttpUrl(url);
                } catch(IOException e){
                    e.printStackTrace();
                    return null;
                }
                return result;
                //return null;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String s) {

        int loaderId = loader.getId();
        if(s != null && !(TextUtils.isEmpty(s))){
            if(loaderId == 1){
                //Display Trailer list
                mBinding.detailDesc.pbTrailer.setVisibility(View.INVISIBLE);
                showTrailerList();
                try {

                    JSONObject jsonObject = new JSONObject(s);

                    JSONArray jsonArray = jsonObject.getJSONArray("results");

                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jObj = jsonArray.getJSONObject(i);

                        Log.d(TAG," movie Trailer title = "+jObj.getString("name"));

                        movieTrailersList.add(new MovieTrailer(jObj.getString("id"),
                                jObj.getString("name"),jObj.getString("key")
                                ));
                    }

                    mTrailersAdapter.setMovieTrailersList(movieTrailersList);
                } catch(JSONException e){
                    e.printStackTrace();
                }
            } else if(loaderId == 2){ //Display Review List
                mBinding.detailDesc.pbReview.setVisibility(View.INVISIBLE);
                showReviewList();
                try {

                    JSONObject jsonObject = new JSONObject(s);

                    JSONArray jsonArray = jsonObject.getJSONArray("results");

                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jObj = jsonArray.getJSONObject(i);

                        Log.d(TAG," movie Review Author = "+jObj.getString("author"));

                        movieReviewsList.add(new MovieReview(jObj.getString("id"),
                                jObj.getString("author"),
                                jObj.getString("content"),
                                jObj.getString("url")
                        ));
                    }

                    //showMoviesListView();
                    mReviewsAdapter.setMovieReviewsList(movieReviewsList);
                } catch(JSONException e){
                    e.printStackTrace();
                }
            }

        } else {
            //show trailers/ review not found
            if(loaderId == 1){
                //Log.d(TAG,"Trailer Json Not Found");
                mBinding.detailDesc.pbTrailer.setVisibility(View.INVISIBLE);
                showTrailerErrorMessage();
            } else if(loaderId == 2){
                //Log.d(TAG,"Review Json Not Found");
                mBinding.detailDesc.pbReview.setVisibility(View.INVISIBLE);
                showReviewErrorMessage();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @Override
    public void onTrailerListItemClick(MovieTrailer movieTrailerClicked) {

        watchYoutubeVideo(this,movieTrailerClicked.mTrailerKey);

    }


    /**
     * This method will open movies trailer on youtube or web browser
     *@params Context and youtube video key
     */
    public static void watchYoutubeVideo(Context context, String key){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + key));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    /**
     * This method will add current movie from favorite database
     *@return Uri to check no of rows inserted into database
     */
    public Uri addFavourite(){
        ContentValues cv = new ContentValues();

        cv.put(PopMoviesContract.FavouritMoviesEntry._ID,movie.id);
        cv.put(PopMoviesContract.FavouritMoviesEntry.COLUMN_TITLE,movie.title);
        cv.put(PopMoviesContract.FavouritMoviesEntry.COLUMN_MOVIE_POSTER,movie.posterPath);
        cv.put(PopMoviesContract.FavouritMoviesEntry.COLUMN_SYNOPSIS,movie.overview);
        cv.put(PopMoviesContract.FavouritMoviesEntry.COLUMN_RELEASE_DATE,movie.releaseDate);
        cv.put(PopMoviesContract.FavouritMoviesEntry.COLUMN_USER_RATING,movie.averageRating);

        Uri uri = getContentResolver().insert(PopMoviesContract.FavouritMoviesEntry.CONTENT_URI,cv);
        return uri;

    }



    /**
     * This method will remove current movie from favorite database.
     *@return boolean to verify whether movie is successfully deleted or not
     */
    public boolean removeFavourite(){

        String id = Integer.toString(movie.id);
        Uri uri = PopMoviesContract.FavouritMoviesEntry.CONTENT_URI;

        uri = uri.buildUpon().appendPath(id).build();
        int favouriteDeleted;
        favouriteDeleted = getContentResolver().delete(uri,null,null);
        if(favouriteDeleted !=0 ){
            return true; //movie successfull deleted from database
        } else {
            return false;//movie not deleted from database
        }

    }


    /**
     * This method will check whether current movie is a favourite or not
     *
     */
    public void checkFavourite(){

        String id = Integer.toString(movie.id);
        Uri uri = PopMoviesContract.FavouritMoviesEntry.CONTENT_URI;

        uri = uri.buildUpon().appendPath(id).build();
        Cursor cursor;
        cursor = getContentResolver().query(uri,
                null,
                null,
                null,
                null);

        if(cursor.getCount() > 0){
            IS_FAVOURITE = FAVOURITE;
            mBinding.detailDesc.ivFavouriteMovie.setImageResource(R.drawable.ic_star);
        } else {
            IS_FAVOURITE = NOT_FAVOURITE;
            mBinding.detailDesc.ivFavouriteMovie.setImageResource(R.drawable.ic_star_border);
        }
    }


    /**
     * This method error message when trailers are not available
     *
     */
    public void showTrailerErrorMessage(){
        mBinding.detailDesc.tvTrailerErrorMessage.setVisibility(View.VISIBLE);
        mBinding.detailDesc.tvTrailerLabel.setVisibility(View.INVISIBLE);
        mBinding.detailDesc.rvMovieTrailers.setVisibility(View.INVISIBLE);
        ARE_TRAILERS_AVAILABLE = false;
    }

    /**
     * This method error message when reviews are not available
     *
     */
    public void showReviewErrorMessage(){
        mBinding.detailDesc.tvReviewErrorMessage.setVisibility(View.VISIBLE);
        mBinding.detailDesc.tvReviewLabel.setVisibility(View.INVISIBLE);
        mBinding.detailDesc.rvMovieReviews.setVisibility(View.INVISIBLE);
        ARE_REVIEWS_AVAILABLE = false;
    }

    /**
     * This method shows trailers list
     *
     */
    public void showTrailerList(){
        mBinding.detailDesc.tvTrailerErrorMessage.setVisibility(View.INVISIBLE);
        mBinding.detailDesc.tvTrailerLabel.setVisibility(View.VISIBLE);
        mBinding.detailDesc.rvMovieTrailers.setVisibility(View.VISIBLE);
        ARE_TRAILERS_AVAILABLE = true;

    }

    /**
     * This method shows reviews list
     *
     */

    public void showReviewList(){
        mBinding.detailDesc.tvReviewErrorMessage.setVisibility(View.INVISIBLE);
        mBinding.detailDesc.tvReviewLabel.setVisibility(View.VISIBLE);
        mBinding.detailDesc.rvMovieReviews.setVisibility(View.VISIBLE);
        ARE_REVIEWS_AVAILABLE = true;
    }
}

