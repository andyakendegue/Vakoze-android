package com.vakoze.video.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vakoze.R;


public class FragmentEffectListAdapter extends RecyclerView.Adapter<FragmentEffectListAdapter.ViewHolder>  {

    private List<String> effect = new ArrayList<String>();
    private TypedArray images = null;

    private Context context;
    private LayoutInflater layoutInflater;

    private OnItemClicked onClick;
    LinearLayout effectLayout;

    public FragmentEffectListAdapter(Context context, List<String> effect, TypedArray images) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.effect = effect;
        this.images = images;
    }





    public interface OnItemClicked {
        void onItemClick(String effect);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_effect_list, parent, false);
        ViewHolder viewHolder1 = new ViewHolder(parent,view);
        return viewHolder1;
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.effectName.setText(effect.get(position));
        holder.effectImage.setImageResource(images.getResourceId(position, -1));
        holder.effectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.effectLayout.setBackgroundColor(v.getContext().getResources().getColor(R.color.white));
                holder.effectLayout.setBackgroundColor(v.getContext().getResources().getColor(R.color.colorPrimary));
                onClick.onItemClick("capp");
            }
        });
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return effect.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView effectImage;
        TextView effectName;
        LinearLayout effectLayout;
        private SparseBooleanArray selectedItems = new SparseBooleanArray();

        ViewHolder(ViewGroup ve, View view) {
            super(view);
            view.setOnClickListener(this);
            effectImage = view.findViewById(R.id.effectImage);
            effectName = view.findViewById(R.id.effectName);
            effectLayout = view.findViewById(R.id.effectLayout);
        }
        @Override
        public void onClick(View v) {
            if (selectedItems.get(getAdapterPosition(), false)) {
                selectedItems.delete(getAdapterPosition());
                v.setSelected(false);
            }
            else {
                selectedItems.put(getAdapterPosition(), true);
                v.setSelected(true);
            }
        }
    }
    public void setOnClick(OnItemClicked onClick)
    {
        this.onClick=onClick;
    }
}
