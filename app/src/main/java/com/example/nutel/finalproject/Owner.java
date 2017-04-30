package com.example.nutel.finalproject;

import com.backendless.BackendlessUser;

public class Owner {
    //
    private BackendlessUser owner;
    private static Owner ourInstance = new Owner();

    public static Owner getInstance() {

        return ourInstance;
    }

    public Owner() {}

    public void setBackendlessUser(BackendlessUser user){
        this.owner = user;
    }

    public BackendlessUser getBackendlessUser(){
        return this.owner;
    }

}
