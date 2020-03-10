package com.example.myapplication;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotCmdState;
import com.asus.robotframework.API.RobotCommand;
import com.asus.robotframework.API.RobotErrorCode;
import com.asus.robotframework.API.RobotFace;
import com.asus.robotframework.API.WheelLights;
import com.robot.asus.robotactivity.RobotActivity;

import org.json.JSONObject;

public class AfterMedActivity extends RobotActivity {
    private String[] wheelLightsID = {"SYNC_BOTH", "ASYNC_LEFT", "ASYNC_RIGHT"};

    public AfterMedActivity(RobotCallback robotCallback, RobotCallback.Listen robotListenCallback) {
        super(robotCallback, robotListenCallback);
    }

    MediaPlayer mMediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_med);

        robotAPI.robot.setExpression(RobotFace.HIDEFACE);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        String Blink;


        Button btnTook = findViewById(R.id.btnTook);
        Button btnSkip = findViewById(R.id.btnSkip);

        robotAPI.robot.speak("Did you take the medicine?");

        btnTook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                robotAPI.robot.setExpression(RobotFace.PROUD);
                robotAPI.robot.speak("You will definitely get better soon");
                int bright = 50;

                robotAPI.motion.moveBody(0,0,180);
                robotAPI.motion.moveBody(1,0,0);
                robotAPI.motion.moveBody(0,0,180);
                // robotAPI.wheelLights.turnOff(WheelLights.Lights.SYNC_BOTH, 0xff);
                robotAPI.wheelLights.setColor(WheelLights.Lights.SYNC_BOTH, 0xff, 0x0000ff00);
                robotAPI.wheelLights.setBrightness(WheelLights.Lights.SYNC_BOTH,0xff, bright);
                robotAPI.wheelLights.startBlinking(WheelLights.Lights.SYNC_BOTH,0xff,30,30,40);


                new CountDownTimer(8000, 1000) {

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        robotAPI.robot.setExpression(RobotFace.PLEASED,"You will not forget to take your medicine anymore");
                        robotAPI.motion.moveBody(0,0,30);
                        robotAPI.motion.moveBody(0,0,-60);
                        robotAPI.motion.moveBody(0,0,30);

                        robotAPI.robot.setExpression(RobotFace.ACTIVE);
                        robotAPI.robot.speak("you see, I can make your life more convenient");

                    }
                }.start();

            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                robotAPI.robot.setExpression(RobotFace.WORRIED);
                robotAPI.robot.speak("Zenbo will have to tell your caretaker");
                int bright = 50;

                robotAPI.motion.moveBody(0,0,180);
                robotAPI.motion.moveBody(1,0,0);
                robotAPI.motion.moveBody(0,0,180);
                robotAPI.wheelLights.setColor(WheelLights.Lights.SYNC_BOTH, 0xff, 0x00ff0000);
                robotAPI.wheelLights.setBrightness(WheelLights.Lights.SYNC_BOTH,0xff, bright);
                robotAPI.wheelLights.startBlinking(WheelLights.Lights.SYNC_BOTH,0xff,30,30,40);


                new CountDownTimer(8000, 1000) {

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        robotAPI.robot.setExpression(RobotFace.HELPLESS,"I will try my best to keep you healthy");
                        robotAPI.motion.moveBody(0,0,30);
                        robotAPI.motion.moveBody(0,0,-60);
                        robotAPI.motion.moveBody(0,0,30);
                        robotAPI.robot.setExpression(RobotFace.PROUD);
                        robotAPI.robot.speak("you know, I care about you so much");

                    }
                }.start();




            }
        });


    }

    @Override
    protected void onResume(){
        super.onResume();

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

    }

    public static RobotCallback robotCallback = new RobotCallback() {
        @Override
        public void onResult(int cmd, int serial, RobotErrorCode err_code, Bundle result) {
            super.onResult(cmd, serial, err_code, result);
            Log.d("RobotDevSample", "onResult:"
                    + RobotCommand.getRobotCommand(cmd).name()
                    + ", serial:" + serial + ", err_code:" + err_code
                    + ", result:" + result.getString("RESULT"));
        }

        @Override
        public void onStateChange(int cmd, int serial, RobotErrorCode err_code, RobotCmdState state) {
            super.onStateChange(cmd, serial, err_code, state);
        }

        @Override
        public void initComplete() {
            super.initComplete();

        }
    };

    public static RobotCallback.Listen robotListenCallback = new RobotCallback.Listen() {
        @Override
        public void onFinishRegister() {

        }

        @Override
        public void onVoiceDetect(JSONObject jsonObject) {

        }

        @Override
        public void onSpeakComplete(String s, String s1) {
            Log.d("RobotDevSample", "speak Complete");
        }

        @Override
        public void onEventUserUtterance(JSONObject jsonObject) {

        }

        @Override
        public void onResult(JSONObject jsonObject) {
        }

        @Override
        public void onRetry(JSONObject jsonObject) {

        }
    };



    public AfterMedActivity (){super (robotCallback, robotListenCallback);}
}
