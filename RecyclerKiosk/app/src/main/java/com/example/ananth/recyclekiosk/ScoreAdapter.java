package com.example.ananth.recyclekiosk;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {
    private List<ScoreItem> mDataset;
    private Context context;
    public ScoreAdapter(List<ScoreItem> list, Context context) {
        mDataset = list;
        this.context = context;
    }
    public void setData(List<ScoreItem> list){
        mDataset = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.score_item, viewGroup, false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.scoreTextView.setText(mDataset.get(i).getScore()+"");
        viewHolder.titleTextView.setText(mDataset.get(i).getTitle());
        //setAnimation(viewHolder.layout, i);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView titleTextView, scoreTextView;
        private ConstraintLayout layout;
        public ViewHolder(View v){
            super(v);
            titleTextView = v.findViewById(R.id.title);
            scoreTextView = v.findViewById(R.id.score);
            layout = (ConstraintLayout)v;
        }
    }
    private int lastPosition = -1;
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
