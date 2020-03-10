package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapplication.Model.Alarm;
import com.example.myapplication.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NewAlarmActivity extends BaseActivity {

    private static final String TAG = "NewAlarmActivity";
    private static final String REQUIRED = "Required";

    private DatabaseReference mDatabase;

    private TimePicker mTimePicker;
    private EditText mAlarmName;
    private EditText mAlarmMedName;
    private EditText mAlarmMedRecom;
    private CheckBox mMon, mTues, mWed, mThurs, mFri, mSat, mSun;
    private Button mSubmit, mCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        mTimePicker = findViewById(R.id.edit_alarm_time_picker);

        mAlarmMedName = findViewById(R.id.edit_alarm_med);
        mAlarmMedRecom = findViewById(R.id.edit_alarm_recom);
        mAlarmName = findViewById(R.id.edit_alarm_name);

        mSubmit = findViewById(R.id.btn_alarm_submit);
        mCancel = findViewById(R.id.btn_alarm_cancel);

        mMon = findViewById(R.id.edit_alarm_mon);
        mTues = findViewById(R.id.edit_alarm_tues);
        mWed = findViewById(R.id.edit_alarm_wed);
        mThurs = findViewById(R.id.edit_alarm_thurs);
        mFri = findViewById(R.id.edit_alarm_fri);
        mSat = findViewById(R.id.edit_alarm_sat);
        mSun = findViewById(R.id.edit_alarm_sun);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAlarm();
            }
        });

    }

   /* private void setDayCheckboxes(Alarm alarm) {
        mMon.setChecked(alarm.getDay(Alarm.MON));
        mTues.setChecked(alarm.getDay(Alarm.TUES));
        mWed.setChecked(alarm.getDay(Alarm.WED));
        mThurs.setChecked(alarm.getDay(Alarm.THURS));
        mFri.setChecked(alarm.getDay(Alarm.FRI));
        mSat.setChecked(alarm.getDay(Alarm.SAT));
        mSun.setChecked(alarm.getDay(Alarm.SUN));
    }*/
    private void submitAlarm() {
        //Get Input EditText, Timepicker, CheckBox

        final String alarmName = mAlarmName.getText().toString();
        final String alarmMedName = mAlarmMedName.getText().toString();
        final String alarmMedRecom = mAlarmMedRecom.getText().toString();
        Log.d("alarmMed", alarmMedName);
        Log.d("alarmRecom", alarmMedRecom);
        Log.d("alarmName",alarmName);
        int chosenHour, chosenMinute;

        final Calendar time = Calendar.getInstance();
        //time.set(Calendar.MINUTE, getTimePickerMinute(mTimePicker));
        //time.set(Calendar.HOUR_OF_DAY, getTimePickerHour(mTimePicker));

        final int hour = mTimePicker.getHour();
        final int minute = mTimePicker.getMinute();
        int hourFormat;
        final String timeDisplay;
        String minFormat;
        if (mTimePicker.getHour() > 12 && mTimePicker.getHour() != 12){
           hourFormat = hour - 12;
        }
        else{
            hourFormat = hour;
        }

        if (mTimePicker.getMinute() < 10){
            //minFormat = "0" + minute;
            timeDisplay = (hourFormat + ":"+ "0" + minute);
        }
        else{
            //minFormat = minute.toString()
            timeDisplay = (hourFormat + ":" + minute);
        }





       // timeDisplay = (hourFormat + ":"+ minute);

        final String ampm;
        if (mTimePicker.getHour() >= 12  ){
            ampm = "PM";
        }
        else{
            ampm = "AM";
        }

        /*Alarm alarm = new Alarm();
        alarm.setDay(Alarm.MON,mMon.isChecked());
        alarm.setDay(Alarm.TUES,mTues.isChecked());
        alarm.setDay(Alarm.WED,mWed.isChecked());
        alarm.setDay(Alarm.THURS,mThurs.isChecked());
        alarm.setDay(Alarm.FRI,mFri.isChecked());
        alarm.setDay(Alarm.SAT,mSat.isChecked());
        alarm.setDay(Alarm.SUN,mSun.isChecked());*/



        //TODO: put ^ this into writeToPost

        setEditingEnabled(false);
        Toast.makeText(this, "Posting Alarm...", Toast.LENGTH_SHORT).show();

        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                // [START_EXCLUDE]
                if (user == null) {
                    // User is null, error out
                    Log.e(TAG, "User " + userId + " is unexpectedly null");
                    Toast.makeText(NewAlarmActivity.this,
                            "Error: could not fetch user.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Write new post + timepicker, date check etc.
                    writeNewPost(userId, user.username, alarmName, alarmMedRecom, alarmMedName, hour, minute,ampm,timeDisplay);
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

        });
    }


    private void setEditingEnabled(boolean enabled) {
        mAlarmName.setEnabled(enabled);
        mAlarmMedRecom.setEnabled(enabled);
        mAlarmMedName.setEnabled(enabled);


        mMon.setEnabled(enabled);
        mTues.setEnabled(enabled);
        mWed.setEnabled(enabled);
        mThurs.setEnabled(enabled);
        mFri.setEnabled(enabled);
        mSat.setEnabled(enabled);
        mSun.setEnabled(enabled);

        mTimePicker.setEnabled(enabled);

    }


    private void writeNewPost(String userId, String username, String name, String recom, String MedName, int hour, int minute, String ampm, String timeDisplay) {

        String key = mDatabase.child("alarms").push().getKey();
        Alarm alarm = new Alarm(userId,hour,minute,ampm,name,MedName,recom,timeDisplay);
        Map<String, Object> alarmValues = alarm.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/alarms/" + key, alarmValues);
        childUpdates.put("/user-alarms/" + userId + "/" + key, alarmValues);

        mDatabase.updateChildren(childUpdates);

    }


}


