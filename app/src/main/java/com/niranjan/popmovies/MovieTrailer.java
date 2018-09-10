package com.niranjan.popmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieTrailer implements Parcelable{

     String mTrailerId;
     String mTrailerName;
     String mTrailerKey;

    public MovieTrailer(String trailerId, String trailerName, String trailerKey){
        mTrailerId = trailerId;
        mTrailerName = trailerName;
        mTrailerKey = trailerKey;
    }

    private MovieTrailer(Parcel in) {
        this.mTrailerId = in.readString();
        this.mTrailerName = in.readString();
        this.mTrailerKey = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mTrailerId);
        dest.writeString(this.mTrailerName);
        dest.writeString(this.mTrailerKey);
    }

    @Override
    public String toString() {
        return this.mTrailerId+"--"+this.mTrailerName+"--"+this.mTrailerKey;
    }

    public static final Creator<MovieTrailer> CREATOR = new Creator<MovieTrailer>() {
        @Override
        public MovieTrailer createFromParcel(Parcel source) {
            return new MovieTrailer(source);
        }

        @Override
        public MovieTrailer[] newArray(int size) {
            return new MovieTrailer[size];
        }
    };

}
