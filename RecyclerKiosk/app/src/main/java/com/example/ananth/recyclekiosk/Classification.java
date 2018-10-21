package com.example.ananth.recyclekiosk;

public class Classification {
    private int points;
    private String classification;

    public Classification(String classification, int points) {
        this.points = points;
        this.classification = classification;
    }

    public int getPoints() {

        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }
}
