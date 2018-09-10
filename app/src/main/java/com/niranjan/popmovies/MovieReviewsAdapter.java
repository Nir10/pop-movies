package com.niranjan.popmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewViewHolder>{

    private static final String TAG = MovieReviewsAdapter.class.getSimpleName();
    ArrayList<MovieReview> mMovieReviewList;
    Context mContext;
    final private MovieReviewsAdapter.ListReviewItemClickListener mOnClickListener;

    public MovieReviewsAdapter(Context context, ListReviewItemClickListener listener){
        mMovieReviewList = new ArrayList<>();
        mContext = context;
        mOnClickListener = listener;
    }

    @Override
    public MovieReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();

        int layoutIdForListItem = R.layout.movie_review_list_item;
        LayoutInflater inflator = LayoutInflater.from(context);

        boolean shouldAttachToParentImmediately = false;

        View view = inflator.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        MovieReviewViewHolder holder = new MovieReviewViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MovieReviewViewHolder movieReviewViewHolder, int i) {

        if(mMovieReviewList !=null && mMovieReviewList.size() > 0) {
            movieReviewViewHolder.mAuthorDisplayTextView.setText(mMovieReviewList.get(i).mAuthor);
            movieReviewViewHolder.mContentDisplayTextView.setText(mMovieReviewList.get(i).mContent);

        }
    }

    @Override
    public int getItemCount() {
        if(mMovieReviewList == null) return 0;
        else return mMovieReviewList.size();

    }


    public interface ListReviewItemClickListener {
        void onListReviewItemClick(MovieReview movieReviewItem);
    }

    class MovieReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mAuthorDisplayTextView;
        TextView mContentDisplayTextView;

        public MovieReviewViewHolder(View v){
            super(v);

            mAuthorDisplayTextView = v.findViewById(R.id.tv_review_list_item_author);
            mContentDisplayTextView = v.findViewById(R.id.tv_review_list_item_content);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListReviewItemClick(mMovieReviewList.get(clickedPosition));
        }
    }

    /**
     * This method is used to set the movie reviews list on a MoviesAdapter if we've already
     * created one.
     * @param movieReviewList The new movie reviews list to be displayed.
     */
    public void setMovieReviewsList(ArrayList<MovieReview> movieReviewList) {
        this.mMovieReviewList = movieReviewList;
        notifyDataSetChanged();
    }
}
