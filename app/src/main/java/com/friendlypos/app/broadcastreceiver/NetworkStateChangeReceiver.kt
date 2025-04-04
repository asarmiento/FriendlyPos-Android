package com.friendlysystemgroup.friendlypos.app.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager

class NetworkStateChangeReceiver : BroadcastReceiver() {
    interface InternetStateHasChange {
        fun networkChangedState(isInternetAvailable: Boolean)
    }

    private var internetStateHasChange: InternetStateHasChange? = null

    fun setInternetStateHasChange(context: Context) {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(this, filter)
        this.internetStateHasChange = context as InternetStateHasChange
    }

    override fun onReceive(context: Context, intent: Intent) {
        networkChangedState(context)
    }

    fun networkChangedState(context: Context) {
        if (internetStateHasChange != null) {
            internetStateHasChange!!.networkChangedState(isNetworkAvailable(context))
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }
}