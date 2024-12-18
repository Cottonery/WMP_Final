package com.example.finalexam;

public class Subject {
    private String id;
    private String name;
    private int credits;
    private boolean isSelected;

    public Subject(String id, String name, int credits) {
        this.id = id;
        this.name = name;
        this.credits = credits;
        this.isSelected = false;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCredits() {
        return credits;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

