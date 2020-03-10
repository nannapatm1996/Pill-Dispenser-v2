package com.example.myapplication.ViewHolder;

import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Alarm;
import com.example.myapplication.R;

public class AlarmViewHolder extends RecyclerView.ViewHolder {

    public TextView ar_time, ar_label, ar_days, ar_meds, ar_format,ar_Recom;
    public Switch ar_switch;
    public String[] mDays;
    public int mAccentColor = -1;

    public AlarmViewHolder(@NonNull View itemView) {
        super(itemView);

        ar_time = itemView.findViewById(R.id.ar_time);
        //ar_ampm = itemView.findViewById(R.id.ar_am_pm);
        ar_label = itemView.findViewById(R.id.ar_label);
        ar_days = itemView.findViewById(R.id.ar_days);
        ar_meds = itemView.findViewById(R.id.ar_med);
        ar_switch = itemView.findViewById(R.id.ar_switch);
        ar_format = itemView.findViewById(R.id.ar_am_pm);
        ar_Recom = itemView.findViewById(R.id.ar_rec);

    }

//    public void setEnabled
//TODO: make text show

    public void setAlarmName(String string){ar_label.setText(string);}

    public void setAmPm (String string){ar_format.setText(string);}

    public void setRecom (String string){ar_Recom.setText(string);}

    public void setTime (String string){ar_time.setText(string);}

    //public void setTime (long string){ar_time.setText;}

    public void setAr_meds (String string){ar_meds.setText(string);}

  //  public void setAr_days(int day){ar_days.setText(buildSelectedDays(day));}

    public void setDays (String string) {ar_days.setText(string);}

    public void bindToPost(Alarm alarm){

    }



}








