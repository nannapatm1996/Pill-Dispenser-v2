package com.example.myapplication;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotCmdState;
import com.asus.robotframework.API.RobotCommand;
import com.asus.robotframework.API.RobotErrorCode;
import com.asus.robotframework.API.RobotFace;
import com.asus.robotframework.API.WheelLights;
import com.example.myapplication.service.MySingleton;
import com.google.firebase.messaging.FirebaseMessaging;
import com.robot.asus.robotactivity.RobotActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AfterMedActivity extends RobotActivity {
    private String[] wheelLightsID = {"SYNC_BOTH", "ASYNC_LEFT", "ASYNC_RIGHT"};

    public AfterMedActivity(RobotCallback robotCallback, RobotCallback.Listen robotListenCallback) {
        super(robotCallback, robotListenCallback);
    }

    //FCM server
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAaTSSlCg:APA91bFKXG62Iqu4yOIYDdM1U6QnGY_dy1pTw50Zbj8FYuzvMiyGHIgcsTTYF97T3weEOXsjgn_NzNnuJjfb_Osg5eXRI4iQIyMV-_g9z2yCbC1FSRGZARss_dpCC8Q3jOKT7enQE2U9";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";

    String NOTIFICATION_TITLE ;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

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

        FirebaseMessaging.getInstance().subscribeToTopic("pills");

        String name = getIntent().getStringExtra("name");
        String med = getIntent().getStringExtra("med");
        String format = getIntent().getStringExtra("format");

        robotAPI.robot.speak("Did you take the medicine?");

        btnTook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("Intent_name", name);
                TOPIC = "/topics/pills"; //topic has to match what the receiver subscribed to
                NOTIFICATION_TITLE = "Pill Dispenser Result";
                NOTIFICATION_MESSAGE = name +" already took" + " " +format + " " + med;

                JSONObject notification = new JSONObject();
                JSONObject notifcationBody = new JSONObject();
                try {
                    notifcationBody.put("title", NOTIFICATION_TITLE);
                    notifcationBody.put("message", NOTIFICATION_MESSAGE);

                    notification.put("to", TOPIC);
                    notification.put("data", notifcationBody);
                } catch (JSONException e) {
                    Log.e(TAG, "onCreate: " + e.getMessage() );
                }
                sendNotification(notification);

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
                //set up FCM
                Log.d("Intent_name", name);
                TOPIC = "/topics/pills"; //topic has to match what the receiver subscribed to
                NOTIFICATION_TITLE = "Pill Dispenser Result";
                NOTIFICATION_MESSAGE = name +" did not took" + " " +format + " " + med + " please check";

                JSONObject notification = new JSONObject();
                JSONObject notifcationBody = new JSONObject();
                try {
                    notifcationBody.put("title", NOTIFICATION_TITLE);
                    notifcationBody.put("message", NOTIFICATION_MESSAGE);

                    notification.put("to", TOPIC);
                    notification.put("data", notifcationBody);
                } catch (JSONException e) {
                    Log.e(TAG, "onCreate: " + e.getMessage() );
                }
                sendNotification(notification);

                robotAPI.robot.setExpression(RobotFace.WORRIED);
                robotAPI.robot.speak("Zenbo will have to tell your caretaker");
                int bright = 50;

                robotAPI.motion.moveBody(0,0,180);
                robotAPI.motion.moveBody(2,0,0);
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

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                        //edtTitle.setText("");
                        //edtMessage.setText("");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AfterMedActivity.this, "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }



    public AfterMedActivity (){super (robotCallback, robotListenCallback);}
}
