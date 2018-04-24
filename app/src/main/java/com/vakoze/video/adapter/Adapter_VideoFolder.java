package com.vakoze.video.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.vakoze.R;
import com.vakoze.video.video_gallery.Activity_galleryView;
import com.vakoze.video.video_gallery.Model_Video;


import java.util.ArrayList;

public class Adapter_VideoFolder extends RecyclerView.Adapter<Adapter_VideoFolder.ViewHolder> {
    ArrayList<Model_Video> al_video;
    Context context;
    Activity activity;
    //declare interface
    private OnItemClicked onClick;

    public Adapter_VideoFolder(Context context, ArrayList<Model_Video> al_video, Activity activity) {

        this.al_video = al_video;
        this.context = context;
        this.activity = activity;
    }

    public interface OnItemClicked {
        void onItemClick(Model_Video video);
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_image;
        public VideoView thmbnail1,thmbnail2, thmbnail3, thmbnail4, thmbnail5, thmbnail6;
        RelativeLayout rl_select;

        public ViewHolder(ViewGroup ve, View v) {

            super(v);
            thmbnail1  = (VideoView) ve.findViewById(R.id.thumbnail1);
            thmbnail2  = (VideoView) ve.findViewById(R.id.thumbnail2);
            thmbnail3 = (VideoView) ve.findViewById(R.id.thumbnail3);
            thmbnail4  = (VideoView) ve.findViewById(R.id.thumbnail4);

            thmbnail5  = (VideoView) ve.findViewById(R.id.thumbnail5);

            thmbnail6  = (VideoView) ve.findViewById(R.id.thumbnail6);


            iv_image = (ImageView) v.findViewById(R.id.iv_image);
            rl_select = (RelativeLayout) v.findViewById(R.id.rl_select);



        }
    }

    @Override
    public Adapter_VideoFolder.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {



        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_videos, parent, false);

        ViewHolder viewHolder1 = new ViewHolder(parent,view);


        return viewHolder1;
    }




    @Override
    public void onBindViewHolder(final ViewHolder Vholder, final int position) {




        Glide.with(context).load("file://"+ al_video.get(position).getStr_thumb())
                .into(Vholder.iv_image);
        Vholder.rl_select.setBackgroundColor(Color.parseColor("#FFFFFF"));
        Vholder.rl_select.setAlpha(0);




        Vholder.rl_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_gallery = new Intent(context,Activity_galleryView.class);
                intent_gallery.putExtra("video",al_video.get(position).getStr_path());
                //activity.startActivity(intent_gallery);
                onClick.onItemClick(al_video.get(position));
                /*
                Vholder.thmbnail1.setVideoURI(Uri.parse("file://"+al_video.get(position).getStr_path()));
                Vholder.thmbnail1.seekTo(100);
                Vholder.thmbnail2.setVideoURI(Uri.parse("file://"+al_video.get(position).getStr_path()));
                Vholder.thmbnail2.seekTo(500);
                Vholder.thmbnail3.setVideoURI(Uri.parse("file://"+al_video.get(position).getStr_path()));
                Vholder.thmbnail3.seekTo(1000);
                Vholder.thmbnail4.setVideoURI(Uri.parse("file://"+al_video.get(position).getStr_path()));
                Vholder.thmbnail4.seekTo(1500);
                Vholder.thmbnail5.setVideoURI(Uri.parse("file://"+al_video.get(position).getStr_path()));
                Vholder.thmbnail5.seekTo(1500);
                Vholder.thmbnail6.setVideoURI(Uri.parse("file://"+al_video.get(position).getStr_path()));
                Vholder.thmbnail6.seekTo(1500);

                //MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
                retriever.setDataSource(al_video.get(position).getStr_path());
                Toast.makeText(context, "selected"+retriever.getMetadata().getAll(), Toast.LENGTH_SHORT).show();
                */

/*
                try {


                    Bitmap myBitmap = Bitmap.createBitmap(retriever.getFrameAtTime(10000,FFmpegMediaMetadataRetriever.OPTION_CLOSEST_SYNC));
                    Drawable drawable = new BitmapDrawable(context.getResources(), myBitmap);
                    Vholder.thmbnail1.setImageDrawable(drawable );

                    Toast.makeText(context, "selected"+al_video.get(position).getStr_path(), Toast.LENGTH_LONG).show();
                    //Toast.makeText(this, "selected", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Log.i("MyDebugCode", "MediaMetadataRetriever got exception:" + ex + al_video.get(position).getStr_path());
                }
                */


            }
        });
    }

    @Override
    public int getItemCount() {
        return al_video.size();
    }


    public void setOnClick(OnItemClicked onClick)
    {
        this.onClick=onClick;
    }

}
