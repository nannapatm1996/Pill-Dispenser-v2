package com.example.myapplication.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class myMedFragment extends medListFragment {

    public myMedFragment(){}

    @Override
    public Query getQuery(DatabaseReference databaseReference){
        return databaseReference.child("user-meds").child(getUid());
    }
}
