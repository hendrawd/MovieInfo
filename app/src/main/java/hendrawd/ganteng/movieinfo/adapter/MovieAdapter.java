package hendrawd.ganteng.movieinfo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import hendrawd.ganteng.movieinfo.R;
import hendrawd.ganteng.movieinfo.activity.DetailActivity;
import hendrawd.ganteng.movieinfo.activity.MainActivity;
import hendrawd.ganteng.movieinfo.network.UrlComposer;
import hendrawd.ganteng.movieinfo.network.response.Movie;
import hendrawd.ganteng.movieinfo.view.AutoFitImageView;

/**
 * @author hendrawd on 11/17/16
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private List<Movie> mData;
    private final Object mLock = new Object();

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ViewGroup mainContent;
        final AutoFitImageView mImageView;
        final TextView mTextView;

        ViewHolder(View view) {
            super(view);
            mainContent = (ViewGroup) view.findViewById(R.id.main_content);
            mImageView = (AutoFitImageView) view.findViewById(R.id.image_view);
            mTextView = (TextView) view.findViewById(R.id.text_view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTextView.getText();
        }

        void clearAnimation() {
            mainContent.clearAnimation();
        }
    }

    public Movie getValueAt(int position) {
        return mData.get(position);
    }

    public MovieAdapter(List<Movie> movieList) {
        mData = movieList;
    }

    public List<Movie> getData() {
        return this.mData;
    }

    public void set(List<Movie> dataToSet) {
        synchronized (mLock) {
            mData = dataToSet;
            notifyDataSetChanged();
        }
    }

    public void append(List<Movie> dataToAppend) {
        synchronized (mLock) {
            int firstPosition = mData.size();
            mData.addAll(dataToAppend);
            notifyItemRangeChanged(firstPosition, dataToAppend.size());
            //notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.mTextView.setSelected(true);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Movie movie = getValueAt(position);

        holder.mTextView.setText(movie.getTitle());

        //use glide
        String path = movie.getPoster_path();
        if (TextUtils.isEmpty(path)) {
            holder.mImageView.setImageResource(R.drawable.error_portrait);
//            Glide.with(holder.mImageView.getContext()).load(R.drawable.error_portrait);
        } else {
            Glide.with(holder.mImageView.getContext())
                    .load(UrlComposer.getPosterUrl(path))
                    .placeholder(R.drawable.placeholder_portrait)
                    .error(R.drawable.error_portrait)
                    .into(holder.mImageView);
        }

        holder.mainContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                if (context instanceof MainActivity) {
                    if (context.getResources().getBoolean(R.bool.isTablet)) {
                        ((MainActivity) context).changeDetailFragment(movie);
                    } else {
                        ((MainActivity) context).openDetailActivity(movie);
                    }
                } else {
                    Intent openDetailIntent = new Intent(context, DetailActivity.class);
                    openDetailIntent.putExtra("movie data", movie);
                    context.startActivity(openDetailIntent);
                }
                //Util.startActivityWithSharedElementTransitionIfPossible(context, openDetailIntent, holder.mTextView);
            }
        });

        //custom animation
        //setAnimation(holder.mainContent, position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        //holder.clearAnimation();
    }

    private int lastPosition = -1;

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}