package com.example.myapplication.Utils;

import android.app.Activity;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AlarmUtils {
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("h:mm", Locale.getDefault());
    private static final SimpleDateFormat AM_PM_FORMAT = new SimpleDateFormat("a", Locale.getDefault());

    private static final int REQUEST_ALARM = 1;
   // private static final String[] PERMISSIONS_ALARM = {Manifest.permission.VIBRATE};

    private AlarmUtils(){throw new AssertionError(); }

    public static void checkAlarmPermissions(Activity activity){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return;
        }
    }

   /* public static ContentValues toContentValues(Alarm alarm){
        final SparseBooleanArray days = alarm.getDays();

    }*/
}
