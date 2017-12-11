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

    /**
     * set URL WEBSRV
     * */
    public void setUrlWebsrv(String url){
        // Storing url in pref
        editor.putString(URL_WEBSRV, url);
        // commit changes
        editor.commit();
    }

    public String getUrlWebsrv() {
        return pref.getString(URL_WEBSRV,null);
    }


    /**
     * @return String url webservice
     */
    public Boolean getFirstTime() {
        return pref.getBoolean(FirstTime, true);
    }

    /**
     * @return Boolean FirstTime
     */
    public Boolean getBlockedApp() {
        Log.d("Blockeado", String.valueOf(pref.getBoolean(BlockedApp, false)));
        return pref.getBoolean(BlockedApp, false);
    }


    /**
     * @return String url webservice
     */
    public String getUrlLogin() {
        return pref.getString(URL_LOGIN, null);
    }

    /**
     * @return String url webservice
     */
    public String getUrlCatalog() {
        return pref.getString(URL_CATALOG,null);
    }

    /**
     * @return String url webservice
     */
    public String getUrlInvuser() {
        return pref.getString(URL_INVUSER,null);
    }

    /**
     * @return String url webservice
     */
    public String getUrlUploadChekin() {
        return pref.getString(URL_UPLOAD_CHEKIN,null);
    }

    /**
     * @return String url webservice
     */
    public String getUrlUploadSales (){
        return pref.getString(URL_UPLOAD_SALES,null);
    }

    /**
     * @return String url refound
     */
    public String getUrlRefound (){
        return pref.getString(URL_REFOUND,null);
    }

    /**
     * @return String url refound
     */
    public String getUrlCredits(){
        return pref.getString(URL_CREDITS,null);
    }

    /**
     * @return String url receipts
     */
    public String getUrlReceipts(){
        return pref.getString(URL_RECEIPTS,null);
    }


    /**
     * @return String url webservice
     */
    public String getGcmid() {
        return pref.getString(GCMID,null);
    }


    public String getUrlUploadToken() {
        return pref.getString(URL_UPLOAD_TOKEN,null);
    }

    public void setUrlToken(String urlToken) {
        editor.putString(URL_UPLOAD_TOKEN, urlToken).commit();
    }
}
