package vakoze.blomidtech.vakoze.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.List;

import vakoze.blomidtech.vakoze.BoxActivity;
import vakoze.blomidtech.vakoze.R;
import vakoze.blomidtech.vakoze.models.Video;

/**
 * Created by capp on 12/02/2018.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder>{


    private Context mContext;
    private List<Video> mVideos ;

    // 1

    public VideoAdapter(Context context, List<Video> videos) {
        this.mContext = context;
        this.mVideos = videos;

    }
    @Override
    public int getItemCount() {

        return mVideos.size();
    }




    @Override
    public long getItemId(int position) {
        return  position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VideoAdapter.ViewHolder holder, int position) {
        //holder.mItem = mValues.get(position);
        final Video video = mVideos.get(position);

        //viewHolder.mIdView.setText(mContext.getString(Integer.parseInt(video.getNom())));
        holder.mIdView.setText(String.valueOf(video.getId()));
        holder.mContentView.setText(video.getCategorie());
        //viewHolder.mVideoView.setVideoURI(Uri.parse("http://"+mContext.getString(Integer.parseInt(video.getSource()))));
        holder.mVideoView.setVideoURI(Uri.parse("http://"+video.getSource()));
        holder.mVideoView.seekTo(100);
        holder.mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });
        holder.mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                holder.progressBar.setVisibility(View.GONE);
            }
        });




    }

    public class ViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView mIdView;
        private final TextView mContentView;
        private final VideoView mVideoView;
        private String mItem;

        private ProgressBar progressBar;


        public void setItem(String item) {
            mItem = item;
            //mTextView.setText(item);
        }

        private ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mVideoView = view.findViewById(R.id.videoTimeline);
            progressBar = (ProgressBar) view.findViewById(R.id.public_video_progress);


        }



        @Override
        public void onClick(View v) {




            Intent i = new Intent(v.getContext(),BoxActivity.class);
            i.putExtra("source", mVideos.get(getPosition()).getSource());
            i.putExtra("userId", String.valueOf(mVideos.get(getPosition()).getUser_id()));
            i.putExtra("nom", mVideos.get(getPosition()).getNom());
            i.putExtra("tags", mVideos.get(getPosition()).getTags());
            i.putExtra("categorie", mVideos.get(getPosition()).getCategorie());
            i.putExtra("id", String.valueOf(mVideos.get(getPosition()).getId()));
            i.putExtra("type", mVideos.get(getPosition()).getType());


            v.getContext().startActivity(i);

        }
    }


}
