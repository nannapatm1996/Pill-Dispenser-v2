package com.example.myapplication.Fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Alarm;
import com.example.myapplication.R;
import com.example.myapplication.RingingDoneActivity;
import com.example.myapplication.ViewHolder.AlarmViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public abstract class alarmListFragment extends Fragment {

    private static final String TAG = "alarmListFragment";
    private DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<Alarm, AlarmViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private String[] mDays;
    private int mAccentColor = -1;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    //public String iName;
    //public String iFormat;

    public alarmListFragment(){}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_alarm, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);
        // [END create_database_reference]

        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        //Intent intent = new Intent(getActivity(), myReceiver.class);
        //pendingIntent = PendingIntent.getBroadcast(this,0,intent,pendingIntent.FLAG_UPDATE_CURRENT);

        final Intent intent = new Intent(getActivity(), RingingDoneActivity.class);
        pendingIntent = PendingIntent.getActivity(getActivity() , 0, intent, 0);
        //intent.putExtra("name",iName);
        //intent.putExtra("format",iFormat);



        mRecycler = rootView.findViewById(R.id.messagesListAlarm);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        Query alarmQuery = getQuery(mDatabase);



        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Alarm>()
                .setQuery(alarmQuery, Alarm.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Alarm, AlarmViewHolder>(options) {

            @Override
            public AlarmViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

                return new AlarmViewHolder(inflater.inflate(R.layout.alarm_row, viewGroup, false));
            }
            @Override
            protected void onBindViewHolder(final AlarmViewHolder viewHolder, final int position, final Alarm model) {
                //put into alarm_row
                //TODO make text show

                //viewHolder.setHour(model.getHour());

                viewHolder.ar_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            Log.v("switch state",""+isChecked+" list# "+position);
                            Calendar cal = Calendar.getInstance();
                            cal.setTimeInMillis(System.currentTimeMillis());
                            String currentTime = cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE);
                            String alarmTime = model.getHour()+":"+model.getMinute();
                            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                            Log.v("TimeFormat",alarmTime);
                            try{

                                Date TimeNow = format.parse(currentTime);
                                Date TimeSet = format.parse(alarmTime);

                                long periodTimeAlert = TimeSet.getTime() - TimeNow.getTime();
                                Log.v("alarmTime", "Will alert in "+periodTimeAlert/1000/60 + " minute"); // For example


                            }
                            catch (Exception e){
                                Log.d(TAG,"period time alert error: "+e);

                            }

                            Calendar RingingAlarm = Calendar.getInstance();
                            RingingAlarm.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE),model.getHour(),model.getMinute(),0);
                            Log.d(TAG,"SetAlarmTimeStamp: " + RingingAlarm.getTimeInMillis());
                            if(System.currentTimeMillis() < RingingAlarm.getTimeInMillis()){
                                alarmManager.set(AlarmManager.RTC_WAKEUP,RingingAlarm.getTimeInMillis(), pendingIntent);

                            }
                           // setBootReceiverEnabled(PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
                        }
                        else{
                            Log.v("switch state",""+isChecked+" list# "+position);
                            alarmManager.cancel(pendingIntent);
                            //setBootReceiverEnabled(PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
                        }
                    }
                });

                viewHolder.setAmPm(model.getFormat()); //this works
                viewHolder.setAlarmName(model.getName());//this works
                viewHolder.setTime(model.getTime()); //This works
                viewHolder.setAr_meds(model.getMed());
                viewHolder.setRecom(model.getRecom());

               // iName = model.getName();
               // iFormat = model.getFormat();

                final DatabaseReference alarmRef = getRef(position);
                final String alarmKey = alarmRef.getKey();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }


        };
        mRecycler.setAdapter(mAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }
    public String getUid(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);




}










