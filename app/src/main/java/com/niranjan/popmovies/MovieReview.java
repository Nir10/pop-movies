package com.niranjan.popmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieReview implements Parcelable{

    String mReviewId;
    String mAuthor;
    String mContent;
    String mUrl;

    public MovieReview(String reviewId,String author,String content,String url){
        mReviewId = reviewId;
        mAuthor = author;
        mContent = content;
        mUrl = url;
    }

    private MovieReview(Parcel in){
        this.mReviewId = in.readString();
        this.mAuthor = in.readString();
        this.mContent = in.readString();
        this.mUrl = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mReviewId);
        dest.writeString(this.mAuthor);
        dest.writeString(this.mContent);
        dest.writeString(this.mUrl);
    }

    @Override
    public String toString() {
        return this.mReviewId+"--"+this.mAuthor+"--"+this.mContent+"--"+this.mUrl;
    }

    public static final Creator<MovieReview> CREATOR = new Creator<MovieReview>() {
        @Override
        public MovieReview createFromParcel(Parcel source) {
            return new MovieReview(source);
        }

        @Override
        public MovieReview[] newArray(int size) {
            return new MovieReview[size];
        }
    };
}
