package com.friendlysystemgroup.friendlypos.application.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class btPrintFile {
    private var _context: Context? = null
    private var _btMAC = ""
    private var _sFile = ""

    private val mAdapter: BluetoothAdapter
    private var mDevice: BluetoothDevice? = null

    private var mHandler: Handler? = null
    private var mState: Int

    private var mConnectThread: ConnectThread? = null
    private var mConnectedThread: ConnectedThread? = null

    constructor(context: Context?, handler: Handler?) {
        log("btPrintFile()")
        _context = context
        mHandler = handler
        mState = STATE_IDLE
        mAdapter = BluetoothAdapter.getDefaultAdapter()
        addText("btPrintFile initialized 1")
    }

    constructor(handler: Handler?, sBTmac: String, sFileName: String) {
        log("btPrintFile()")

        mHandler = handler
        _btMAC = sBTmac
        _sFile = sFileName
        mState = STATE_IDLE
        mAdapter = BluetoothAdapter.getDefaultAdapter()
        addText("btPrintFile initialized 2")
    }

    @Synchronized
    fun start() {
        log("start")
        addText("start()")
        if (mConnectThread != null) {
            mConnectThread!!.cancel()
            mConnectThread = null
        }
        if (mConnectedThread != null) {
            mConnectedThread!!.cancel()
            mConnectedThread = null
        }
        state = STATE_IDLE //idle
        addText("start done.")
    }

    @Synchronized
    fun stop() {
        log("stop")
        addText("stop()")
        if (mConnectThread != null) {
            mConnectThread!!.cancel()
            mConnectThread = null
        }
        if (mConnectedThread != null) {
            mConnectedThread!!.cancel()
            mConnectedThread = null
        }
        state = STATE_DISCONNECTED
        addText("stop() done.")
    }

    inner class MyRunnable(btSocket: BluetoothSocket?) : Runnable {
        private var socket: BluetoothSocket? = null

        init {
            this.socket = btSocket
        }

        override fun run() {
        }
    }

    @get:Synchronized
    @set:Synchronized
    var state: Int
        get() = mState
        private set(state) {
            if (D) Log.d(
                TAG,
                "setState() $mState -> $state"
            )
            mState = state

            addText(msgTypes.STATE, state)
        }

    fun printESCP(): String {
        val message = """w(          FUEL CITY
       8511 WHITESBURG DR
      HUNTSVILLE, AL 35802
         (256)585-6389

 Merchant ID: 1312
 Ref #: 0092

w)      Sale
w( XXXXXXXXXXX4003
 AMEX       Entry Method: Swiped


 Total:               $    53.22


 12/21/12               13:41:23
 Inv #: 000092 Appr Code: 565815
 Transaction ID: 001194600911275
 Apprvd: Online   Batch#: 000035


          Cutomer Copy
           Thank You!



**************************************************************"""
        return message
    }

    @Synchronized
    fun connect(device: BluetoothDevice) {
        if (D) Log.d(
            TAG,
            "connect to: $device"
        )
        addText("connecting to $device")
        mDevice = device
        if (mState == STATE_CONNECTING) {
            addText("already connected. Disconnecting first")
            if (mConnectThread != null) {
                mConnectThread!!.cancel()
                mConnectThread = null
            }
        }

        if (mConnectedThread != null) {
            mConnectedThread!!.cancel()
            mConnectedThread = null
        }
        mConnectThread = ConnectThread(device)
        mConnectThread!!.start()
        addText("new connect thread started")
        state = STATE_CONNECTING
    }

    private inner class ConnectThread(private val mmDevice: BluetoothDevice) : Thread() {
        private val mmSocket: BluetoothSocket?

        init {
            var tmp: BluetoothSocket? = null

            try {
                addText("createInsecureRfcommSocketToServiceRecord")
                tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(UUID_SPP)
            } catch (e: IOException) {
                Log.e(TAG, "create() failed", e)
            }
            mmSocket = tmp
        }

        override fun run() {
            Log.i(TAG, "BEGIN mConnectThread")
            name = "ConnectThread"

            mAdapter.cancelDiscovery()

            try {
                mmSocket!!.connect()
            } catch (e: IOException) {
                Log.e("Error BT", e.message!!)
                connectionFailed()
                try {
                    mmSocket!!.close()
                } catch (e2: IOException) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2)
                }
                this@btPrintFile.start()
                return
            }

            synchronized(this@btPrintFile) {
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
    }

    private inner class ConnectedThread(socket: BluetoothSocket) : Thread() {
        private val mmSocket: BluetoothSocket
        private val mmInStream: InputStream?
        private val mmOutStream: OutputStream?

        init {
            Log.d(TAG, "create ConnectedThread")
            mmSocket = socket
            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null

            try {
                tmpIn = socket.inputStream
                tmpOut = socket.outputStream
            } catch (e: IOException) {
                Log.e(TAG, "temp sockets not created", e)
            }

            mmInStream = tmpIn
            mmOutStream = tmpOut
        }

        override fun run() {
            Log.i(TAG, "BEGIN mConnectedThread")
            val buffer = ByteArray(1024)
            var bytes: Int

            while (true) {
                try {
                    bytes = mmInStream!!.read(buffer)
                    mHandler!!.obtainMessage(msgTypes.MESSAGE_READ, bytes, -1, buffer)
                        .sendToTarget()
                } catch (e: IOException) {
                    Log.e(TAG, "disconnected", e)
                    connectionLost()
                    break
                }
            }
        }

        fun write(buffer: ByteArray?) {
            addText("write...")
            try {
                mmOutStream!!.write(PrinterCommands.INIT)
                mmOutStream.write(PrinterCommands.SELECT_FONT_A)
                mmOutStream.write(PrinterCommands.SELECT_PRINT_SHEET)
                mmOutStream.write(buffer)
                mHandler!!.obtainMessage(msgTypes.MESSAGE_WRITE, -1, -1, buffer)
                    .sendToTarget()
            } catch (e: IOException) {
                Log.e(TAG, "Exception during write", e)
            }
            addText("write done")
        }

        fun cancel() {
            addText("cancel")
            try {
                mmSocket.close()
            } catch (e: IOException) {
                Log.e(TAG, "close() of connect socket failed", e)
            }
        }
    }

    @Synchronized
    fun connected(socket: BluetoothSocket, device: BluetoothDevice) {
        if (D) Log.d(TAG, "connected")

        if (mConnectThread != null) {
            mConnectThread!!.cancel()
            mConnectThread = null
        }

        if (mConnectedThread != null) {
            mConnectedThread!!.cancel()
            mConnectedThread = null
        }

        mConnectedThread = ConnectedThread(socket)
        mConnectedThread!!.start()

        val msg = mHandler!!.obtainMessage(msgTypes.MESSAGE_DEVICE_NAME)
        val bundle = Bundle()
        bundle.putString(msgTypes.DEVICE_NAME, device.name)
        msg.data = bundle
        mHandler!!.sendMessage(msg)

        state = STATE_CONNECTED
    }

    private fun connectionLost() {
        addText("connectionLost()")
        state = STATE_DISCONNECTED

        val msg = mHandler!!.obtainMessage(msgTypes.MESSAGE_TOAST)
        val bundle = Bundle()
        bundle.putString(msgTypes.TOAST, "El dispositivo se desconecto")
        msg.data = bundle
        mHandler!!.sendMessage(msg)
    }

    fun write(out: ByteArray?) {
        val r: ConnectedThread?
        synchronized(this) {
            if (mState != STATE_CONNECTED) return
            r = mConnectedThread
        }
        r!!.write(out)
    }

    private fun connectionFailed() {
        addText("connectionFailed()")
        state = STATE_DISCONNECTED
        val msg = mHandler!!.obtainMessage(msgTypes.MESSAGE_TOAST)
        val bundle = Bundle()
        bundle.putString(msgTypes.TOAST, "Toast: connectionFailed")
        msg.data = bundle
        mHandler!!.sendMessage(msg)
    }

    fun addText(s: String) {
        val msg = mHandler!!.obtainMessage(msgTypes.MESSAGE_INFO)
        val bundle = Bundle()
        bundle.putString(msgTypes.INFO, "INFO: $s")
        msg.data = bundle
        mHandler!!.sendMessage(msg)
    }

    fun addText(msgType: String, state: Int) {
        var type: msgTypes
        val bundle = Bundle()
        val msg = if (msgType == msgTypes.STATE) {
            mHandler!!.obtainMessage(msgTypes.MESSAGE_STATE_CHANGE) // mHandler.obtainMessage(_Activity.MESSAGE_DEVICE_NAME);
        } else if (msgType == msgTypes.DEVICE_NAME) {
            mHandler!!.obtainMessage(msgTypes.MESSAGE_DEVICE_NAME)
        } else if (msgType == msgTypes.INFO) {
            mHandler!!.obtainMessage(msgTypes.MESSAGE_INFO)
        } else if (msgType == msgTypes.TOAST) {
            mHandler!!.obtainMessage(msgTypes.MESSAGE_TOAST)
        } else if (msgType == msgTypes.READ) {
            mHandler!!.obtainMessage(msgTypes.MESSAGE_READ)
        } else if (msgType == msgTypes.WRITE) {
            mHandler!!.obtainMessage(msgTypes.MESSAGE_WRITE)
        } else {
            Message()
        }
        bundle.putInt(msgType, state)
        msg.data = bundle
        msg.arg1 = state
        mHandler!!.sendMessage(msg)
        Log.i(TAG, "addText: $msgType, state=$state")
    }

    fun log(msg: String) {
        if (D) Log.d(TAG, msg)
    }


    companion object {
        private const val TAG = "btPrintFile"
        private const val D = true

        private val UUID_SPP: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        const val STATE_IDLE: Int = 0 // we're doing nothing
        const val STATE_LISTEN: Int = 1 // now listening for incoming connections
        const val STATE_CONNECTING: Int = 2 // now initiating an outgoing connection
        const val STATE_CONNECTED: Int = 3 // now connected to a remote device
        const val STATE_DISCONNECTED: Int = 4 // now connected to a remote device
    }
}
