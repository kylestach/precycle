package com.example.ananth.recyclekiosk;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class ScoreListAdapter extends RecyclerView.Adapter<ScoreListAdapter.ViewHolder> {
    private List<ScorePageModel> mDataset;
    private Context context;
    public ScoreListAdapter(List<ScorePageModel> list, Context context) {
        mDataset = list;
        this.context = context;
    }
    public void setData(List<ScorePageModel> list){
        mDataset = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CardView layout = (CardView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.personal_score_layout, viewGroup, false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.scoreRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        viewHolder.scoreRecyclerView.setAdapter(mDataset.get(i).getScoreAdapter());
        viewHolder.titleTextView.setText(mDataset.get(i).getTitle());
        //setAnimation(viewHolder.layout, i);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView titleTextView;
        public  RecyclerView scoreRecyclerView;
        public ProgressBar progressBar;
        public CardView layout;
        public ViewHolder(View v){
            super(v);
            titleTextView = v.findViewById(R.id.scoreTitle);
            scoreRecyclerView = v.findViewById(R.id.scoreRecyclerView);
            progressBar = v.findViewById(R.id.scoreProgressBar);
            layout = (CardView)v;
        }
    }

}
