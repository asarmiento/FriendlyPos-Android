package com.friendlysystemgroup.friendlypos.principal.activity

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.friendlysystemgroup.friendlypos.app.broadcastreceiver.BluetoothStateChangeReceiver
import com.friendlysystemgroup.friendlypos.app.broadcastreceiver.BluetoothStateChangeReceiver.BluetoothStateHasChange
import com.friendlysystemgroup.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver
import com.friendlysystemgroup.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver.InternetStateHasChange

open class BluetoothActivity : AppCompatActivity(), InternetStateHasChange,
    BluetoothStateHasChange {
    @JvmField
    var printer_enabled: Boolean = false
    @JvmField
    var printer: String? = null

    var networkStateChangeReceiver: NetworkStateChangeReceiver? = null
    @JvmField
    var bluetoothStateChangeReceiver: BluetoothStateChangeReceiver? = null
    var init: Boolean = false

    val preferences: Unit
        get() {
            val sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this)
            printer_enabled = sharedPref.getBoolean("pref_usar_impresora", true)
            printer = sharedPref.getString("pref_conectar_bluetooth", "")
        }

    public override fun onCreate(savedInstanceState: Bundle?) {
        init = true

        super.onCreate(savedInstanceState)
        networkStateChangeReceiver = NetworkStateChangeReceiver()
        bluetoothStateChangeReceiver = BluetoothStateChangeReceiver()
        networkStateChangeReceiver!!.setInternetStateHasChange(this)
        bluetoothStateChangeReceiver!!.setBluetoothStateChangeReceiver(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkStateChangeReceiver)
        unregisterReceiver(bluetoothStateChangeReceiver)
    }

    override fun networkChangedState(isInternetAvailable: Boolean) {
        if (init) {
            init = false
        } else {
            if (isInternetAvailable) {
                Log.d("SALSANEQUER", "networkChangedState $isInternetAvailable")
                /*Functions.subirVentasPendientesCada5Minutos(this);*/
            }
        }
    }

    override fun bluetoothChangedState(isBluetoothAvailable: Boolean) {
        if (isBluetoothAvailable == true) {
            Log.d("SALSANEQUER", "bluetoothChangedStateTRUE$isBluetoothAvailable")
        } else {
            Log.d("SALSANEQUER", "bluetoothChangedStateFALSE $isBluetoothAvailable")
            // Functions.CreateMessage(getApplicationContext(), "Error","El bluetooth esta desactivado");
        }
    }
}
