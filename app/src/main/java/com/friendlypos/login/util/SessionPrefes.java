package com.friendlypos.login.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.friendlypos.login.activity.IniciarActivity;
import com.friendlypos.login.activity.LoginActivity;
import com.friendlypos.login.modelo.User;
import com.friendlypos.login.modelo.UserResponse;

public class SessionPrefes {

    public static final String PREFS_NAME = "LOGIN_PREFS";
    public static final String PREF_USER_TOKEN_TYPE = "PREF_USER_TOKEN_TYPE";
    public static final String PREF_USER_EXPIRES_IN = "PREF_USER_EXPIRES_IN";
    public static final String PREF_USER_ACCESS_TOKEN = "PREF_USER_ACCESS_TOKEN";
    public static final String PREF_USER_REFRESH_TOKEN = "PREF_USER_REFRESH_TOKEN";

    public static final String PREF_USER_NAME = "PREF_USER_NAME";
    public static final String PREF_USER_PASS = "PREF_USER_PASS";

    public static final String PREF_PREV_NUMERO = "PREF_PREV_NUMERO";

    public static final String PREF_DESCARGA_DATOS = "PREF_DESCARGA_DATOS";

    private final SharedPreferences mPrefs;

    private boolean mIsLoggedIn = false;

    private static SessionPrefes INSTANCE;

    Context _context;

    public static SessionPrefes get(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SessionPrefes(context);
        }
        return INSTANCE;
    }

    public SessionPrefes(Context context) {
        this._context = context;
        mPrefs = _context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        mIsLoggedIn = !TextUtils.isEmpty(mPrefs.getString(PREF_USER_ACCESS_TOKEN, null));
    }



    public boolean isLoggedIn() {
        return mIsLoggedIn;
    }

    public void guardarDatosUsuario(UserResponse userresp) {
        if (userresp != null) {
            //todo revisar que es PREF_USER_EXPIRES_IN
            // referencia https://www.timecalculator.net/milliseconds-to-date
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(PREF_USER_TOKEN_TYPE, userresp.getToken_type());
            editor.putString(PREF_USER_EXPIRES_IN, userresp.getExpires_in());
            editor.putString(PREF_USER_ACCESS_TOKEN, userresp.getAccess_token());
            editor.putString(PREF_USER_REFRESH_TOKEN, userresp.getRefresh_token());
            editor.apply();

            mIsLoggedIn = true;
        }
    }

    public void guardarDatosPivotPreventa(int numero) {
        if (numero != 0) {
            //todo revisar que es PREF_USER_EXPIRES_IN
            // referencia https://www.timecalculator.net/milliseconds-to-date
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putInt(PREF_PREV_NUMERO, numero);
           /* editor.putString(PREF_USER_ACCESS_TOKEN, userresp.getAccess_token());
            editor.putString(PREF_USER_REFRESH_TOKEN, userresp.getRefresh_token());*/
            editor.apply();
        }
    }

    public int getDatosPivotPreventa(){
        int numero = mPrefs.getInt(PREF_PREV_NUMERO, 0);
        return numero;
    }

    public void guardarDatosUsuarioas(String userName, String passWord) {
        if (userName != null && passWord != null ) {
            //todo revisar que es PREF_USER_EXPIRES_IN
            // referencia https://www.timecalculator.net/milliseconds-to-date
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(PREF_USER_NAME, userName);
            editor.putString(PREF_USER_PASS, passWord);
           /* editor.putString(PREF_USER_ACCESS_TOKEN, userresp.getAccess_token());
            editor.putString(PREF_USER_REFRESH_TOKEN, userresp.getRefresh_token());*/
            editor.apply();

            mIsLoggedIn = true;
        }
    }

    public String getToken(){
        return mPrefs.getString(PREF_USER_ACCESS_TOKEN, null);
    }

    public String getUsuarioPrefs(){
        String usuario = mPrefs.getString(PREF_USER_NAME, null);
        return usuario;
    }

    public int getPrefDescargaDatos(){
        int descargarDatos = mPrefs.getInt(PREF_DESCARGA_DATOS, 0);
        return descargarDatos;
    }

    public void setPrefDescargaDatos(int tipoDescarga){
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(PREF_DESCARGA_DATOS, tipoDescarga);
        editor.commit();
        editor.apply();
    }

    public String getTiempo(){
        String tiempo = mPrefs.getString(PREF_USER_EXPIRES_IN, null);
        return tiempo;
    }


    public void cerrarSesion(){
        // Limpiar todos los datos guardados del Usuario
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, IniciarActivity.class);
        // Cerrar todas las actividades
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        _context.startActivity(i);
    }
}
