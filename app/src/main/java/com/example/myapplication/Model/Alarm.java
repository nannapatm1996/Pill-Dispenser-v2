package com.example.myapplication.Model;


import android.util.SparseBooleanArray;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

public class Alarm {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef ({MON,TUES,WED,THURS,FRI,SAT,SUN})
    @interface Days{}
    public static final int MON = 1;
    public static final int TUES = 2;
    public static final int WED = 3;
    public static final int THURS = 4;
    public static final int FRI = 5;
    public static final int SAT = 6;
    public static final int SUN = 7;

    private int alarmNumber, hour, minute;
    private String format, name, uid, alarmMed,alarmRecom,time;
    private SparseBooleanArray allDays;
 //   private boolean mon, tues, wed, thurs, fri, sat, sun;
    //private long time;





   // public Alarm(String uid, int hour, int minute, String format,String name,String alarmMed, String alarmRecom,String time,@Days int... days){
    public Alarm(String uid, int hour, int minute, String format,String name,String alarmMed, String alarmRecom, String time){
        //this.alarmNumber = alarmNumber;
        this.uid = uid;
        this.hour = hour;
        this.minute = minute;
        this.format = format;
       // this.allDays = buildDaysArray(days);
        this.name = name;
        this.alarmMed = alarmMed;
        this.alarmRecom = alarmRecom;
        this.time = time;
       /* this.mon = mon;
        this.tues = tues;
        this.wed = wed;
        this.thurs = thurs;
        this.fri = fri;
        this.sat = sat;
        this.sun = sun;*/
       // this.isEnabled = isEnabled;
    }

    public Alarm() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public String getUid(){return uid; }

    public void setUid(String Uid){this.uid = Uid;}

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getName(){return name;}

    public void setName(String name){this.name = name;}

    public String getMed(){return alarmMed;}

    public void setMed(String alarmMed){this.alarmMed = alarmMed;}

    public String getRecom(){return alarmRecom;}

    public void setRecom(String alarmRecom){this.alarmRecom = alarmRecom; }

    public void setTime(String time) { this.time = time; }

    public String getTime() { return time; }


    //public void setIsEnabled(){this.isEnabled = isEnabled;}

    //public boolean getIsEnabled(){return isEnabled;}

   /* public static void setDay(@Days int day, boolean isAlarmed) {
        allDays.append(day, isAlarmed);
    }

    public SparseBooleanArray getDays() {
        return allDays;
    }

    public boolean getDay(@Days int day){
        return allDays.get(day);
    }*/

  /*  public void setDay(@Days int day, boolean isAlarmed) {
        allDays.append(day, isAlarmed);
    }

    public SparseBooleanArray getDays() {
        return allDays;
    }

    public boolean getDay(@Days int day){
        return allDays.get(day);
    }*/





    /*public int getAlarmNumber() {
        return alarmNumber;
    }

    public void setAlarmNumber(int alarmNumber) {
        this.alarmNumber = alarmNumber;
    }*/

   // public String getName() { return name; }

    //public void setName(String ampm) { this.name = name; }


    private static SparseBooleanArray buildDaysArray(@Days int... days) {

        final SparseBooleanArray array = buildBaseDaysArray();

        for (@Days int day : days) {
            array.append(day, true);
        }

        return array;

    }

    private static SparseBooleanArray buildBaseDaysArray() {

        final int numDays = 7;

        final SparseBooleanArray array = new SparseBooleanArray(numDays);

        array.put(MON, false);
        array.put(TUES, false);
        array.put(WED, false);
        array.put(THURS, false);
        array.put(FRI, false);
        array.put(SAT, false);
        array.put(SUN, false);

        return array;

    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid",uid);
        result.put("hour",hour);
        result.put("minute",minute);
        result.put("format", format);
        result.put("alldays", allDays);
        result.put("name",name);
        result.put("med",alarmMed);
        result.put("recom",alarmRecom);
        result.put("time",time);
      /*  result.put("mon", mon);
        result.put("tues", tues);
        result.put("wed", wed);
        result.put("thurs", thurs);
        result.put("fri",fri);
        result.put("sat", sat);
        result.put("sun", sun);*/
        //result.put("isEnabled",isEnabled);

        return result;

    }


}
