package vakoze.blomidtech.vakoze.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import vakoze.blomidtech.vakoze.R;
import vakoze.blomidtech.vakoze.models.Video;
import vakoze.blomidtech.vakoze.videoFragment.OnListFragmentInteractionListener;

import java.util.Collections;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyvideoRecyclerViewAdapter extends RecyclerView.Adapter<MyvideoRecyclerViewAdapter.ViewHolder> {

    private List<Video> mValues = Collections.emptyList();
    private final Context context;

    //private final OnListFragmentInteractionListener mListener;
/*
    public MyvideoRecyclerViewAdapter(List<Video> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }
    */
    public MyvideoRecyclerViewAdapter(List<Video> mValues, Context context) {
        this.mValues = mValues;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getNom());
        holder.mContentView.setText(mValues.get(position).getCategorie());
        holder.mVideoView.setVideoURI(Uri.parse("http://"+mValues.get(position).getSource()));
        holder.mVideoView.seekTo(100);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    //mListener.onListFragmentInteraction(holder.mItem);
                }
                */
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final VideoView mVideoView;
        //public Video mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mVideoView = view.findViewById(R.id.videoTimeline);
        }


    }
}
