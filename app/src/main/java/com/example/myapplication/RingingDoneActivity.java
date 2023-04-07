package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.NonNull;

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
import com.asus.robotframework.API.VisionConfig;
import com.asus.robotframework.API.results.DetectPersonResult;
import com.asus.robotframework.API.results.RoomInfo;
import com.example.myapplication.Model.Alarm;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.robot.asus.robotactivity.RobotActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.asus.robotframework.API.MotionControl.SpeedLevel.Head.L2;

public class RingingDoneActivity extends RobotActivity {

    private static final String TAG = "RingingDone";

    private MediaPlayer mMediaPlayer;
    private static Context context;
    private CountDownTimer mCountdownTimer;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    //DatabaseReference mDbRef = mDatabase.getReference('alarms');
    public String globalformat;
    public String globalname;
    public String globalMed;
    public String globalRecom;
    public String globalReminder;
    public String globalDay;
    public static boolean personDetect = false;
     public String userid;
    // private DatabaseReference myRef;
    //public Intent i;

    //For snapshot
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;

    //Robot Locomotion
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static boolean isRobotApiInitialed = false;
    private static String sRoom1,sRoom2,sRoom3,sRoom4,sRoom5;
    private int day, month, hr, min;
    private String IslamicUrl;
    private static String facedetect_result = "no face detect";
    private static String thismap = "map1";
    CountDownTimer TimerDetect;


    public RingingDoneActivity(RobotCallback robotCallback, RobotCallback.Listen robotListenCallback) {
        super(robotCallback, robotListenCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringing_done);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        final DatabaseReference table_user = mDatabase.getReference("alarms");
        //TODO: Query Data


        // FirebaseUser user = mAuth.getCurrentUser();
        // userID = user.getUid();
        table_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String Name = ds.child("name").getValue().toString();
                    String Format = ds.child("format").getValue().toString();
                    String Medicine = ds.child("med").getValue().toString();
                    String Recom = ds.child("recom").getValue().toString();
                    String addRemind = ds.child("addReminder").getValue().toString();

                    globalname = Name;
                    globalformat = Format;
                    globalMed = Medicine;
                    globalRecom = Recom;
                    globalReminder = addRemind;
                    //etc
                }

                Log.d("GlobalVar", globalname);
                Log.d("GlobalVar", globalformat);
                Log.d("GlobalVar", globalMed);
                Log.d("GlobalVar", globalRecom);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        //TODO: Query the name to fill the the sentence

        playSound(this, getAlarmUri());
        new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                //robotAPI.robot.setExpression(RobotFace.SHOCKED);
                mMediaPlayer.stop();
                robotAPI.robot.setExpression(RobotFace.SHOCKED);
                robotAPI.robot.setExpression(RobotFace.DEFAULT);

                IslamicCalendar();


                //TODO: add fetch time and country

