package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapplication.Model.Medicine;
import com.example.myapplication.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class NewMedicineActivity extends BaseActivity{
    private static final String TAG = "NewMedicineActivity";
    private static final String REQUIRED = "Required";

    private DatabaseReference mDatabase;

    private EditText medName;
    private EditText medDesc;
    private EditText medRecom;
    private Button medSubmit;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_medicine);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        medName = findViewById(R.id.edit_medicine_name);
        medDesc = findViewById(R.id.edit_medicine_desc);
        medRecom = findViewById(R.id.edit_medicine_recommendation);
        medSubmit = findViewById(R.id.btn_submit);

        medSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });


    }


    private void submitPost(){
        final String desc = medDesc.getText().toString();
        final String name = medName.getText().toString();
        final String recom = medRecom.getText().toString();

        if(TextUtils.isEmpty(name)){
            medName.setError(REQUIRED);
            return;
        }
        setEditingEnabled(false);
        Toast.makeText(this,"Posting Med...",Toast.LENGTH_SHORT).show();

        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewMedicineActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            writeNewPost(userId, user.username,name, desc,recom);
                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
                        finish();
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        setEditingEnabled(true);
                        // [END_EXCLUDE]

                    }
                }
        );

    }
    private void setEditingEnabled(boolean enabled){
        medName.setEnabled(enabled);
        medDesc.setEnabled(enabled);
        medRecom.setEnabled(enabled);

        }


    private void writeNewPost(String userId, String username, String name, String desc, String recom ){

        String key = mDatabase.child("med").push().getKey();
        Medicine med = new Medicine(userId,name,desc,recom);
        Map<String, Object> medValues = med.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/meds/" + key,medValues);
        childUpdates.put("/user-meds/" + userId + "/"+ key,medValues);

        mDatabase.updateChildren(childUpdates);

    }

}
