package com.niranjan.popmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.niranjan.popmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private static final String TAG = MoviesAdapter.class.getSimpleName();
    ArrayList<Movie> mMovieList;
    Context mContext;
    final private ListItemClickListener mOnClickListener;


    public MoviesAdapter(Context context, ListItemClickListener listener){
        mMovieList = new ArrayList<>();

        mContext = context;
        mOnClickListener = listener;

    }

    public interface ListItemClickListener {
        void onListItemClick(Movie movieItem);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();

        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflator = LayoutInflater.from(context);

        boolean shouldAttachToParentImmediately = false;

        View view = inflator.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        MovieViewHolder holder = new MovieViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

        Picasso.with(mContext).load(NetworkUtils.MOVIE_POSTER_BASE_URL+mMovieList.get(position).posterPath).into(holder.listItemMoviePoster);

    }

    @Override
    public int getItemCount() {
        if(mMovieList == null) return 0;
        else return mMovieList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView listItemMoviePoster;

        public MovieViewHolder(View itemView) {
            super(itemView);
            listItemMoviePoster = itemView.findViewById(R.id.list_item_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(mMovieList.get(clickedPosition));
        }
    }

    /**
     * This method is used to set the movies list on a MoviesAdapter if we've already
     * created one.
     * @param movieList The new movies list to be displayed.
     */
    public void setMovieList(ArrayList<Movie> movieList) {
        this.mMovieList = movieList;
        notifyDataSetChanged();
    }
}
