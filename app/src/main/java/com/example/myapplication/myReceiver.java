package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

public class myReceiver extends BroadcastReceiver {
    private static final String TAG = myReceiver.class.getSimpleName();
    MediaPlayer mp;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");

        Toast.makeText(context,"Alarm Triggered",Toast.LENGTH_SHORT).show();
        mp = MediaPlayer.create(context,R.raw.meditation_piano);
        mp.start();

    }
}
