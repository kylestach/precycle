package com.example.ananth.recyclekiosk;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class PersonalScoreFragment extends Fragment {
    private List<ScoreItem> dataset;
    private String title;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.personal_score_layout, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.scoreRecyclerView);
        ScoreAdapter adapter = new ScoreAdapter(dataset,getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        TextView scoreTitle = rootView.findViewById(R.id.scoreTitle);
        scoreTitle.setText(title);
        return rootView;
    }
    public static PersonalScoreFragment create(List<ScoreItem> dataset){
        PersonalScoreFragment fragment = new PersonalScoreFragment();
        fragment.setDataset(dataset);
        return fragment;
    }

    public void setDataset(List<ScoreItem> dataset) {
        this.dataset = dataset;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
