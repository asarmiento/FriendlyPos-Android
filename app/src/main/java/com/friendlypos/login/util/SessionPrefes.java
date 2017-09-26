package com.friendlypos.login.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.friendlypos.login.modelo.UserResponse;

public class SessionPrefes {

    String token_type;
    String expires_in;
    String access_token;
    String refresh_token;

    public static final String PREFS_NAME = "LOGIN_PREFS";
    public static final String PREF_USER_TOKEN_TYPE = "PREF_USER_TOKEN_TYPE";
    public static final String PREF_USER_EXPIRES_IN = "PREF_USER_EXPIRES_IN";
    public static final String PREF_USER_ACCESS_TOKEN = "PREF_USER_ACCESS_TOKEN";
    public static final String PREF_USER_REFRESH_TOKEN = "PREF_USER_REFRESH_TOKEN";

    private final SharedPreferences mPrefs;

    private boolean mIsLoggedIn = false;

    private static SessionPrefes INSTANCE;

    public static SessionPrefes get(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SessionPrefes(context);
        }
        return INSTANCE;
    }

    private SessionPrefes(Context context) {
        mPrefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        mIsLoggedIn = !TextUtils.isEmpty(mPrefs.getString(PREF_USER_ACCESS_TOKEN, null));
    }

    public boolean isLoggedIn() {
        return mIsLoggedIn;
    }

    public void saveAffiliate(UserResponse userresp) {
        if (userresp != null) {
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(PREF_USER_TOKEN_TYPE, userresp.getToken_type());
            editor.putString(PREF_USER_EXPIRES_IN, userresp.getExpires_in());
            editor.putString(PREF_USER_ACCESS_TOKEN, userresp.getAccess_token());
            editor.putString(PREF_USER_REFRESH_TOKEN, userresp.getRefresh_token());
            editor.apply();

            mIsLoggedIn = true;
        }
    }

    public void logOut(){
        mIsLoggedIn = false;
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(PREF_USER_TOKEN_TYPE, null);
        editor.putString(PREF_USER_EXPIRES_IN, null);
        editor.putString(PREF_USER_ACCESS_TOKEN, null);
        editor.putString(PREF_USER_REFRESH_TOKEN, null);
        editor.apply();
    }
}
