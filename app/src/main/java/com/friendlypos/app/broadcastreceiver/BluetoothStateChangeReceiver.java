package com.friendlypos.app.broadcastreceiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BluetoothStateChangeReceiver extends BroadcastReceiver {

    public interface BluetoothStateHasChange {
        void bluetoothChangedState(boolean isBluetoothAvailable);
    }

    BluetoothStateHasChange bluetoothStateHasChange;

    public void setBluetoothStateChangeReceiver(Context context) {
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(this, filter);
        this.bluetoothStateHasChange = (BluetoothStateHasChange) context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
        switch (state) {
            case BluetoothAdapter.STATE_OFF:
            case BluetoothAdapter.STATE_ON:
                bluetoothChangedState();
                break;
        }
    }

    public void bluetoothChangedState() {
        if (bluetoothStateHasChange != null) {
            bluetoothStateHasChange.bluetoothChangedState(isBluetoothAvailable());
        }
    }

    public boolean isBluetoothAvailable() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        return bluetoothAdapter.isEnabled();
    }
}