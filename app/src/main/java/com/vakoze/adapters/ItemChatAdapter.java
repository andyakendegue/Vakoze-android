package com.vakoze.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vakoze.R;
import com.vakoze.chatFirebase.MessengerActivity;
import com.vakoze.models.UserListChat;
import com.vakoze.models.Video;


public class ItemChatAdapter extends RecyclerView.Adapter<ItemChatAdapter.ViewHolder> implements Filterable {

    private List<UserListChat> chatList = new ArrayList<UserListChat>();
    private List<UserListChat> chatListFiltered = new ArrayList<UserListChat>();

    private Context context;
    private LayoutInflater layoutInflater;

    CustomFilter filter;

    public ItemChatAdapter(Context context, List<UserListChat> objects) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.chatList = objects;
        chatListFiltered = objects;
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }



    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //holder.mItem = mValues.get(position);
        //final Video video = mVideos.get(position);
        final UserListChat chat = chatList.get(holder.getAdapterPosition());

        //viewHolder.mIdView.setText(mContext.getString(Integer.parseInt(video.getNom())));
        //holder.mIdView.setText(String.valueOf(video.getId()));
        holder.nom.setText(chat.getNom());
        if(chat.getPhoto()!=null&&!chat.getPhoto().isEmpty()&&!chat.getPhoto().equals("")){
            Glide.with(context)
                    .load(chat.getPhoto())
                    //.fitCenter()
                    .into(holder.profileTo);
        } else {
            //profile_pic.setBackgroundResource(R.drawable.com_facebook_profile_picture_blank_square);
            //profile_pic.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);

            Glide.with(context)
                    //.load(user.getProfile_pic())
                    .load(Uri.parse("android.resource://com.vakoze/" + R.drawable.profile_pic))
                    //.fitCenter()
                    .into(holder.profileTo);
        }
        /*
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

*/
        holder.box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),MessengerActivity.class);
                i.putExtra("messengerTo", chatList.get(holder.getAdapterPosition()).getUId());
                i.putExtra("name", chatList.get(holder.getAdapterPosition()).getNom());
                i.putExtra("photo", chatList.get(holder.getAdapterPosition()).getPhoto());
                /*
                i.putExtra("source", mVideos.get(holder.getAdapterPosition()).getSource());
                i.putExtra("userId", String.valueOf(mVideos.get(holder.getAdapterPosition()).getUser_id()));
                i.putExtra("nom", mVideos.get(holder.getAdapterPosition()).getNom());
                i.putExtra("tags", mVideos.get(holder.getAdapterPosition()).getTags());
                i.putExtra("categorie", mVideos.get(holder.getAdapterPosition()).getCategorie());
                i.putExtra("description", mVideos.get(holder.getAdapterPosition()).getDescription());
                i.putExtra("id", String.valueOf(mVideos.get(holder.getAdapterPosition()).getId()));
                i.putExtra("type", mVideos.get(holder.getAdapterPosition()).getType());
                */
                v.getContext().startActivity(i);
            }
        });
    }


    public class ViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView nom;
        private final ImageView profileTo;
        private RelativeLayout box;

        private ViewHolder(View view) {
            super(view);
            nom = view.findViewById(R.id.nomUserChat);
            profileTo =  view.findViewById(R.id.profileTo);
            box = view.findViewById(R.id.chat_list_fragment);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //listener.onVideoSelected(mVideos.get(getAdapterPosition()));
                    Intent i = new Intent(v.getContext(),MessengerActivity.class);
                    i.putExtra("messengerTo", chatList.get(getPosition()).getUId());
                    /*
                    i.putExtra("source", mVideos.get(getPosition()).getSource());
                    i.putExtra("userId", String.valueOf(mVideos.get(getPosition()).getUser_id()));
                    i.putExtra("nom", mVideos.get(getPosition()).getNom());
                    i.putExtra("tags", mVideos.get(getPosition()).getTags());
                    i.putExtra("categorie", mVideos.get(getPosition()).getCategorie());
                    i.putExtra("description", mVideos.get(getPosition()).getDescription());
                    i.putExtra("id", String.valueOf(mVideos.get(getPosition()).getId()));
                    i.putExtra("type", mVideos.get(getPosition()).getType());*/
                    v.getContext().startActivity(i);
                }
            });
        }
        @Override
        public void onClick(View v) {
            Intent i = new Intent(v.getContext(),MessengerActivity.class);
            i.putExtra("messengerTo", chatList.get(getPosition()).getUId());
            /*
            i.putExtra("source", mVideos.get(getPosition()).getSource());
            i.putExtra("userId", String.valueOf(mVideos.get(getPosition()).getUser_id()));
            i.putExtra("nom", mVideos.get(getPosition()).getNom());
            i.putExtra("tags", mVideos.get(getPosition()).getTags());
            i.putExtra("categorie", mVideos.get(getPosition()).getCategorie());
            i.putExtra("id", String.valueOf(mVideos.get(getPosition()).getId()));
            i.putExtra("type", mVideos.get(getPosition()).getType());*/
            v.getContext().startActivity(i);
        }
    }


    @Override
    public Filter getFilter() {
        // TODO Auto-generated method stub
        if(filter == null)
        {
            filter=new ItemChatAdapter.CustomFilter();
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

                ArrayList<UserListChat> filters=new ArrayList<UserListChat>();

                //get specific items
                for(int i=0;i<chatListFiltered.size();i++)
                {
                    if(chatListFiltered.get(i).getNom().toUpperCase().contains(constraint))
                    {
                        UserListChat p=new UserListChat(chatListFiltered.get(i).getUId(),chatListFiltered.get(i).getNom(), chatListFiltered.get(i).getPhoto());
                        /*
                        p.setId(videoFiltered.get(i).getId());
                        //video.setDate_ajout(Date.parse(objData.getString("date_ajout")));
                        p.setCategorie(videoFiltered.get(i).getCategorie());
                        p.setDescription(videoFiltered.get(i).getDescription());
                        p.setNom(videoFiltered.get(i).getNom());
                        p.setTags(videoFiltered.get(i).getTags());
                        p.setType(videoFiltered.get(i).getType());
                        p.setSource(videoFiltered.get(i).getSource());
                        p.setUser_id(videoFiltered.get(i).getUser_id());
                        */
                        filters.add(p);
                    }
                }

                results.count=filters.size();
                results.values=filters;

            } else
            {
                results.count=chatListFiltered.size();
                results.values=chatListFiltered;

            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // TODO Auto-generated method stub

            chatList=(List<UserListChat>) results.values;
            notifyDataSetChanged();
        }

    }

    public interface VideoAdapterListener {
        void onVideoSelected(Video video);
    }
}
