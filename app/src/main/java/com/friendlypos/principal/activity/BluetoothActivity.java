package com.friendlypos.principal.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver;


public class BluetoothActivity extends AppCompatActivity implements NetworkStateChangeReceiver.InternetStateHasChange {
    public boolean printer_enabled;
    public String printer;

    NetworkStateChangeReceiver networkStateChangeReceiver;
    boolean init;

    public void getPreferences() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        printer_enabled = sharedPref.getBoolean("pref_results_display", true);
        printer = sharedPref.getString("pref_external_display", "");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        init = true;

        super.onCreate(savedInstanceState);
        networkStateChangeReceiver = new NetworkStateChangeReceiver();
        networkStateChangeReceiver.setInternetStateHasChange(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkStateChangeReceiver);
    }

    @Override
    public void networkChangedState(boolean isInternetAvailable) {
        if (init) {
            init = false;
        } else {
            if (isInternetAvailable) {
                Log.d("JD", "networkChangedState " + String.valueOf(isInternetAvailable));
                /*Functions.subirVentasPendientesCada5Minutos(this);*/
            }
        }
    }
}
