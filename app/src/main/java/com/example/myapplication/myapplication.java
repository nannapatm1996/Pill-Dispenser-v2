package com.example.myapplication;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class myapplication extends Application {

    public void onCreate(){
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }



}
