package com.robot.asus.robotactivity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotCallback;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RobotActivity extends Activity{
    public RobotAPI robotAPI;
    RobotCallback robotCallback;
    RobotCallback.Listen robotListenCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.robotAPI = new RobotAPI(getApplicationContext(), robotCallback);
    }

    public RobotActivity (RobotCallback robotCallback, RobotCallback.Listen robotListenCallback) {
        this.robotCallback = robotCallback;
        this.robotListenCallback = robotListenCallback;
    }

    @Override
    protected void onPause() {
        super.onPause();
        robotAPI.robot.unregisterListenCallback();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(robotListenCallback!= null)
            robotAPI.robot.registerListenCallback(robotListenCallback);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        robotAPI.release();
    }

    public static class deviceHttpPost extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... parameters) {
            OkHttpClient client = new OkHttpClient();
            String lightPin = parameters[0];
            String lightResult = parameters[1];
            String resultLightValue;
            MediaType mediaType = MediaType.parse("text/csv");
            if(lightResult.equals("hight")) {
                resultLightValue = lightPin+",,1";
            }
            else
                resultLightValue = lightPin+",,0";
            RequestBody body = RequestBody.create(mediaType, resultLightValue);

            Request request = new Request.Builder()
                    .url("https://api.mediatek.com/mcs/v2/devices/DjhyExmq/datapoints.csv")
                    .post(body)
                    .addHeader("deviceKey", "UJDPigFqdMOECY10")
                    .addHeader("Content-Type", "text/csv")
                    .addHeader("cache-control", "no-cache")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return lightResult;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //modeStatus.setText("Presentation");
        }
    }

    public static void presentationActivity(){
        new deviceHttpPost().execute("2","low");
        new deviceHttpPost().execute("3","low");
        new deviceHttpPost().execute("4","hight");
    }
    public static void groupDiscussionActivity(){
        new deviceHttpPost().execute("2","hight");
        new deviceHttpPost().execute("3","hight");
        new deviceHttpPost().execute("4","hight");
    }
    public static void afterClassActivity(){
        new deviceHttpPost().execute("2","low");
        new deviceHttpPost().execute("3","low");
        new deviceHttpPost().execute("4","low");
    }
}
