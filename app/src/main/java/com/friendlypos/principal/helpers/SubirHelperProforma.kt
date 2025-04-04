package com.friendlysystemgroup.friendlypos.principal.helpers

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.friendlysystemgroup.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver
import com.friendlysystemgroup.friendlypos.application.datamanager.BaseManager.Companion.api
import com.friendlysystemgroup.friendlypos.application.interfaces.RequestInterface
import com.friendlysystemgroup.friendlypos.distribucion.modelo.EnviarFactura
import com.friendlysystemgroup.friendlypos.distribucion.modelo.invoice
import com.friendlysystemgroup.friendlypos.distribucion.modelo.invoice.isResult
import com.friendlysystemgroup.friendlypos.distribucion.modelo.invoice.message
import com.friendlysystemgroup.friendlypos.distribucion.modelo.invoice.toString
import com.friendlysystemgroup.friendlypos.login.util.SessionPrefes.Companion.get
import com.friendlysystemgroup.friendlypos.principal.activity.MenuPrincipal

/**
 * Created by Delvo on 02/12/2017.
 */
class SubirHelperProforma(private val activity: MenuPrincipal) {
    private val networkStateChangeReceiver =
        NetworkStateChangeReceiver()

    private val mContext: Context = activity
    private val mAPIService: RequestInterface? = api
    var codigoS: String? = null
    var mensajeS: String? = null
    var resultS: String? = null
    var codigoServer: Int = 0
    var codigo: Int = 0

    fun sendPostProforma(facturaQuery: EnviarFactura, cantidadFactura: String?) {
        val token = "Bearer " + get(mContext).token
        Log.d("tokenCliente", "$token ")
        if (isOnline) {
            Log.d("factura1", "$facturaQuery ")
            mAPIService!!.savePostProforma(facturaQuery, token)
                .enqueue(object : retrofit2.Callback<invoice?> {
                    override fun onResponse(
                        call: retrofit2.Call<invoice?>?,
                        response: retrofit2.Response<invoice?>
                    ) {
                        if (response.isSuccessful()) {
                            Log.d("respProforma", response.body().toString())
                            codigo = response.code()
                            codigoS = response.body().code
                            mensajeS = response.body().message
                            resultS = response.body().isResult.toString()
                            activity.codigoDeRespuestaProforma(
                                codigoS!!, mensajeS,
                                resultS!!, codigo, cantidadFactura
                            )
                        } else {
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<invoice?>?, t: Throwable?) {
                        Log.e(ContentValues.TAG, "Unable to submit post to API.")
                    }
                })
        } else {
            Toast.makeText(
                activity,
                "Error, por favor revisar conexión de Internet",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val isOnline: Boolean
        get() = networkStateChangeReceiver.isNetworkAvailable(mContext)
}
