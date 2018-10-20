package com.example.ananth.recyclekiosk;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {
    private List<ScoreItem> mDataset;
    public ScoreAdapter() {
        mDataset = new ArrayList<>();
    }
    public ScoreAdapter(List<ScoreItem> list) {
        mDataset = list;
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
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView titleTextView, scoreTextView;
        public ViewHolder(View v){
            super(v);
            titleTextView = v.findViewById(R.id.title);
            scoreTextView = v.findViewById(R.id.score);
        }
    }

}
