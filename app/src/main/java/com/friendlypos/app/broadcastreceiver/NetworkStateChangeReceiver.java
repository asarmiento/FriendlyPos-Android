package com.friendlypos.app.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by juandiegoGL on 4/6/17.
 */

public class NetworkStateChangeReceiver extends BroadcastReceiver {

    public interface InternetStateHasChange {
        void networkChangedState(boolean isInternetAvailable);
    }

    private InternetStateHasChange internetStateHasChange;

    public void setInternetStateHasChange(Context context) {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(this, filter);
        this.internetStateHasChange = (InternetStateHasChange) context;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        networkChangedState(context);
    }

    public void networkChangedState(Context context) {
        if (internetStateHasChange != null) {
            internetStateHasChange.networkChangedState(isNetworkAvailable(context));
        }
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
            = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}