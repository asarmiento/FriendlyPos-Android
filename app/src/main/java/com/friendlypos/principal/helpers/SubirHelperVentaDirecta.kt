package com.friendlypos.principal.helpers

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver
import com.friendlypos.application.datamanager.BaseManager.Companion.api
import com.friendlypos.application.interfaces.RequestInterface
import com.friendlypos.distribucion.modelo.EnviarFactura
import com.friendlypos.distribucion.modelo.invoice
import com.friendlypos.distribucion.modelo.invoice.isResult
import com.friendlypos.distribucion.modelo.invoice.message
import com.friendlypos.distribucion.modelo.invoice.toString
import com.friendlypos.login.util.SessionPrefes.Companion.get
import com.friendlypos.principal.activity.MenuPrincipal

/**
 * Created by Delvo on 02/12/2017.
 */
class SubirHelperVentaDirecta(private val activity: MenuPrincipal) {
    private val networkStateChangeReceiver =
        NetworkStateChangeReceiver()

    private val mContext: Context = activity
    private val mAPIService: RequestInterface? = api
    var codigo: Int = 0
    var respuestaServer: String? = null
    var codigoS: String? = null
    var mensajeS: String? = null
    var resultS: String? = null
    var idFacturaSubida: String? = null
    var codigoServer: Int = 0

    fun sendPostVentaDirecta(facturaQuery: EnviarFactura, cantidadFactura: String?) {
        val token = "Bearer " + get(mContext).token
        Log.d("tokenCliente", "$token ")
        if (isOnline) {
            Log.d("factura1", "$facturaQuery ")

            mAPIService!!.savePostVentaDirecta(facturaQuery, token)
                .enqueue(object : retrofit2.Callback<invoice?> {
                    override fun onResponse(
                        call: retrofit2.Call<invoice?>?,
                        response: retrofit2.Response<invoice?>
                    ) {
                        if (response.isSuccessful()) {
                            idFacturaSubida = response.body().id
                            // showResponse(response.body().toString());
                            Log.d("respVentaDirecta", response.body().toString())
                            codigo = response.code()
                            codigoS = response.body().code
                            mensajeS = response.body().message
                            resultS = response.body().isResult.toString()

                            activity.codigoDeRespuestaVD(
                                codigoS!!,
                                mensajeS!!, resultS!!, codigo, cantidadFactura
                            )

                            Log.d("respVentaDirecta", response.body().toString())
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
