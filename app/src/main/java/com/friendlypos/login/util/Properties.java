package com.friendlypos.login.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;



/**
 * Created by Desarrollo on 9/9/2015.
 */
public class Properties {

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;



    // Sharedpref file name
    private static final String PREF_NAME = "BTLSyncProp";

    // All Shared Preferences Keys
    private static final String GCMID = "GcmId";
    private static final String FirstTime = "firstTime";
    private static final String BlockedApp = "blockedapp";

    // All Shared Preferences Keys
    private static final String URL_WEBSRV = "urlWebSRV";

    private static final String URL_LOGIN = "urlLogin";

    private static final String URL_CATALOG = "urlDownloadData";

    private static final String URL_INVUSER = "urlInvUser";

    private static final String URL_UPLOAD_CHEKIN = "urlUpCheckin";

    private static final String URL_UPLOAD_SALES = "urlUpSales";

    private static final String URL_REFOUND = "urlRefound";

    private static final String URL_CREDITS = "urlCredits";

    private static final String URL_RECEIPTS = "urlUploadReceipts";

    private static final String URL_UPLOAD_TOKEN = "urlUploadToken";

    // User name (make variable public to access from outside)
    public static final String KEY_USER_ID = "id";

    // Email address (make variable public to access from outside)
    public static final String KEY_USER_NAME = "name";

    // Email address (make variable public to access from outside)
    public static final String KEY_USER_ROLE = "role";

    // Constructor
    public Properties(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(context.getPackageName(), PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setUrlWebsrv(String url){
        editor.putString(URL_WEBSRV, url);
        editor.commit();
        editor.apply();
    }

    public String getUrlWebsrv() {
        return pref.getString(URL_WEBSRV,null);
    }




}
