package com.friendlysystemgroup.friendlypos.principal.helpers

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.friendlysystemgroup.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver
import com.friendlysystemgroup.friendlypos.application.datamanager.BaseManager.Companion.api
import com.friendlysystemgroup.friendlypos.application.interfaces.RequestInterface
import com.friendlysystemgroup.friendlypos.crearCliente.modelo.customer_new
import com.friendlysystemgroup.friendlypos.login.util.SessionPrefes.Companion.get
import com.friendlysystemgroup.friendlypos.principal.activity.MenuPrincipal
import com.friendlysystemgroup.friendlypos.principal.modelo.EnviarClienteNuevo

/**
 * Created by DelvoM on 24/10/2018.
 */
class SubirHelperClienteNuevo(private val activity: MenuPrincipal) {
    private val networkStateChangeReceiver = NetworkStateChangeReceiver()

    private val mContext: Context = activity
    private val mAPIService: RequestInterface? = api

    var codigo: Int = 0
    var respuestaServer: String? = null
    var codigoS: String? = null
    var mensajeS: String? = null
    var resultS: String? = null

    fun sendPostClienteNuevo(facturaQuery: EnviarClienteNuevo) {
        val token = "Bearer ${get(mContext).token}"
        Log.d("tokenCliente", token)
        
        if (isOnline) {
            Log.d("factura1", "$facturaQuery")
            
            mAPIService?.savePostClienteNuevo(facturaQuery, token)
                ?.enqueue(object : retrofit2.Callback<customer_new?> {
                    override fun onResponse(
                        call: retrofit2.Call<customer_new?>?,
                        response: retrofit2.Response<customer_new?>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let { body ->
                                Log.d("respClienteNuevo", body.toString())
                                
                                codigo = response.code()
                                codigoS = body.code
                                mensajeS = body.messages
                                resultS = body.isResult.toString()

                                activity.codigoDeRespuestaClienteNuevo(
                                    codigoS ?: "", 
                                    mensajeS,
                                    resultS ?: "", 
                                    codigo
                                )
                            }
                        } else {
                            Toast.makeText(mContext, "ERROR", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<customer_new?>?, t: Throwable?) {
                        Log.e(ContentValues.TAG, "Unable to submit post to API.")
                    }
                })
        }
    }

    private val isOnline: Boolean
        get() = networkStateChangeReceiver.isNetworkAvailable(mContext)
}
