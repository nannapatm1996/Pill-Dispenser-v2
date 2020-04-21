package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class PrayingWaitActivity extends BaseActivity {
    String name,medicine,format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_praying_wait);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        name = getIntent().getStringExtra("name");
        medicine = getIntent().getStringExtra("med");
        format = getIntent().getStringExtra("format");

        Button FinishBtn;

        FinishBtn = findViewById(R.id.finishBtn);

        FinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrayingWaitActivity.this, AfterMedActivity.class);
                //Log.d("Result",responseResult);
                intent.putExtra("name", name);
                intent.putExtra("med", medicine);
                intent.putExtra("format", format);
                //intent.putExtra("imagePath",selectedImagePath);
                startActivity(intent);
            }
        });


    }
}
