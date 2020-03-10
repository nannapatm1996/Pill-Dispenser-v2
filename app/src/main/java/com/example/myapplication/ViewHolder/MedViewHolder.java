package com.example.myapplication.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Medicine;
import com.example.myapplication.R;

public class MedViewHolder extends RecyclerView.ViewHolder {
    public TextView medName, medDesc, medRecom;

    public MedViewHolder(@NonNull View itemView) {
        super(itemView);

        medName = itemView.findViewById(R.id.TxmedName);
        medDesc = itemView.findViewById(R.id.TxmedDesc);
        medRecom = itemView.findViewById(R.id.TxmedRecom);
    }

    public void setMedName(String string){ medName.setText(string); }

    public void setMedDesc(String string){
        medDesc.setText(string);
    }

    public void setMedRecom(String string){
        medRecom.setText(string);
    }


    public void bindToPost(Medicine med){
        medName.setText(med.medName);
        medDesc.setText(med.medDesc);
        medRecom.setText(med.medRecom);
    }
}
