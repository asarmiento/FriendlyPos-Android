package com.friendlypos.principal.helpers

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver
import com.friendlypos.application.datamanager.BaseManager.Companion.api
import com.friendlypos.application.interfaces.RequestInterface
import com.friendlypos.login.util.SessionPrefes.Companion.get
import com.friendlypos.principal.activity.MenuPrincipal
import com.friendlypos.principal.modelo.EnviarClienteGPS
import com.friendlypos.principal.modelo.customer_location

/**
 * Created by DelvoM on 24/10/2018.
 */
class SubirHelperClienteGPS(private val activity: MenuPrincipal) {
    private val networkStateChangeReceiver =
        NetworkStateChangeReceiver()

    private val mContext: Context = activity
    private val mAPIService: RequestInterface? = api

    var codigo: Int = 0
    var respuestaServer: String? = null
    var codigoS: String? = null
    var mensajeS: String? = null
    var resultS: String? = null

    fun sendPostClienteGPS(facturaQuery: EnviarClienteGPS) {
        val token = "Bearer " + get(mContext).token
        Log.d("tokenCliente", "$token ")
        if (isOnline) {
            Log.d("factura1", "$facturaQuery ")
            mAPIService!!.savePostClienteGPS(facturaQuery, token)
                .enqueue(object : retrofit2.Callback<customer_location?> {
                    override fun onResponse(
                        call: retrofit2.Call<customer_location?>?,
                        response: retrofit2.Response<customer_location?>
                    ) {
                        if (response.isSuccessful()) {
                            // showResponse(response.body().toString());
                            Log.d("respVentaDirecta", response.body().toString())
                            codigo = response.code()
                            codigoS = response.body().getCode()
                            mensajeS = response.body().getMessage()
                            resultS = response.body().isResult().toString()

                            activity.codigoDeRespuestaClienteGPS(
                                codigoS!!,
                                mensajeS,
                                resultS!!,
                                codigo
                            )
                        } else {
                            Toast.makeText(mContext, "ERRRRROOOORRRR", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(
                        call: retrofit2.Call<customer_location?>?,
                        t: Throwable?
                    ) {
                        Log.e(ContentValues.TAG, "Unable to submit post to API.")
                    }
                })
        }
    }

    private val isOnline: Boolean
        get() = networkStateChangeReceiver.isNetworkAvailable(mContext)
}
