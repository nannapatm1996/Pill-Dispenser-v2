package com.example.myapplication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myapplication.Adapter.DeviceListAdapter;
import com.example.myapplication.Model.Alarm;
import com.example.myapplication.Model.User;
import com.example.myapplication.service.BluetoothConnectionService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class NewAlarmActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    private static final String TAG = "NewAlarmActivity";
    private static final String REQUIRED = "Required";

    private DatabaseReference mDatabase;
    private TimePicker mTimePicker;
    private EditText mAlarmName;
    private EditText mAlarmMedName;
    private EditText mAlarmMedRecom;
    private Button mSubmit, mCancel,mBtnRoomPermission;
    private Spinner mReminderSpinner;
    private TextView mRoomPermission;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    String[] reminderOptions = {"Wash Hand","Wear Mask", "Wash hands and wear masks"};
    String reminder;

    //--------------- Var for BT --------------------
    BluetoothAdapter mBluetoothAdapter;
    Button btnEnableDisable_Discoverable;

    BluetoothConnectionService mBluetoothConnection;

    Button btnStartConnection;
    Button btnSend;
    String newline = "\r\n";
    EditText etSend;
    //private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BluetoothDevice mBTDevice;

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;
    ListView lvNewDevices;


    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,mBluetoothAdapter.ERROR);

                switch (state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceiver:State OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1:State TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1:State ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1:State TURNING ON");
                        break;

                }

            }
        }
    };

    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                final int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,mBluetoothAdapter.ERROR);

                switch (mode){
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled");
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled. Able to receive connections");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive Connections");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2:Connecting.....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG,"mBroadcastReceiver2: Connected");
                        break;

                }

            }
        }
    };

    private BroadcastReceiver  mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND");

            if(action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(TAG, "onReceive: "+ device.getName() + ":" + device.getAddress());
                mDeviceListAdapter = new DeviceListAdapter(context,R.layout.bluetooth_row, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);
            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice= intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG,"BroadcastReceiver : BOND_BONDED ");
                    mBTDevice = mDevice;
                }
                if(mDevice.getBondState() == BluetoothDevice.BOND_BONDING){
                    Log.d(TAG,"BroadcastReceiver : BOND_BONDING ");
                }
                if(mDevice.getBondState() == BluetoothDevice.BOND_NONE){
                    Log.d(TAG,"BroadcastReceiver : BOND_NONE ");
                }
            }
        }
    };

    /*@Override
    protected void onDestroy(){
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver2);
        unregisterReceiver(mBroadcastReceiver3);
        unregisterReceiver(mBroadcastReceiver4);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);
        //-------------- Declare for alarm --------------

        mDatabase = FirebaseDatabase.getInstance().getReference();
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mTimePicker = findViewById(R.id.edit_alarm_time_picker);
        mAlarmMedName = findViewById(R.id.edit_alarm_med);
        mAlarmMedRecom = findViewById(R.id.edit_alarm_recom);
        mAlarmName = findViewById(R.id.edit_alarm_name);
        mReminderSpinner = findViewById(R.id.spinner_reminder);
        mSubmit = findViewById(R.id.btn_alarm_submit);
        mCancel = findViewById(R.id.btn_alarm_cancel);
        mReminderSpinner = findViewById(R.id.spinner_reminder);
        mRoomPermission = findViewById(R.id.TvPermission);
        mBtnRoomPermission = findViewById(R.id.btnPermissionRoom);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, reminderOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mReminderSpinner.setAdapter(adapter);
        mReminderSpinner.setOnItemSelectedListener(this);
        // check permission READ_CONTACTS is granted or not
        if (ContextCompat.checkSelfPermission(NewAlarmActivity.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted by user yet
            Log.d("ZenboGoToLocation", "READ_CONTACTS permission is not granted by user yet");
            mRoomPermission.setText(getString(R.string.permission_not_granted));
            mBtnRoomPermission.setEnabled(true);

        }
        else{
            // permission is granted by user
            Log.d("ZenboGoToLocation", "READ_CONTACTS permission is granted");
            mRoomPermission.setText(getString(R.string.permission_granted));
            mBtnRoomPermission.setEnabled(false);

        }

        // initial params
       // mTvRoom1.setText(getString(R.string.first_room_info));

        //------------- Declare for BT ------------------
        Button btnONOFF = (Button) findViewById(R.id.btnONOFF);
        btnEnableDisable_Discoverable = (Button) findViewById(R.id.btnDiscoverable_on_off);
        lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);
        mBTDevices = new ArrayList<>();

        btnStartConnection = (Button) findViewById(R.id.btnStartConnection);
        //btnSend = (Button) findViewById(R.id.btnSend);
        //etSend = (EditText) findViewById(R.id.editText);

        //IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //registerReceiver(mBroadcastReceiver4,filter);

        //mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        lvNewDevices.setOnItemClickListener(NewAlarmActivity.this);

        /*btnONOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick: Enabling/Disabling bluetooth");
                enableDisableBT();
            }
        });*/

        /*btnStartConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startConnection();
            }
        });*/


        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAlarm();
            }
        });

        mBtnRoomPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });
    }




    public void startConnection(){
        startBtConnection(mBTDevice,MY_UUID_INSECURE);
    }

    public void startBtConnection(BluetoothDevice device,UUID uuid){
        Log.d(TAG,"startBTConnection: Initializing RFCOM Bluetooth Connection,"+uuid);
        mBluetoothConnection.startClient(device,uuid);
    }

    public void enableDisableBT(){
        if(mBluetoothAdapter == null){
            Log.d(TAG,"enabledDisabledBT: Does not have BT capabilities");
        }

        if(!mBluetoothAdapter.isEnabled()){
            Log.d(TAG,"enabledDisableBT: enabling BT");
            Intent enabledBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enabledBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        if(mBluetoothAdapter.isEnabled()){
            Log.d(TAG,"enabledDisableBT: enabling BT");
            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
    }


    public void btnEnableDisable_Discoverable(View view) {
        Log.d(TAG, "btnEnableDisable_Discoverable: Making device discoverable for 300 seconds");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2,intentFilter);
    }

    public void btnDiscover(View view) {
        Log.d(TAG, "btnDiscover: Looking for unpaired devices");


        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Cancelling discovery");

            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3,discoverDevicesIntent);
        }
        if(!mBluetoothAdapter.isDiscovering()){
            checkBTPermissions();
            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3,discoverDevicesIntent);
        }
    }

    private void checkBTPermissions(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK Version < LOLLIPOP.");
        }
    }

   // @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
        mBluetoothAdapter.cancelDiscovery();
        Log.d(TAG, "onItemClick: you clicked on a device");
        String deviceName = mBTDevices.get(i).getName();
        String deviceAddress = mBTDevices.get(i).getAddress();
        //String deviceUUIDS = mBTDevice.getUuids();
        Log.d(TAG, "onItemClick: deviceName = "+deviceName);
        Log.d(TAG, "onItemClick: deviceAddress= "+deviceAddress);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            Log.d(TAG, "Trying to pair with "+ deviceName);
            mBTDevices.get(i).createBond();

            mBTDevice = mBTDevices.get(i);
            mBluetoothConnection = new BluetoothConnectionService(NewAlarmActivity.this);
        }
    }

    private void requestPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                this.checkSelfPermission(Manifest.permission.READ_CONTACTS) ==
                        PackageManager.PERMISSION_GRANTED) {
            // Android version is lesser than 6.0 or the permission is already granted.
            Log.d("ZenboGoToLocation", "permission is already granted");
            return;
        }

        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            //showMessageOKCancel("You need to allow access to Contacts",
            //        new DialogInterface.OnClickListener() {
            //            @Override
            //            public void onClick(DialogInterface dialog, int which) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
            //            }
            //        });
        }
    }



    private void submitAlarm() {
        //Get Input EditText, Timepicker, CheckBox

        final String alarmName = mAlarmName.getText().toString();
        final String alarmMedName = mAlarmMedName.getText().toString();
        final String alarmMedRecom = mAlarmMedRecom.getText().toString();
        final String addReminder = reminder;

        int chosenHour, chosenMinute;

        final Calendar time = Calendar.getInstance();
        //time.set(Calendar.MINUTE, getTimePickerMinute(mTimePicker));
        //time.set(Calendar.HOUR_OF_DAY, getTimePickerHour(mTimePicker));

        final int hour = mTimePicker.getHour();
        final int minute = mTimePicker.getMinute();
        int hourFormat;
        final String timeDisplay;
        String minFormat;
        if (mTimePicker.getHour() > 12 && mTimePicker.getHour() != 12){
           hourFormat = hour - 12;
        }
        else{
            hourFormat = hour;
        }

        if (mTimePicker.getMinute() < 10){
            //minFormat = "0" + minute;
            timeDisplay = (hourFormat + ":"+ "0" + minute);
        }
        else{
            //minFormat = minute.toString()
            timeDisplay = (hourFormat + ":" + minute);
        }

       // timeDisplay = (hourFormat + ":"+ minute);

        final String ampm;
        if (mTimePicker.getHour() >= 12  ){
            ampm = "PM";
        }
        else{
            ampm = "AM";
        }

        //-------- send data via bluetooth ----------
        //String fromEtSend = etSend.getText().toString();
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        String dayofWeek = new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime());
        String fromEtSend = dayofWeek+","+ampm;
        String formated = newline + fromEtSend;
        byte[] bytes = formated.getBytes(Charset.defaultCharset());
        //byte[] bytes = etSend.getText().toString().getBytes(Charset.defaultCharset());
       // mBluetoothConnection.write(bytes);

        //TODO: put ^ this into writeToPost

        setEditingEnabled(false);
        Toast.makeText(this, "Posting Alarm...", Toast.LENGTH_SHORT).show();


        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                // [START_EXCLUDE]
                if (user == null) {
                    // User is null, error out
                    Log.e(TAG, "User " + userId + " is unexpectedly null");
                    Toast.makeText(NewAlarmActivity.this,
                            "Error: could not fetch user.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Write new post + timepicker, date check etc.
                    writeNewPost(userId, user.username, alarmName, alarmMedRecom, alarmMedName, hour, minute,ampm,timeDisplay,addReminder);
                }

                // Finish this Activity, back to the stream
                setEditingEnabled(true);
                finish();
                // [END_EXCLUDE]
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                setEditingEnabled(true);
                // [END_EXCLUDE]

            }

        });
    }

    private void setEditingEnabled(boolean enabled) {
        mAlarmName.setEnabled(enabled);
        mAlarmMedRecom.setEnabled(enabled);
        mAlarmMedName.setEnabled(enabled);
        mTimePicker.setEnabled(enabled);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final String addReminder = parent.getItemAtPosition(position).toString();
        Toast.makeText(this, "Select Item: "+ addReminder, Toast.LENGTH_SHORT).show();
        //final String addReminder = mAlarmMedRecom.getText().toString();

        // final String addReminder = mReminderSpinner

        reminder = addReminder;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "wash hand and wear mask", Toast.LENGTH_SHORT).show();

    }

    private void writeNewPost(String userId, String username, String name, String recom, String MedName, int hour, int minute, String ampm, String timeDisplay, String addReminder) {

        String key = mDatabase.child("alarms").push().getKey();
        Alarm alarm = new Alarm(userId,hour,minute,ampm,name,MedName,recom,timeDisplay,addReminder);
        Map<String, Object> alarmValues = alarm.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/alarms/" + key, alarmValues);
        childUpdates.put("/user-alarms/" + userId + "/" + key, alarmValues);

        mDatabase.updateChildren(childUpdates);

    }

}


