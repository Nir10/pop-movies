package com.niranjan.popmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    int id;
    String title;
    String posterPath;
    String overview;
    String averageRating;
    String releaseDate;


    public Movie(int mId, String mTitle, String mPosterPath, String mOverview, String mAverageRating, String mReleaseDate) {

        this.id = mId;
        this.title = mTitle;
        this.posterPath = mPosterPath;
        this.overview = mOverview;
        this.averageRating = mAverageRating;
        this.releaseDate = mReleaseDate;
    }

    void setId(int mId) {
        this.id = mId;
    }

    int getId() {
        return this.id;
    }

    void setTitle(String mTitle) {
        this.title = mTitle;
    }

    String getTitle() {
        return this.title;
    }

    void setPosterPath(String mPosterPath) {
        this.posterPath = mPosterPath;
    }

    String getPosterPath() {
        return posterPath;
    }

    void setOverview(String mOverview) {
        this.overview = mOverview;
    }

    String getOverview() {
        return this.overview;
    }

    void setAverageRating(String mAverageRating) {
        this.averageRating = mAverageRating;
    }

    String getAverageRating() {
        return this.averageRating;
    }

    void setReleaseDate(String mReleaseDate) {
        this.releaseDate = mReleaseDate;
    }

    String getReleaseDate() {
        return this.releaseDate;
    }


    private Movie(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.posterPath = in.readString();
        this.overview = in.readString();
        this.averageRating = in.readString();
        this.releaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.posterPath);
        dest.writeString(this.overview);
        dest.writeString(this.averageRating);
        dest.writeString(this.releaseDate);
    }

    @Override
    public String toString() {
        return this.id+"--"+this.title+"--"+this.posterPath+"--"+this.overview+"--"+this.averageRating
                +"--"+this.releaseDate;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

}