                //Intent intent = new Intent(RingingDoneActivity.this, CameraActivity.class);
                //startActivity(intent);
                //newLoco();

            }
        }.start();


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

    private void motion(String prayingtime){
        ArrayList<RoomInfo> roomInfo = robotAPI.contacts.room.getAllRoomInfo();
        sRoom1 = roomInfo.get(0).keyword;
        sRoom2 = roomInfo.get(1).keyword;
        sRoom3 = roomInfo.get(2).keyword;
        //sRoom4 = roomInfo.get(3).keyword;
        //sRoom5 = roomInfo.get(4).keyword;

        //findPerson(sRoom1);

        if(prayingtime.equals("True")){
            robotAPI.motion.goTo(sRoom4);
            new CountDownTimer(18000, 1000) {

                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    findPerson(sRoom4);
                }
            }.start();

        }
        else{
            robotAPI.motion.goTo(sRoom1);
            new CountDownTimer(18000, 1000) {

                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    findPerson(sRoom1);
                }
            }.start();

        }
    }

    private void findPerson(String room){

        new CountDownTimer(30000,1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                robotAPI.robot.speak("Find Person");
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

    private void StartFollowMe() {
        int SerialFollow = robotAPI.utility.followUser();

        new CountDownTimer(18000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                robotAPI.robot.setExpression(RobotFace.HAPPY);
                robotAPI.motion.moveHead(0, 10, L2);
            }
        }.start();

        new CountDownTimer(24000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                robotAPI.cancelCommandBySerial(SerialFollow);
                //Intent intent = new Intent(RingingDoneActivity.this, CameraActivity.class);
                Intent intent = new Intent(RingingDoneActivity.this, CameraActivity.class);
                intent.putExtra("format", globalformat);
                intent.putExtra("med", globalMed);
                intent.putExtra("name", globalname);
                intent.putExtra("recom", globalRecom);
                intent.putExtra("remind", globalReminder);
                startActivity(intent);
            }
        }.start();


    }


    private void playSound(Context context, Uri alert) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }

    private Uri getAlarmUri() {
        //Uri path = Uri.parse("android.resouce://" + getPackageName() + "/raw/mediation_piano.mp3");
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        if (alert == null) {
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
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
                    Intent intent = new Intent(RingingDoneActivity.this, CameraActivity.class);
                    startActivity(intent);
                }
            }.start();
    }

    private void onPersonNotDetect(String prevRoom) {
        if(prevRoom.equals(sRoom3)){
            Intent i = new Intent(RingingDoneActivity.this, AfterMedActivity.class);
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

    private void Loco() {
        robotAPI.robot.speak("Going to bedroom");
        robotAPI.motion.goTo(sRoom1);
        thismap = "map 1";

        new CountDownTimer(120000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                //robotAPI.robot.speak("Starting Camera...");
                //dispatchTakePictureIntent();
                //galleryAddPic();
                startDetectFace();
                TimerDetect = new CountDownTimer(40000, 10000) {

                    @Override
                    public void onTick(long millisUntilFinished) {

                        if (!facedetect_result.equals("no face detect")) {
                            robotAPI.robot.speak("Face Found");
                            TimerDetect.cancel();
                            TimerDetect = null;
                            robotAPI.robot.speak("Timer Stopped");
                            stopDetectFace();
                            robotAPI.robot.speak("Face Found");
                        } else {
                            robotAPI.robot.speak("Face Not Found");

                        }
                    }

                    @Override
                    public void onFinish() {

                        robotAPI.robot.speak("Timeout");
                        stopDetectFace();
                        Intent i = new Intent(RingingDoneActivity.this, AfterMedActivity.class);

                    }
                }.start();
            }
        }.start();


    }

    private void startDetectFace() {
        // start detect face
        VisionConfig.FaceDetectConfig config = new VisionConfig.FaceDetectConfig();
        config.enableDebugPreview = true;  // set to true if you need preview screen
        config.intervalInMS = 2000;
        config.enableDetectHead = true;
        robotAPI.vision.requestDetectFace(config);
    }

    private void stopDetectFace() {
        // stop detect face
        robotAPI.vision.cancelDetectFace();
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

    private void showData(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            Alarm alarm = new Alarm();
            //User user = new User();
            alarm.setName(ds.child(userID).getValue(Alarm.class).getName()); //set the name
            alarm.setFormat(ds.child(userID).getValue(Alarm.class).getFormat());
            alarm.setAddReminder(ds.child(userID).getValue(Alarm.class).getAddReminder());
//            uInfo.setEmail(ds.child(userID).getValue(UserInformation.class).getEmail()); //set the email
//            uInfo.setPhone_num(ds.child(userID).getValue(UserInformation.class).getPhone_num()); //set the phone_num

            //display all the information
            Log.d(TAG, "showData: name: " + alarm.getName());
            Log.d(TAG, "showData: format: " + alarm.getFormat());
            Log.d(TAG, "showData: Reminder "+ alarm.getAddReminder());
//            Log.d(TAG, "showData: phone_num: " + uInfo.getPhone_num());

        }
    }


    public RingingDoneActivity (){super (robotCallback, robotListenCallback);}

}