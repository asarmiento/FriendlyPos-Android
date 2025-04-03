package com.friendlypos.principal.helpers

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver
import com.friendlypos.application.datamanager.BaseManager.Companion.api
import com.friendlypos.application.interfaces.RequestInterface
import com.friendlypos.login.util.SessionPrefes.Companion.get
import com.friendlypos.preventas.modelo.EnviarClienteVisitado
import com.friendlypos.preventas.modelo.visit
import com.friendlypos.preventas.modelo.visit.toString

/**
 * Created by Delvo on 02/12/2017.
 */
class SubirHelperClienteVisitado(private val activity: Activity) {
    private val networkStateChangeReceiver =
        NetworkStateChangeReceiver()

    private val mContext: Context = activity
    private val mAPIService: RequestInterface? = api

    fun sendPostClienteVisitado(facturaQuery: EnviarClienteVisitado) {
        val token = "Bearer " + get(mContext).token
        Log.d("tokenCliente", "$token ")
        if (isOnline) {
            Log.d("factura1", "$facturaQuery ")
            mAPIService!!.savePostClienteVisitado(facturaQuery, token)
                .enqueue(object : retrofit2.Callback<visit?> {
                    override fun onResponse(
                        call: retrofit2.Call<visit?>?,
                        response: retrofit2.Response<visit?>
                    ) {
                        if (response.isSuccessful()) {
                            // showResponse(response.body().toString());
                            Log.d("respClienteVisitado", response.body().toString())

                            if (response.code() == 200) {
                                Log.d("respClViMens", "OK")
                                Toast.makeText(mContext, "Se subio con exito", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                Log.d("respClViMens", "ERROR")
                            }
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<visit?>?, t: Throwable?) {
                        Log.e(ContentValues.TAG, "Unable to submit post to API.")
                    }
                })
        }
    }

    private val isOnline: Boolean
        get() = networkStateChangeReceiver.isNetworkAvailable(mContext)
}
