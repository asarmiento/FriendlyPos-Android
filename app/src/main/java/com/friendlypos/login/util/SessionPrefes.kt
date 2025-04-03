package com.friendlypos.login.util

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.text.TextUtils
import com.friendlypos.login.activity.IniciarActivity
import com.friendlypos.login.modelo.UserResponse

class SessionPrefes(var _context: Context) {
    private val mPrefs: SharedPreferences

    var isLoggedIn: Boolean = false
        private set

    init {
        mPrefs = _context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        isLoggedIn = !TextUtils.isEmpty(mPrefs.getString(PREF_USER_ACCESS_TOKEN, null))
    }


    fun guardarDatosUsuario(userresp: UserResponse?) {
        if (userresp != null) {
            //todo revisar que es PREF_USER_EXPIRES_IN
            // referencia https://www.timecalculator.net/milliseconds-to-date
            val editor = mPrefs.edit()
            editor.putString(PREF_USER_TOKEN_TYPE, userresp.token_type)
            editor.putString(PREF_USER_EXPIRES_IN, userresp.expires_in)
            editor.putString(PREF_USER_ACCESS_TOKEN, userresp.access_token)
            editor.putString(PREF_USER_REFRESH_TOKEN, userresp.refresh_token)
            editor.apply()

            isLoggedIn = true
        }
    }

    fun guardarDatosBonus(bonus: Int) {
        val editor = mPrefs.edit()
        editor.putInt(PREF_BONUS_EXIST, bonus)
        editor.apply()
    }

    val datosBonus: Int
        get() {
            val bonus = mPrefs.getInt(PREF_BONUS_EXIST, 0)
            return bonus
        }

    fun guardarDatosPivotVentaDirecta(numero: Int) {
        if (numero != 0) {
            //todo revisar que es PREF_USER_EXPIRES_IN
            // referencia https://www.timecalculator.net/milliseconds-to-date
            val editor = mPrefs.edit()
            editor.putInt(PREF_PREV_NUMERO, numero)
            /* editor.putString(PREF_USER_ACCESS_TOKEN, userresp.getAccess_token());
            editor.putString(PREF_USER_REFRESH_TOKEN, userresp.getRefresh_token());*/
            editor.apply()
        }
    }

    val datosPivotVentaDirecta: Int
        get() {
            val numero = mPrefs.getInt(PREF_PREV_NUMERO, 0)
            return numero
        }

    fun guardarDatosPivotPreventa(numero: Int) {
        if (numero != 0) {
            //todo revisar que es PREF_USER_EXPIRES_IN
            // referencia https://www.timecalculator.net/milliseconds-to-date
            val editor = mPrefs.edit()
            editor.putInt(PREF_PREV_NUMERO, numero)
            /* editor.putString(PREF_USER_ACCESS_TOKEN, userresp.getAccess_token());
            editor.putString(PREF_USER_REFRESH_TOKEN, userresp.getRefresh_token());*/
            editor.apply()
        }
    }

    val datosPivotPreventa: Int
        get() {
            val numero = mPrefs.getInt(PREF_PREV_NUMERO, 0)
            return numero
        }


    fun guardarDatosBloquearBotonesDevolver(numero: Int) {
        val editor = mPrefs.edit()
        editor.putInt(PREF_PREV_NUMERO, numero)
        editor.apply()
    }

    val datosBloquearBotonesDevolver: Int
        get() {
            val numero = mPrefs.getInt(PREF_PREV_NUMERO, 0)
            return numero
        }


    fun guardarDatosUsuarioas(userName: String?, passWord: String?) {
        if (userName != null && passWord != null) {
            //todo revisar que es PREF_USER_EXPIRES_IN
            // referencia https://www.timecalculator.net/milliseconds-to-date
            val editor = mPrefs.edit()
            editor.putString(PREF_USER_NAME, userName)
            editor.putString(PREF_USER_PASS, passWord)
            /* editor.putString(PREF_USER_ACCESS_TOKEN, userresp.getAccess_token());
            editor.putString(PREF_USER_REFRESH_TOKEN, userresp.getRefresh_token());*/
            editor.apply()

            isLoggedIn = true
        }
    }

    val token: String?
        get() = mPrefs.getString(PREF_USER_ACCESS_TOKEN, null)

    val usuarioPrefs: String?
        get() {
            val usuario =
                mPrefs.getString(PREF_USER_NAME, null)
            return usuario
        }

    var prefDescargaDatos: Int
        get() {
            val descargarDatos = mPrefs.getInt(PREF_DESCARGA_DATOS, 0)
            return descargarDatos
        }
        set(tipoDescarga) {
            val editor = mPrefs.edit()
            editor.putInt(PREF_DESCARGA_DATOS, tipoDescarga)
            editor.commit()
            editor.apply()
        }

    val tiempo: String?
        get() {
            val tiempo =
                mPrefs.getString(PREF_USER_EXPIRES_IN, null)
            return tiempo
        }


    fun cerrarSesion() {
        // Limpiar todos los datos guardados del Usuario
        val editor = mPrefs.edit()
        editor.clear()
        editor.commit()

        val i = Intent(_context, IniciarActivity::class.java)
        // Cerrar todas las actividades
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        _context.startActivity(i)
    }

    companion object {
        const val PREFS_NAME: String = "LOGIN_PREFS"
        const val PREF_USER_TOKEN_TYPE: String = "PREF_USER_TOKEN_TYPE"
        const val PREF_USER_EXPIRES_IN: String = "PREF_USER_EXPIRES_IN"
        const val PREF_USER_ACCESS_TOKEN: String = "PREF_USER_ACCESS_TOKEN"
        const val PREF_USER_REFRESH_TOKEN: String = "PREF_USER_REFRESH_TOKEN"

        const val PREF_USER_NAME: String = "PREF_USER_NAME"
        const val PREF_USER_PASS: String = "PREF_USER_PASS"

        const val PREF_PREV_NUMERO: String = "PREF_PREV_NUMERO"
        const val PREF_BONUS_EXIST: String = "PREF_BONUS_EXIST"


        const val PREF_DESCARGA_DATOS: String = "PREF_DESCARGA_DATOS"

        private var INSTANCE: SessionPrefes? = null

        @JvmStatic
        fun get(context: Context): SessionPrefes {
            if (INSTANCE == null) {
                INSTANCE = SessionPrefes(context)
            }
            return INSTANCE!!
        }
    }
}
