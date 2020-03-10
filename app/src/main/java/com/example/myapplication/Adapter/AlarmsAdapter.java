package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Alarm;
import com.example.myapplication.R;

import java.util.List;

public final class AlarmsAdapter extends RecyclerView.Adapter<AlarmsAdapter.ViewHolder> {
    private List<Alarm> mAlarms;

    private String[] mDays;
    private int mAccentColor = -1;

    @NonNull
    @Override
    public AlarmsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmsAdapter.ViewHolder holder, int position) {

        final Context c = holder.itemView.getContext();

        if(mAccentColor == -1){
            mAccentColor = ContextCompat.getColor(c, R.color.colorAccent);
        }

      /*  if(mDays == null){
            mDays = c.getResources().getStringArray(R.array.days_abbreviated);
        }*/

        final Alarm alarm = mAlarms.get(position);

       // holder.ar_time.setText();

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {
        public TextView ar_time, ar_ampm, ar_label, ar_days, ar_meds;
        public Switch ar_switch;

        ViewHolder(View itemView) {
            super(itemView);

            ar_time = itemView.findViewById(R.id.ar_time);
            ar_ampm = itemView.findViewById(R.id.ar_am_pm);
            ar_label = itemView.findViewById(R.id.ar_label);
            ar_days = itemView.findViewById(R.id.ar_days);
            ar_meds = itemView.findViewById(R.id.ar_med);
            ar_switch = itemView.findViewById(R.id.ar_switch);

        }
    }
}
