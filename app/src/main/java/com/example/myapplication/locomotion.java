package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotCmdState;
import com.asus.robotframework.API.RobotCommand;
import com.asus.robotframework.API.RobotErrorCode;
import com.asus.robotframework.API.RobotFace;
import com.asus.robotframework.API.results.DetectPersonResult;
import com.asus.robotframework.API.results.RoomInfo;
import com.robot.asus.robotactivity.RobotActivity;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class locomotion extends RobotActivity {

    public static boolean personDetect = false;
    private static String sRoom1,sRoom2,sRoom3,sRoom4,sRoom5,room;
    private int day, month, hr, min;
    private String IslamicUrl;
    //private static String facedetect_result = "no face detect";


    public locomotion(RobotCallback robotCallback, RobotCallback.Listen robotListenCallback) {
        super(robotCallback, robotListenCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locomotion);

        //Get intent from camera activity on previous room
        room = getIntent().getStringExtra("room");
        onPersonNotDetect(room);


    }

    private void IslamicCalendar(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt = new Date();
        day = dt.getDate();
        month = dt.getMonth() + 1;
        hr = dt.getHours();
        min = dt.getMinutes();

        IslamicUrl = "https://zenbo.pythonanywhere.com/api/v1/resources/prayertime?day=" + day + "&month=" +
                month + "&hour=" + hr + "&minute=" + min + "&country=QA";

        StringRequest req = new StringRequest(Request.Method.GET, IslamicUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Response", response);
                motion(response);

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(req);

    }

    private void motion(String prevroom){
        ArrayList<RoomInfo> roomInfo = robotAPI.contacts.room.getAllRoomInfo();
        sRoom1 = roomInfo.get(0).keyword;
        sRoom2 = roomInfo.get(1).keyword;
        sRoom3 = roomInfo.get(2).keyword;
        //sRoom4 = roomInfo.get(3).keyword;
        //sRoom5 = roomInfo.get(4).keyword;

            robotAPI.motion.goTo(sRoom1);
            findPerson(sRoom1);

    }

    private void findPerson(String room){
        new CountDownTimer(30000,1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                robotAPI.utility.findPersonNearby();
                robotAPI.vision.requestDetectPerson(60);
                robotAPI.utility.lookAtUser(5);
                robotAPI.robot.setExpression(RobotFace.DEFAULT);
                if(personDetect = true){
                    onPersonDetect();
                }
                else{
                    onPersonNotDetect(room);
                }

            }
        }.start();
    }

    private void onPersonDetect() {

        int SerialFollow = robotAPI.utility.followUser();
        new CountDownTimer(30000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                //send to Facial Recognition Activity
                robotAPI.cancelCommandBySerial(SerialFollow);
                Intent intent = new Intent(locomotion.this, CameraActivity.class);
                startActivity(intent);
            }
        }.start();
    }

    private void onPersonNotDetect(String prevRoom) {
        if(prevRoom.equals(sRoom3)){
            Intent i = new Intent(locomotion.this, AfterMedActivity.class);
            startActivity(i);
        }
        else if(prevRoom.equals(sRoom1)){
            robotAPI.motion.goTo(sRoom2);
            findPerson(sRoom2);
        }
        else if(prevRoom.equals(sRoom2)){
            robotAPI.motion.goTo(sRoom3);
            findPerson(sRoom3);
        }
        /*else{
            robotAPI.motion.goTo(sRoom4);
            findPerson(sRoom4);
        }*/

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

        @Override
        public void onDetectPersonResult(List<DetectPersonResult> resultList) {
            super.onDetectPersonResult(resultList);

            Log.d("RobotDevSample", "onDetectFaceResult: " + resultList.get(0));
            personDetect = true;

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
}

