package com.example.ananth.recyclekiosk;

public class ScoreItem {
    private String title;
    private int score;

    public ScoreItem(String title, int score) {
        this.title = title;
        this.score = score;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ScoreItem){
            return ((ScoreItem) obj).title.equals(title);
        }
        else{
            return false;
        }
    }
}
