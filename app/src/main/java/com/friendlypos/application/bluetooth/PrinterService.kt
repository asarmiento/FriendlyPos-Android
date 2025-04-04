package com.friendlysystemgroup.friendlypos.application.bluetooth

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.friendlysystemgroup.friendlypos.principal.activity.MenuPrincipal
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID


class PrinterService : Service() {
    private var mBluetoothAdapter: BluetoothAdapter? = null

    // device
    private var mConnectThread: ConnectThread? = null
    private var mMacAddress: String? = null

    private var mContext: Context? = null
    private var onSocket: BluetoothSocket? = null
    override fun onCreate() {
        Log.i(TAG, "Service started")
        this.registerReceiver(onBroadcast, IntentFilter(BROADCAST_CLASS))

        mContext = this
        mHandler2 = Handler()

        super.onCreate()
    }


    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG, "ONBIND")
        return mBinder
    }

    inner class LocalBinder : Binder() {
        val service: PrinterService
            get() = this@PrinterService
    }


    private val mBinder: IBinder = LocalBinder()


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "Onstart Command")
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val chosenDevice = intent.getStringExtra(BT_DEVICE)
        if (mBluetoothAdapter != null && chosenDevice != "") {
            val pairedDevices = mBluetoothAdapter!!.bondedDevices
            if (pairedDevices.size > 0) {
                for (d in pairedDevices) {
                    if (d.address == chosenDevice) device = d
                }
            }
            if (device == null) {
                Log.d(TAG, "No device... stopping")
                return START_STICKY_COMPATIBILITY
            }
            deviceName = device!!.name
            mMacAddress = device!!.address
            if (mMacAddress != null && mMacAddress!!.length > 0) {
                Log.d(TAG, "Connecting to: " + deviceName)
                connectToDevice(mMacAddress)
            } else {
                Log.d(TAG, "No macAddress... stopping")
                stopSelf()
                return START_STICKY_COMPATIBILITY
            }
        }

        return START_STICKY
    }

    @Synchronized
    private fun connectToDevice(macAddress: String?) {
        Log.d(TAG, "Connecting... ")
        val device = mBluetoothAdapter!!.getRemoteDevice(macAddress)
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread!!.cancel()
                mConnectThread = null
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread!!.cancel()
            mConnectedThread = null
        }
        mConnectThread = ConnectThread(device)
        mConnectThread!!.start()
        setState(STATE_CONNECTING)
    }

    private fun setState(state: Int) {
        mState = state
    }

    @Synchronized
    fun stop() {
        setState(STATE_NONE)
        mHandler2!!.removeCallbacks(reconnect)

        if (mConnectThread != null) {
            mConnectThread!!.cancel()
            mConnectThread = null
        }

        if (mConnectedThread != null) {
            mConnectedThread!!.cancel()
            mConnectedThread = null
        }
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter!!.cancelDiscovery()
        }
        stopSelf()
    }

    override fun stopService(name: Intent): Boolean {
        setState(STATE_NONE)
        if (mConnectThread != null) {
            mConnectThread!!.cancel()
            mConnectThread = null
        }

        if (mConnectedThread != null) {
            mConnectedThread!!.cancel()
            mConnectedThread = null
        }
        mBluetoothAdapter!!.cancelDiscovery()
        return super.stopService(name)
    }

    private fun connectionFailed() {
        Log.d(TAG, "Connection Failed")
        //RaceResultsDisplayService.this.stop();
        // Post to UI that connection is off
        if (mState == STATE_NONE) return
        reconnect()
    }

    fun connectionLost() {
        Log.d(TAG, "Connection Lost")
        if (mConnectThread != null) {
            mConnectThread!!.cancel()
            mConnectThread = null
        }

        if (mConnectedThread != null) {
            mConnectedThread!!.cancel()
            mConnectedThread = null
        }

        if (onSocket != null) {
            try {
                onSocket!!.close()
                onSocket = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        //RaceResultsDisplayService.this.stop();
        // Post to UI that connection is off
        reconnect()
    }

    var reconnect: Runnable = Runnable { connectToDevice(mMacAddress) }

    fun reconnect() {
        stopSelf()
        //        Log.d(TAG, "Reconnecting in 5 seconds...");
//        mHandler2.postDelayed(reconnect, 1000 * 60 * 2);
    }

    @Synchronized
    private fun connected(mmSocket: BluetoothSocket, mmDevice: BluetoothDevice) {
        Log.d(TAG, "Connected")
        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread!!.cancel()
            mConnectThread = null
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread!!.cancel()
            mConnectedThread = null
        }

        mConnectedThread = ConnectedThread(mmSocket)
        mConnectedThread!!.start()
        onSocket = mmSocket
        setState(STATE_CONNECTED)


        // Post to UI that connection is on!
    }

    private inner class ConnectThread(private val mmDevice: BluetoothDevice) : Thread() {
        private var mmSocket: BluetoothSocket? = null

        override fun run() {
            name = "ConnectThread"
            mBluetoothAdapter!!.cancelDiscovery()
            try {
                mmSocket = device!!.createRfcommSocketToServiceRecord(UUID_SPP)
                mmSocket.connect()
            } catch (e: IOException) {
                e.printStackTrace()
                try {
                    mmSocket!!.close()
                } catch (e1: IOException) {
                    e1.printStackTrace()
                }
                connectionFailed()
                return
            }
            synchronized(this@PrinterService) {
                mConnectThread = null
            }
            connected(mmSocket, mmDevice)
        }

        fun cancel() {
            try {
                mmSocket!!.close()
            } catch (e: IOException) {
                Log.e(TAG, "close() of connect socket failed", e)
            }
        }

        fun sendData(data: String) {
            mConnectedThread!!.write(data.toByteArray())
        }
    }

    private inner class ConnectedThread(socket: BluetoothSocket) : Thread() {
        //private final BluetoothSocket mmSocket;
        private val mmOutStream: OutputStream?
        private val mmInStream: InputStream?

        var buffer: ByteArray = ByteArray(256)
        var bufferLength: Int = 0

        private val last_time: Long = 0
        private val ping_sent = false

        init {
            //mmSocket = socket;
            var tmpOut: OutputStream? = null
            var tmpIn: InputStream? = null
            try {
                tmpOut = socket.outputStream
                tmpIn = socket.inputStream
            } catch (e: IOException) {
                Log.e(TAG, "temp sockets not created", e)
            }
            mmOutStream = tmpOut
            mmInStream = tmpIn
        }

        override fun run() {
            Log.i(TAG, "BEGIN mConnectedThread")
            val buffer = ByteArray(1024)
            var bytes: Int

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream!!.read(buffer)
                    // Send the obtained bytes to the UI Activity
                    mHandler!!.obtainMessage(msgTypes.MESSAGE_READ, bytes, -1, buffer)
                        .sendToTarget()
                } catch (e: IOException) {
                    Log.e(TAG, "disconnected", e)
                    connectionLost()
                    break
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
        fun write(buffer: ByteArray?) {
            try {
                mmOutStream!!.write(buffer)
            } catch (e: IOException) {
                Log.e(TAG, "Exception during write", e)
            }
        }

        fun cancel() {
            try {
                //mmSocket.close();
                setState(STATE_NONE)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        this.unregisterReceiver(onBroadcast)


        setState(STATE_NONE)

        stop()
        super.onDestroy()
    }

    // Binding for Service->UI Communication
    private val onBroadcast: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra(BROADCAST_CLASS + "TO_PRINT")) {
                if (mState != STATE_CONNECTED) return

                val extras = intent.extras
                println("is here " + extras.toString())
                val data = extras!!.getString(BROADCAST_CLASS + "TO_PRINT")
                    ?: return

                if (data == "true") {
                    try {
                        val bill = extras.getString("bill_to_print")
                        if (bill != null) {
                            mConnectedThread!!.write(bill.toByteArray())
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    companion object {
        const val CLASS_NAME: String = "com.friendlypos.application.bluetooth.PrinterService"
        const val BROADCAST_CLASS: String = "com.friendlypos.printbill"
        private val UUID_SPP: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        fun stop(context: MenuPrincipal): Boolean {
            if (context.isServiceRunning(CLASS_NAME)) {
                val serviceIntent = Intent(context, PrinterService::class.java)
                context.stopService(serviceIntent)
                return true
            }
            return false
        }


        private const val TAG = "PrintService"

        const val BT_DEVICE: String = "btdevice"
        const val STATE_NONE: Int = 0 // we're doing nothing
        const val STATE_LISTEN: Int = 1 // now listening for incoming

        // connections
        const val STATE_CONNECTING: Int = 2 // now initiating an outgoing

        // connection
        const val STATE_CONNECTED: Int = 3 // now connected to a remote
        private var mConnectedThread: ConnectedThread? = null

        var mHandler: Handler? = null
        var mHandler2: Handler? = null
        var mState: Int = STATE_NONE
        var deviceName: String? = null
        var device: BluetoothDevice? = null

        @JvmStatic
        fun startRDService(context: Context, prefExternalDisplay: String?) {
            if (prefExternalDisplay == null || prefExternalDisplay == "") return

            val serviceIntent = Intent(context, PrinterService::class.java)
            serviceIntent.putExtra(BT_DEVICE, prefExternalDisplay)
            context.startService(serviceIntent)
        }

        private val obj = Any()

        fun write(out: ByteArray?) {
            // Create temporary object
            val r: ConnectedThread?
            // Synchronize a copy of the ConnectedThread
            synchronized(obj) {
                if (mState != STATE_CONNECTED) return
                r = mConnectedThread
            }
            // Perform the write unsynchronized
            r!!.write(out)
        }
    }
}