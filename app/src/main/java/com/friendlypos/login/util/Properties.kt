package com.friendlypos.login.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * Created by Desarrollo on 9/9/2015.
 */
class Properties(// Context
    var _context: Context
) {
    // Shared Preferences
    var pref: SharedPreferences

    // Editor for Shared preferences
    var editor: SharedPreferences.Editor

    // Shared pref mode
    var PRIVATE_MODE: Int = 0


    // Constructor
    init {
        pref = _context.getSharedPreferences(_context.packageName, PRIVATE_MODE)
        editor = pref.edit()
    }

    var urlWebsrv: String?
        get() = pref.getString(URL_WEBSRV, null)
        set(url) {
            editor.putString(URL_WEBSRV, url)
            editor.commit()
            editor.apply()
        }

    val blockedApp: Boolean
        get() {
            Log.d(
                "Blockeado",
                pref.getBoolean(BlockedApp, false)
                    .toString()
            )
            return pref.getBoolean(
                BlockedApp,
                false
            )
        }


    companion object {
        // Sharedpref file name
        private const val PREF_NAME = "BTLSyncProp"

        // All Shared Preferences Keys
        private const val GCMID = "GcmId"
        private const val FirstTime = "firstTime"
        private const val BlockedApp = "blockedapp"

        // All Shared Preferences Keys
        private const val URL_WEBSRV = "urlWebSRV"

        private const val URL_LOGIN = "urlLogin"

        private const val URL_CATALOG = "urlDownloadData"

        private const val URL_INVUSER = "urlInvUser"

        private const val URL_UPLOAD_CHEKIN = "urlUpCheckin"

        private const val URL_UPLOAD_SALES = "urlUpSales"

        private const val URL_REFOUND = "urlRefound"

        private const val URL_CREDITS = "urlCredits"

        private const val URL_RECEIPTS = "urlUploadReceipts"

        private const val URL_UPLOAD_TOKEN = "urlUploadToken"

        // User name (make variable public to access from outside)
        const val KEY_USER_ID: String = "id"

        // Email address (make variable public to access from outside)
        const val KEY_USER_NAME: String = "name"

        // Email address (make variable public to access from outside)
        const val KEY_USER_ROLE: String = "role"
    }
}
