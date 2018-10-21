package com.example.ananth.recyclekiosk;

import java.util.List;

public class ScorePageModel {
    private ScoreAdapter scoreAdapter;
    private String title;

    public ScoreAdapter getScoreAdapter() {
        return scoreAdapter;
    }

    public void setScoreAdapter(ScoreAdapter scoreAdapter) {
        this.scoreAdapter = scoreAdapter;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ScorePageModel(ScoreAdapter scoreAdapter, String title) {
        this.scoreAdapter = scoreAdapter;
        this.title = title;
    }
}
