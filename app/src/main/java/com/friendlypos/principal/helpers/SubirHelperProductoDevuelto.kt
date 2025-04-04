package com.friendlysystemgroup.friendlypos.principal.helpers

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.friendlysystemgroup.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver
import com.friendlysystemgroup.friendlypos.application.datamanager.BaseManager.Companion.api
import com.friendlysystemgroup.friendlypos.application.interfaces.RequestInterface
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Inventario
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Inventario.isResult
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Inventario.toString
import com.friendlysystemgroup.friendlypos.login.util.SessionPrefes.Companion.get
import com.friendlysystemgroup.friendlypos.principal.activity.MenuPrincipal
import com.friendlysystemgroup.friendlypos.principal.modelo.EnviarProductoDevuelto

/**
 * Created by Delvo on 02/12/2017.
 */
class SubirHelperProductoDevuelto(private val activity: MenuPrincipal) {
    private val networkStateChangeReceiver =
        NetworkStateChangeReceiver()

    private val mContext: Context = activity
    private val mAPIService: RequestInterface? = api
    var codigoServer: Int = 0
    var codigo: Int = 0
    var codigoS: String? = null
    var mensajeS: String? = null
    var resultS: String? = null

    fun sendPostClienteProductoDevuelto(
        facturaQuery: EnviarProductoDevuelto,
        cantidadFactura: Int
    ) {
        val token = "Bearer " + get(mContext).token
        Log.d("tokenCliente", "$token ")
        if (isOnline) {
            Log.d("factura1", "$facturaQuery ")
            mAPIService!!.savePostProductoDevuelto(facturaQuery, token)
                .enqueue(object : retrofit2.Callback<Inventario?> {
                    override fun onResponse(
                        call: retrofit2.Call<Inventario?>?,
                        response: retrofit2.Response<Inventario?>
                    ) {
                        if (response.isSuccessful()) {
                            Log.d("respPreventa", response.body().toString())

                            codigo = response.code()
                            codigoS = response.body().code
                            mensajeS = response.body().messages
                            resultS = response.body().isResult.toString()

                            activity.codigoDeRespuestaProductoDevuelto(
                                codigoS!!, mensajeS,
                                resultS!!, codigo, cantidadFactura
                            )
                            Log.d("codID", cantidadFactura.toString() + "")
                        } else {
                            Log.d("respPreventa", "Error")
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<Inventario?>?, t: Throwable?) {
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
