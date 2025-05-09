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
import com.friendlysystemgroup.friendlypos.login.util.SessionPrefes.Companion.get

/**
 * Created by Delvo on 02/12/2017.
 */
class SubirHelper(private val activity: com.friendlysystemgroup.friendlypos.principal.activity.MenuPrincipal) {
    private val networkStateChangeReceiver = NetworkStateChangeReceiver()

    private val mContext: Context = activity
    private val mAPIService: RequestInterface? = api
    var codigo: Int = 0
    var respuestaServer: String? = null
    var codigoS: String? = null
    var mensajeS: String? = null
    var resultS: String? = null
    var codigoServer: Int = 0

    fun sendPost(facturaQuery: EnviarFactura, cantidadFactura: String?) {
        val token = "Bearer ${get(mContext).token}"
        Log.d("tokenCliente", token)
        
        if (isOnline) {
            Log.d("factura1", "$facturaQuery")
            
            mAPIService?.savePost(facturaQuery, token)
                ?.enqueue(object : retrofit2.Callback<invoice?> {
                    override fun onResponse(
                        call: retrofit2.Call<invoice?>?,
                        response: retrofit2.Response<invoice?>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let { body ->
                                Log.d("respuestaFactura", body.toString())
                                codigo = response.code()
                                codigoS = body.code
                                mensajeS = body.message
                                resultS = body.isResult.toString()
                                
                                activity.codigoDeRespuestaDistr(
                                    codigoS ?: "",
                                    mensajeS ?: "", 
                                    resultS ?: "", 
                                    codigo, 
                                    cantidadFactura
                                )
                            }
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
