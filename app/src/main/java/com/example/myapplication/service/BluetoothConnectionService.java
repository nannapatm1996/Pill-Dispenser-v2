package com.example.myapplication.service;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothConnectionService {
    private static final String TAG = "BluetoothConnectionServ";
    private static final String appName = "MYAPP";
    //private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BluetoothAdapter mBluetoothAdapter;
    Context mContext;
    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    private BluetoothDevice mmDevice;
    private UUID deviceUUID;
    ProgressDialog mProgressDialog;

    private ConnectedThread mConnectedThread;

    public BluetoothConnectionService(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        start();
    }

    private class AcceptThread extends Thread{
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(){
            BluetoothServerSocket tmp = null;
            try {

                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);
                Log.d(TAG, "AcceptThread: Setting up server using: " + MY_UUID_INSECURE);
            }
            catch(IOException e){
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage() );
            }

            mmServerSocket = tmp;
        }
        public void run(){
            Log.d(TAG,"run:AccepThread Running.");
            BluetoothSocket socket = null;
            try {
                Log.d(TAG, "Run:RFCOM server socket start.....");
                socket = mmServerSocket.accept();
                Log.d(TAG, "Run:RFCOM server socket accepted COnntection.");
            }
            catch (IOException e){
                Log.e(TAG,"AcceptThread: IOException: "+e.getMessage());
            }

            if(socket!= null){
                connected(socket,mmDevice);
            }
            Log.i(TAG,"END mAcceptThread");
        }

        public void cancel(){
            Log.d(TAG,"cancel: Canceling AcceptThread");
            try{
                mmServerSocket.close();
            }
            catch (IOException e){
                Log.e(TAG,"cancel: Close of AcceptThread ServerSocket failed "+e.getMessage());
            }
        }
    }

    private class ConnectThread extends Thread{
        private BluetoothSocket mmSocket;
        public ConnectThread(BluetoothDevice device, UUID uuid){
            Log.d(TAG,"ConnectThread: started. ");
            mmDevice = device;
            deviceUUID = uuid;
        }

        public void run(){
            BluetoothSocket tmp = null;
            Log.i(TAG,"Run mConnectThread");

            try {
                Log.d(TAG,"ConnectThread: Trying to create InsecureRfcommSocket using UUID: "+ MY_UUID_INSECURE);
                tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(deviceUUID);
            }
            catch (IOException e) {
                Log.e(TAG,"ConnectThread: Could not create InsecureRfcommSocket using UUID: "+ e.getMessage());
            }

            mmSocket = tmp;
            mBluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
                Log.d(TAG,"run: ConnectThread connected");
            } catch (IOException e) {
                //close the socket
                try {
                    mmSocket.close();
                    Log.d(TAG,"run: Closed Socket ");
                } catch (IOException e1) {
                    Log.e(TAG,"mConnectThread: run: Unable to close COnnection in socket "+ e1.getMessage());
                }
                Log.d(TAG,"run: ConnectThread: Could not connect to UUID " + MY_UUID_INSECURE );

            }
            connected(mmSocket,mmDevice);
        }

        public void cancel(){
            Log.d(TAG,"cancel: Closing Client Socket. ");
            try{
                mmSocket.close();
            }
            catch (IOException e){
                Log.e(TAG,"cancel: close() of mmSocket in Connectthrad failed "+e.getMessage());
            }
        }

    }



    public synchronized void start(){
        Log.d(TAG,"Start");

        if(mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mInsecureAcceptThread == null){
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }
    }

    public void startClient(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startClient: started");
        mProgressDialog = ProgressDialog.show(mContext,"Connecting Bluetooth","Please Wait....",true);
        mConnectThread = new ConnectThread(device,uuid);
        mConnectThread.start();
    }

    private class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket){
            Log.d(TAG,"ConnectedThread: Starting");
            mmSocket =socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try{mProgressDialog.dismiss();
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }

            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;

            while (true){
                try {
                    bytes = mmInStream.read(buffer);
                    String incomingMessage = new String(buffer,0,bytes);
                    Log.d(TAG,"InputStream: "+incomingMessage);
                } catch (IOException e) {
                    Log.d(TAG,"reading: Error reading to inputstream. "+e.getMessage());
                    break;
                }
            }
        }

        public void write(byte[] bytes){
            //String newline = "\r\n";
            String text = new String(bytes, Charset.defaultCharset());
            //String new_text = newline + text;
            //bytes = new_text.getBytes();
            Log.d(TAG,"write: Writing to outputStream: "+text);
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.d(TAG,"write: Error writing to outputstream. "+e.getMessage());
            }
        }

        public void cancel(){
            try {
                mmSocket.close();
            }
            catch (IOException e){
            }
        }
    }

    private void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
        Log.d(TAG,"connected: Starting. ");

        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }

    public void write(byte[] out){
        ConnectThread r;

        Log.d(TAG,"write: Write Called. ");
        mConnectedThread.write(out);
    }

}
