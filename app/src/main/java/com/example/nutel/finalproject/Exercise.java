package com.example.nutel.finalproject;

public class Exercise {
    private String name,description;
    private boolean isDone;

    public Exercise(String name, String description){
        this.name = name;
        this.description = description;
        this.isDone=false;
    }
    public Exercise(String description){
        this.description=description;
    }
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }


    public boolean isDone() {
        return isDone;
    }

    public void toggleIsDone() {
        isDone = !isDone;
    }
    public void setNameAndDescription(String name, String description ){
        this.name = name;
        this.description = description;

    }

}
