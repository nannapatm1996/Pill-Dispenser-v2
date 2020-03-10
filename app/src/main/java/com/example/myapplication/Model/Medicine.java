package com.example.myapplication.Model;

import java.util.HashMap;
import java.util.Map;

public class Medicine {
    public String medName, medDesc, medRecom,uid;
   // private int mednumber;

    public Medicine(String uid, String medName, String medDesc, String medRecom){

        this.uid = uid;
       // this.mednumber = mednumber;
        this.medName = medName;
        this.medDesc = medDesc;
        this.medRecom = medRecom;
    }
    public Medicine(){}

   /* public int getMedNumber() { return mednumber; }

    public void setMedNumber(int medNumber) {
        this.mednumber = mednumber;
    }*/

    public String getUid(){return uid; }

    public String setUid(String Uid){return this.uid = Uid;}

    public String getMedName(){ return  medName; }

    public void setMedName(String medName){this.medName = medName;}

    public String getMedDesc (){return medDesc; }

    public void setMedDesc(String medDesc) {this.medDesc = medDesc; }

    public String getMedRecom (){return medRecom; }

    public void setMedRecom(String medRecom) {this.medRecom = medRecom; }

   public Map<String, Object> toMap(){
       HashMap<String, Object> result = new HashMap<>();
       result.put("uid",uid);
       result.put("medName", medName);
       result.put("medDesc", medDesc);
       result.put("medRecom",medRecom);

       return result;
   }
}
