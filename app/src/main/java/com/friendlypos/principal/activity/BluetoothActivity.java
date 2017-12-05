package com.friendlypos.principal.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.friendlypos.app.broadcastreceiver.BluetoothStateChangeReceiver;
import com.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver;


public class BluetoothActivity extends AppCompatActivity implements NetworkStateChangeReceiver.InternetStateHasChange, BluetoothStateChangeReceiver.BluetoothStateHasChange {
    public boolean printer_enabled;
    public String printer;

    NetworkStateChangeReceiver networkStateChangeReceiver;
    BluetoothStateChangeReceiver bluetoothStateChangeReceiver;
    boolean init;

    public void getPreferences() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        printer_enabled = sharedPref.getBoolean("pref_usar_impresora", true);
        printer = sharedPref.getString("pref_conectar_bluetooth", "");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        init = true;

        super.onCreate(savedInstanceState);
        networkStateChangeReceiver = new NetworkStateChangeReceiver();
        bluetoothStateChangeReceiver = new BluetoothStateChangeReceiver();
        networkStateChangeReceiver.setInternetStateHasChange(this);
        bluetoothStateChangeReceiver.setBluetoothStateChangeReceiver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkStateChangeReceiver);
        unregisterReceiver(bluetoothStateChangeReceiver);
    }

    @Override
    public void networkChangedState(boolean isInternetAvailable) {
        if (init) {
            init = false;
        } else {
            if (isInternetAvailable) {
                Log.d("SALSANEQUER", "networkChangedState " + String.valueOf(isInternetAvailable));
                /*Functions.subirVentasPendientesCada5Minutos(this);*/
            }
        }
    }

    @Override
    public void bluetoothChangedState(boolean isBluetoothAvailable) {
        Log.d("SALSANEQUER", "bluetoothChangedState " + String.valueOf(isBluetoothAvailable));
        if(!isBluetoothAvailable){
            // TODO MOSTRAR UN DIALOG DE ERROR.
        }
    }
}
