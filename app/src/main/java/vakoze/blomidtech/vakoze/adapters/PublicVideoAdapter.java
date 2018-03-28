package vakoze.blomidtech.vakoze.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import vakoze.blomidtech.vakoze.PublicBoxActivity;
import vakoze.blomidtech.vakoze.R;
import vakoze.blomidtech.vakoze.models.Video;

/**
 * Created by capp on 12/02/2018.
 */

public class PublicVideoAdapter extends RecyclerView.Adapter<PublicVideoAdapter.ViewHolder> implements Filterable{


    private Context mContext;
    private List<Video> mVideos ;
    List<Video> videoFiltered;
    private PublicVideoAdapterListener listener;

    CustomFilter filter;


    public class ViewHolder  extends RecyclerView.ViewHolder{

        private final TextView mIdView;
        private final TextView mContentView;
        private final VideoView mVideoView;
        private ProgressBar progressBar;
        private RelativeLayout box;



        private ViewHolder(View view) {
            super(view);

            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.like);
            mVideoView = view.findViewById(R.id.videoTimeline);
            progressBar = (ProgressBar) view.findViewById(R.id.public_video_progress);
            box = view.findViewById(R.id.video_list_fragment);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onVideoSelected(mVideos.get(getAdapterPosition()));


                    Intent i = new Intent(v.getContext(),PublicBoxActivity.class);
                    i.putExtra("source", mVideos.get(getPosition()).getSource());
                    i.putExtra("userId", String.valueOf(mVideos.get(getPosition()).getUser_id()));
                    i.putExtra("nom", mVideos.get(getPosition()).getNom());
                    i.putExtra("tags", mVideos.get(getPosition()).getTags());
                    i.putExtra("categorie", mVideos.get(getPosition()).getCategorie());

                    i.putExtra("description", mVideos.get(getPosition()).getDescription());
                    i.putExtra("id", String.valueOf(mVideos.get(getPosition()).getId()));
                    i.putExtra("type", mVideos.get(getPosition()).getType());


                    v.getContext().startActivity(i);

                }
            });

        }



        /*
        @Override
        public void onClick(View v) {

            listener.onVideoSelected(videoFiltered.get(getAdapterPosition()));


            Intent i = new Intent(v.getContext(),PublicBoxActivity.class);
            i.putExtra("source", mVideos.get(getPosition()).getSource());
            i.putExtra("userId", String.valueOf(mVideos.get(getPosition()).getUser_id()));
            i.putExtra("nom", mVideos.get(getPosition()).getNom());
            i.putExtra("tags", mVideos.get(getPosition()).getTags());
            i.putExtra("categorie", mVideos.get(getPosition()).getCategorie());
            i.putExtra("id", String.valueOf(mVideos.get(getPosition()).getId()));
            i.putExtra("type", mVideos.get(getPosition()).getType());


            v.getContext().startActivity(i);

        }
        */

    }

    public PublicVideoAdapter(Context context, List<Video> mVideos, List<Video> videosFiltered) {
        //this.mContext = context;
        //this.listener = listener;
        this.mVideos = mVideos;
        videoFiltered = mVideos;
        //thvideoFiltered  = new ArrayList<Video>();
        //this.videoFiltered .addAll(videosFiltered);

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PublicVideoAdapter.ViewHolder holder, final int position) {
        //holder.mItem = mValues.get(position);
        //final Video video = mVideos.get(position);
        final Video video = mVideos.get(position);

        //viewHolder.mIdView.setText(mContext.getString(Integer.parseInt(video.getNom())));
        //holder.mIdView.setText(String.valueOf(video.getId()));
        holder.mIdView.setText(video.getNom());
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


        holder.box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),PublicBoxActivity.class);
                i.putExtra("source", mVideos.get(position).getSource());
                i.putExtra("userId", String.valueOf(mVideos.get(position).getUser_id()));
                i.putExtra("nom", mVideos.get(position).getNom());
                i.putExtra("tags", mVideos.get(position).getTags());
                i.putExtra("categorie", mVideos.get(position).getCategorie());
                i.putExtra("description", mVideos.get(position).getDescription());
                i.putExtra("id", String.valueOf(mVideos.get(position).getId()));
                i.putExtra("type", mVideos.get(position).getType());


                v.getContext().startActivity(i);

            }
        });



    }

    @Override
    public int getItemCount() {
        return mVideos.size();
        //return videoFiltered.size();
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mVideos.clear();
        if (charText.length() == 0) {
            mVideos.addAll(videoFiltered);
            this.notifyDataSetChanged();
        }
        else
        {
            for (Video video : videoFiltered)
            {
                if (video.getNom().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    mVideos.add(video);
                    this.notifyDataSetChanged();
                }
            }
            this.notifyDataSetChanged();
        }

        this.notifyDataSetChanged();

    }

    @Override
    public Filter getFilter() {
        // TODO Auto-generated method stub
        if(filter == null)
        {
            filter=new CustomFilter();
        }

        return filter;
    }

    //INNER CLASS
    class CustomFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // TODO Auto-generated method stub

            FilterResults results=new FilterResults();

            if(constraint != null && constraint.length()>0)
            {
                //CONSTARINT TO UPPER
                constraint=constraint.toString().toUpperCase();

                ArrayList<Video> filters=new ArrayList<Video>();

                //get specific items
                for(int i=0;i<videoFiltered.size();i++)
                {
                    if(videoFiltered.get(i).getNom().toUpperCase().contains(constraint))
                    {
                        Video p=new Video();
                        p.setId(videoFiltered.get(i).getId());
                        //video.setDate_ajout(Date.parse(objData.getString("date_ajout")));
                        p.setCategorie(videoFiltered.get(i).getCategorie());
                        p.setDescription(videoFiltered.get(i).getDescription());
                        p.setNom(videoFiltered.get(i).getNom());
                        p.setTags(videoFiltered.get(i).getTags());
                        p.setType(videoFiltered.get(i).getType());
                        p.setSource(videoFiltered.get(i).getSource());
                        p.setUser_id(videoFiltered.get(i).getUser_id());
                        filters.add(p);
                    }
                }

                results.count=filters.size();
                results.values=filters;

            } else
            {
                results.count=videoFiltered.size();
                results.values=videoFiltered;

            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // TODO Auto-generated method stub

            mVideos=(List<Video>) results.values;
            notifyDataSetChanged();
        }

    }
    public void displayToast(View v,String message){
        Snackbar snackbar = Snackbar
                .make(v, message, Snackbar.LENGTH_LONG)
                /*.setAction("Reessayer", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                buttonClicked = "login";
                                attemptLogin();
                            }
                        })*/;
        // Changing message text color
        //snackbar.setActionTextColor(Color.RED);
        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    public interface PublicVideoAdapterListener {
        void onVideoSelected(Video video);
    }

}
