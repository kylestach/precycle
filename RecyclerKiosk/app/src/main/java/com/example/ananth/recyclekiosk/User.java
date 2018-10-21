package com.example.ananth.recyclekiosk;

import java.util.List;

public class User {
    private String name, email, school, major, id;
    private List<ScoreItem> scoreItems;

    public List<ScoreItem> getScoreItems() {
        return scoreItems;
    }

    public void setScoreItems(List<ScoreItem> scoreItems) {
        this.scoreItems = scoreItems;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }
}
