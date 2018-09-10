package com.niranjan.popmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MovieTrailersAdapter  extends RecyclerView.Adapter<MovieTrailersAdapter.MovieTrailerViewHolder>{

    Context mContext;
    ArrayList<MovieTrailer> mTrailersList;
    final private ListTrailerItemClickListener mOnTrailerClickListener;

    public MovieTrailersAdapter(Context context, ListTrailerItemClickListener onClickListener){
        mContext = context;
        mOnTrailerClickListener = onClickListener;
    }

    public interface ListTrailerItemClickListener{
        void onTrailerListItemClick(MovieTrailer movieTrailer);
    }

    @Override
    public MovieTrailerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.movie_trailer_list_item,viewGroup,
                false);
        MovieTrailerViewHolder viewHolder = new MovieTrailerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieTrailerViewHolder movieTrailerViewHolder,final int i) {

        if(mTrailersList != null && mTrailersList.size() > 0) {
            movieTrailerViewHolder.mTrailerNameTextView.setText(mTrailersList.get(i).mTrailerName);
            /*if(i ==(mTrailersList.size()-1)){
                movieTrailerViewHolder.mDivider.setVisibility(View.INVISIBLE);
            }*/
        }



    }

    @Override
    public int getItemCount() {
        if(mTrailersList == null )return 0;
        else return mTrailersList.size();

    }

    class MovieTrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mTrailerNameTextView;
        View mDivider;

        MovieTrailerViewHolder(View itemView){
            super(itemView);
            mTrailerNameTextView = itemView.findViewById(R.id.tv_trailer_list_item_name);
            //mDivider = itemView.findViewById(R.id.v_trailer_divider);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnTrailerClickListener.onTrailerListItemClick(mTrailersList.get(clickedPosition));
        }
    }

    /**
     * This method is used to set the movies list on a MovieTrailersAdapter if we've already
     * created one.
     * @param movieTrailerList The new movies list to be displayed.
     */
    public void setMovieTrailersList(ArrayList<MovieTrailer> movieTrailerList) {
        this.mTrailersList = movieTrailerList;
        notifyDataSetChanged();
    }

}
