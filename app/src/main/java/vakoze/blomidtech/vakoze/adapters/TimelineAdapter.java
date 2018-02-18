package vakoze.blomidtech.vakoze.adapters;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.List;

import vakoze.blomidtech.vakoze.R;
import vakoze.blomidtech.vakoze.models.Video;

/**
 * Created by capp on 14/02/2018.
 */

public class TimelineAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Video> videoItems;

    public TimelineAdapter(Activity activity, List<Video> videoItems) {
        this.activity = activity;
        this.videoItems = videoItems;
    }

    @Override
    public int getCount() {
        return videoItems.size();
    }

    @Override
    public Object getItem(int position) {
        return videoItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.fragment_video, null);
        final TextView mIdView;
        final TextView mContentView;
        final VideoView mVideoView;
        mIdView = (TextView) convertView.findViewById(R.id.id);
        mContentView = (TextView) convertView.findViewById(R.id.content);
        mVideoView = convertView.findViewById(R.id.videoTimeline);
        final Video video = videoItems.get(position);

        //viewHolder.mIdView.setText(mContext.getString(Integer.parseInt(video.getNom())));
        mIdView.setText(video.getNom());
        mContentView.setText(video.getCategorie());
        //viewHolder.mVideoView.setVideoURI(Uri.parse("http://"+mContext.getString(Integer.parseInt(video.getSource()))));
        mVideoView.setVideoURI(Uri.parse("http://"+video.getSource()));
        mVideoView.seekTo(100);

        return convertView;
    }
}
