package com.friendlypos.application.bluetooth;


import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.friendlypos.application.FriendlyApp;
import com.friendlypos.principal.activity.MenuPrincipal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;


public class PrinterService extends Service {

    public static final String CLASS_NAME = "com.friendlypos.application.bluetooth.PrinterService";
    public static final String BROADCAST_CLASS = "com.friendlypos.printbill";
    private static final UUID UUID_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static boolean stop(MenuPrincipal context){
        if (context.isServiceRunning(CLASS_NAME)) {
            Intent serviceIntent = new Intent(context, PrinterService.class);
            context.stopService(serviceIntent);
            return true;
        }
        return false;
    }



    private static final String TAG = "PrintService";

    private BluetoothAdapter mBluetoothAdapter;
    public static final String BT_DEVICE = "btdevice";
    public static final int STATE_NONE = 0; // we're doing nothing
    public static final int STATE_LISTEN = 1; // now listening for incoming
    // connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing
    // connection
    public static final int STATE_CONNECTED = 3; // now connected to a remote
    // device
    private ConnectThread mConnectThread;
    private static ConnectedThread mConnectedThread;

    public static Handler mHandler = null;
    public static Handler mHandler2 = null;
    public static int mState = STATE_NONE;
    public static String deviceName;
    public static BluetoothDevice device = null;

    private String mMacAddress;

    private Context mContext;
    private BluetoothSocket onSocket;
    public static void startRDService(Context context, String prefExternalDisplay){
        if (prefExternalDisplay == null || prefExternalDisplay.equals("")) return;

        Intent serviceIntent = new Intent(context, PrinterService.class);
     serviceIntent.putExtra(BT_DEVICE, prefExternalDisplay);
        context.startService(serviceIntent);
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "Service started");
        this.registerReceiver(onBroadcast, new IntentFilter(PrinterService.BROADCAST_CLASS));

        mContext = this;
        mHandler2 = new Handler();

        super.onCreate();
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "ONBIND");
        return mBinder;
    }

    public class LocalBinder extends Binder {
        PrinterService getService() {
            return PrinterService.this;
        }
    }



    private final IBinder mBinder = new LocalBinder();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Onstart Command");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String chosenDevice = intent.getStringExtra(BT_DEVICE);
        if (mBluetoothAdapter != null && !chosenDevice.equals("")) {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice d : pairedDevices) {
                    if (d.getAddress().equals(chosenDevice))
                        device = d;
                }
            }
            if (device == null){
                Log.d(TAG, "No device... stopping");
                return START_STICKY_COMPATIBILITY;
            }
            deviceName = device.getName();
            mMacAddress = device.getAddress();
            if (mMacAddress != null && mMacAddress.length() > 0) {
                Log.d(TAG, "Connecting to: "+deviceName);
                connectToDevice(mMacAddress);
            } else {
                Log.d(TAG, "No macAddress... stopping");
                stopSelf();
                return START_STICKY_COMPATIBILITY;
            }
        }

        return START_STICKY;
    }

    private synchronized void connectToDevice(String macAddress) {
        Log.d(TAG, "Connecting... ");
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(macAddress);
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    private void setState(int state) {
        PrinterService.mState = state;
    }

    public synchronized void stop() {
        setState(STATE_NONE);
        mHandler2.removeCallbacks(reconnect);

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
        stopSelf();
    }

    @Override
    public boolean stopService(Intent name) {

        setState(STATE_NONE);
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        mBluetoothAdapter.cancelDiscovery();
        return super.stopService(name);
    }

    private void connectionFailed() {
        Log.d(TAG, "Connection Failed");
        //RaceResultsDisplayService.this.stop();
        // Post to UI that connection is off
        if (mState == STATE_NONE) return;
        reconnect();
    }

    public void connectionLost() {
        Log.d(TAG, "Connection Lost");
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if(onSocket !=null){
            try {
                onSocket.close();
                onSocket = null;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        //RaceResultsDisplayService.this.stop();
        // Post to UI that connection is off
        reconnect();
    }

    public Runnable reconnect = new Runnable() {
        @Override
        public void run() {
            connectToDevice(mMacAddress);
        }
    };

    public void reconnect(){
        stopSelf();
//        Log.d(TAG, "Reconnecting in 5 seconds...");
//        mHandler2.postDelayed(reconnect, 1000 * 60 * 2);
    }

    private final static Object obj = new Object();

    public static void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (obj) {
            if (mState != STATE_CONNECTED)
                return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    private synchronized void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
        Log.d(TAG, "Connected");
        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
        onSocket = mmSocket;
        setState(STATE_CONNECTED);

        // Post to UI that connection is on!


    }

    private class ConnectThread extends Thread {
        private  BluetoothSocket mmSocket;
        private  BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            this.mmDevice = device;
        }

        @Override
        public void run() {
            setName("ConnectThread");
            mBluetoothAdapter.cancelDiscovery();
            try {
                mmSocket = device.createRfcommSocketToServiceRecord(UUID_SPP);
                mmSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    mmSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                connectionFailed();
                return;

            }
            synchronized (PrinterService.this) {
                mConnectThread = null;
            }
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }

        public void sendData(String data){
            mConnectedThread.write(data.getBytes());
        }
    }

    private class ConnectedThread extends Thread {
        //private final BluetoothSocket mmSocket;
        private final OutputStream mmOutStream;
        private final InputStream mmInStream;

        byte[] buffer = new byte[256];
        int bufferLength;

        private long last_time = 0;
        private boolean ping_sent = false;

        public ConnectedThread(BluetoothSocket socket) {
            //mmSocket = socket;
            OutputStream tmpOut = null;
            InputStream tmpIn = null;
            try {
                tmpOut = socket.getOutputStream();
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }
            mmOutStream = tmpOut;
            mmInStream = tmpIn;
        }

        @Override
        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI Activity
                    mHandler.obtainMessage(msgTypes.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }
        /*public void run() {

            while (mState==STATE_CONNECTED) {


                long time = System.nanoTime();
                if (time-last_time > 10000000000L){
                    if (ping_sent) {
                        Log.d(TAG, "PING NOT RETURNED");
                        connectionLost();
                    } else {
                        last_time = time;
                        String ping = String.format("{\"type\":\"ping\",\"time\":%d}", time);
                        Log.d(TAG, ping);
                        write(ping.getBytes());
                        ping_sent = true;
                    }
                }

                try {
                    if (mmInStream.available()>0){
                        bufferLength = mmInStream.read(buffer);

                        byte[] data = new byte[bufferLength];
                        System.arraycopy(buffer, 0, data, 0, bufferLength);
                        final String response = new String(data, "UTF-8");
                        Log.d(TAG, "R:"+response);
                        System.out.println("R:"+response);
                        if (response.equals(String.format("%d", last_time))) {
                            ping_sent = false;
                        }
                    }

                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }*/


        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                //mmSocket.close();
                setState(STATE_NONE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onDestroy() {
        this.unregisterReceiver(onBroadcast);


        setState(STATE_NONE);

        stop();
        super.onDestroy();
    }

    // Binding for Service->UI Communication
    private BroadcastReceiver onBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.hasExtra(BROADCAST_CLASS+"TO_PRINT")){
                if (mState != STATE_CONNECTED) return;

                Bundle extras = intent.getExtras();
                System.out.println("is here "+ extras.toString());
                String data = extras.getString(BROADCAST_CLASS+"TO_PRINT");
                if (data == null){
                    return;
                }

                if (data.equals("true")){
                    try {
                        String bill = extras.getString("bill_to_print");
                        if(bill != null) {
                            mConnectedThread.write(bill.getBytes());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    };

}