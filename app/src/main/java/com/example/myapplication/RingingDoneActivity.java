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
    private static String sRoom1;
    private static String sRoom2;
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
                //robotAPI.motion.moveBody(0,0,30);
                //robotAPI.robot.speak("Patrick, where are you");
                //robotAPI.motion.moveBody(0,0,-60);
                //robotAPI.motion.moveBody(0,0,30);

                //robot Locomotion
                ArrayList<RoomInfo> arrayListRooms = robotAPI.contacts.room.getAllRoomInfo();
                sRoom1 = arrayListRooms.get(0).keyword;
                sRoom2 = arrayListRooms.get(1).keyword;
                // sRoom3 = arrayListRooms.get(2).keyword;
                //mTvRoom1.setText(sRoom1+" ; "+sRoom2); set room at Text

                //TODO: add fetch time and country
                Calendar c = Calendar.getInstance();
                SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date dt = new Date();
                day = dt.getDate();
                month = dt.getMonth() + 1;
                hr = dt.getHours();
                min = dt.getMinutes();

                IslamicUrl = "https://zenbo.pythonanywhere.com/api/v1/resources/prayertime?day=" + day + "&month=" +
                        month + "&hour=" + hr + "&minute=" + min + "&country=QA";

                newLoco();

            }
        }.start();


        /*new CountDownTimer(5000,1000){
            public void onTick(long millisUntilFinished){

            }
            public void onFinish(){
                robotAPI.robot.setExpression(RobotFace.DEFAULT, globalname +", it's time to take your medicine");
            }
        }.start();

        new CountDownTimer(13000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                robotAPI.motion.moveBody(0,0,30);
                //robotAPI.robot.speak("Patrick, where are you");
                robotAPI.motion.moveBody(0,0,-60);
                robotAPI.motion.moveBody(0,0,30);
                robotAPI.robot.speak( globalname + ", where are you");
                robotAPI.robot.setExpression(RobotFace.DOUBTING);
            }
        }.start();

        new CountDownTimer(16000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {

                robotAPI.robot.speak("Oh, you are over there, I see you now");
                robotAPI.robot.setExpression(RobotFace.PROUD);
                //int SerialFollow = robotAPI.utility.followUser();
                //startDetectFace();
                StartFollowMe();

            }
        }.start();*/

    }

    private void StartFollowMe() {
        int SerialFollow = robotAPI.utility.followUser();

        new CountDownTimer(18000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                // robotAPI.robot.speak("Have a good day");
                robotAPI.robot.setExpression(RobotFace.HAPPY);
                //robotAPI.motion.moveBody(0,0,30);
                //robotAPI.motion.moveBody(0,0,-60);
                //robotAPI.motion.moveBody(0,0,30);
                robotAPI.motion.moveHead(0, 10, L2);
                //robotAPI.motion.moveHead(0,0,L2);
              /*  robotAPI.robot.speak("Please take the " + "Wednesday, " + globalformat + "," + globalMed +
                        " " + "," +"and " + globalRecom + "and don't forget to " + globalReminder +
                        " before taking the pill from the pill dispenser box");*/
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

    //Get an alarm sound. Try for an alarm. If none set, try notification,
    //Otherwise, ringtone.
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

    private void newLoco() {
        //robotAPI.motion.goTo(sRoom1);
        //robotAPI.slam.getLocation();

        new CountDownTimer(120000, 1000) {


            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {


                //robotAPI.slam.getLocation();
                robotAPI.utility.findPersonNearby();
                robotAPI.vision.requestDetectPerson(60);
                robotAPI.utility.lookAtUser(7);
                robotAPI.robot.setExpression(RobotFace.DEFAULT);

            }
        }.start();

        if (personDetect = true) {
            int SerialFollow = robotAPI.utility.followUser();
            new CountDownTimer(30000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    robotAPI.cancelCommandBySerial(SerialFollow);
                    Intent intent = new Intent(RingingDoneActivity.this, MainActivity_pray.class);
                    startActivity(intent);
                }
            }.start();
            //Intent intent = new Intent(MainActivity.this,CameraActivity.class);
        } else {
            robotAPI.robot.speak("Not Found");
        }
       /* robotAPI.utility.findPersonNearby();
        robotAPI.vision.requestDetectPerson(60);
        robotAPI.utility.lookAtUser(7);
        robotAPI.robot.setExpression(RobotFace.DEFAULT);*/

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
                            Intent i = new Intent(RingingDoneActivity.this, CameraActivity_pray.class);
                            startActivity(i);
                        } else {
                            robotAPI.robot.speak("Face Not Found");
                            //robotAPI.robot.speak("Rotating...");
                            //robotAPI.motion.moveBody(0,0,90);
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




   /* private void startDetectFace(){

        //TODO: Query to find the time format (AM/PM)

        VisionConfig.FaceDetectConfig config = new VisionConfig.FaceDetectConfig();
        config.enableDebugPreview = true;  // set to true if you need preview screen
        config.intervalInMS = 1000;
        config.enableDetectHead = true;
        int serialFaceDetect = robotAPI.vision.requestDetectFace(config);
        //robotAPI.robot.setExpression(RobotFace.HAPPY,"Patrick I found you!");

        /*new CountDownTimer(8000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                // robotAPI.robot.speak("Have a good day");
                //robotAPI.robot.speak("This is your afternoon medicine, please take it");
            }
        }.start();*/

    //robotAPI.utility.followUser();

    // int serialFollow = robotAPI.utility.followUser();


//        robotAPI.vision.cancelDetectFace(serialFollow);

        /*new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                robotAPI.cancelCommand(serialFaceDetect);
                robotAPI.cancelCommand(serialFollow);
                robotAPI.motion.moveBody(0,0,180);
                robotAPI.motion.moveBody((float) 0.30,0,0);
                robotAPI.robot.speak("Have a good day");

            }
        }.start();




        new CountDownTimer(8000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                //robotAPI.robot.speak("Have a good day");

            }
        }.start();




    }

    private void stopDetectFace() {
        // stop detect face
        robotAPI.vision.cancelDetectFace();
        robotAPI.utility.resetToDefaultSetting();

    }*/

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

            //use toast to show detected faces
            //facedetect_result = "Face Detected";

            //String toast_result = "Detect Face";
//            Toast toast = Toast.makeText(context, toast_result, Toast.LENGTH_SHORT);
            //           toast.show();
            //super.onDetectPersonResult(resultList);
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