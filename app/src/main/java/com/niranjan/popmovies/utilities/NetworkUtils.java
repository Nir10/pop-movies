package com.niranjan.popmovies.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the themoviedb.org servers.
 */

public class NetworkUtils {

    //Base url used to connect to themoviedb.org server
    final static String MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3";

    //Base url end used to connect to themoviedb.org server to fetch movie reviews and trailers
    final static String MOVIE_DB_BASE_URL_END = "/movie";

    //base url used to fetch movie posters from themoviedb.org server
    final public static String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w342/";

    //End Point to fetch list of POPULAR movies from themoviedb.org server
    final static String POPULAR_MOVIES = "/movie/popular";

    //End Point to fetch list of TOP RATED movies from themoviedb.org server
    final static String TOP_RATED = "/movie/top_rated";

    final static String MOVIE_TRAILERS = "/videos";

    final static String MOVIE_REVIEWS = "/reviews";

    //API key used to connect to themoviedb.org server
    final static String PARAM_API_KEY = "api_key";
    final static String API_KEY = "themoviedb.org API KEY";

    
    final static String PARAM_LANGUAGE = "language";
    final static String LANGUAGE = "en-US";


    /**
     * Builds the URL used to talk to the themoviedb.org server using API key and language.
     * The URL fetches list of POPULAR MOVIES from themoviedb.org server.
     * @return The URL to use to query the themoviedb.org server.
     */
    public static URL buildPopularMoviesURL(){

        Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL+POPULAR_MOVIES).buildUpon()
                        .appendQueryParameter(PARAM_API_KEY,API_KEY)
                        .appendQueryParameter(PARAM_LANGUAGE,LANGUAGE)
                        .build();

           URL url = null;

           try{
               url = new URL(builtUri.toString());
           } catch(MalformedURLException e){
                e.printStackTrace();
           }
        return url;
    }


    /**
     * Builds the URL used to talk to the themoviedb.org server using API key.
     * The URL fetches list of TOP RATED Movies from themoviedb.org server.
     * @return The URL to use to query the themoviedb.org server.
     */
    public static URL buildTopRatedMoviesURL(){

        Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL+TOP_RATED).buildUpon()
                        .appendQueryParameter(PARAM_API_KEY,API_KEY)
                        .appendQueryParameter(PARAM_LANGUAGE,LANGUAGE)
                        .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch(MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }


    /**
     * Builds the URL used to talk to the themoviedb.org server using API key.
     * The URL fetches list of MOVIE TRAILERS from themoviedb.org server based on Movie id.
     * @return The URL to use to query the themoviedb.org server.
     */
    public static URL buildMovieTrailersURL(int id){

        Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL+MOVIE_DB_BASE_URL_END+"/"+id+MOVIE_TRAILERS).buildUpon()
                .appendQueryParameter(PARAM_API_KEY,API_KEY)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch(MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Builds the URL used to talk to the themoviedb.org server using API key.
     * The URL fetches list of MOVIE Reviews from themoviedb.org server based on Movie id.
     * @return The URL to use to query the themoviedb.org server.
     */
    public static URL buildMovieReviewsURL(int id){

        Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL+MOVIE_DB_BASE_URL_END+"/"+id+MOVIE_REVIEWS).buildUpon()
                .appendQueryParameter(PARAM_API_KEY,API_KEY)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch(MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if(hasInput){
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
