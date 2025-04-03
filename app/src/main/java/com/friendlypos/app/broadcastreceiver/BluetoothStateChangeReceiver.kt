package com.friendlypos.app.broadcastreceiver

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class BluetoothStateChangeReceiver : BroadcastReceiver() {
    interface BluetoothStateHasChange {
        fun bluetoothChangedState(isBluetoothAvailable: Boolean)
    }

    var bluetoothStateHasChange: BluetoothStateHasChange? = null

    fun setBluetoothStateChangeReceiver(context: Context) {
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(this, filter)
        this.bluetoothStateHasChange = context as BluetoothStateHasChange
    }

    override fun onReceive(context: Context, intent: Intent) {
        val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
        when (state) {
            BluetoothAdapter.STATE_OFF, BluetoothAdapter.STATE_ON -> bluetoothChangedState()
        }
    }

    fun bluetoothChangedState() {
        if (bluetoothStateHasChange != null) {
            bluetoothStateHasChange!!.bluetoothChangedState(isBluetoothAvailable)
        }
    }

    val isBluetoothAvailable: Boolean
        get() {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            return bluetoothAdapter.isEnabled
        }
}