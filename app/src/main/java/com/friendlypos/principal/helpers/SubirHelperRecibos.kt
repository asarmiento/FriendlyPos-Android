package com.friendlysystemgroup.friendlypos.principal.helpers

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.friendlysystemgroup.friendlypos.Recibos.modelo.EnviarRecibos
import com.friendlysystemgroup.friendlypos.Recibos.modelo.receipts
import com.friendlysystemgroup.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver
import com.friendlysystemgroup.friendlypos.application.datamanager.BaseManager.Companion.api
import com.friendlysystemgroup.friendlypos.application.interfaces.RequestInterface
import com.friendlysystemgroup.friendlypos.login.util.SessionPrefes.Companion.get
import com.friendlysystemgroup.friendlypos.principal.activity.MenuPrincipal

/**
 * Created by Delvo on 02/12/2017.
 */
class SubirHelperRecibos(private val activity: MenuPrincipal) {
    private val networkStateChangeReceiver =
        NetworkStateChangeReceiver()

    private val mContext: Context = activity
    private val mAPIService: RequestInterface? = api
    var codigo: Int = 0
    var codigoS: String? = null
    var mensajeS: String? = null
    var resultS: String? = null

    fun sendPostRecibos(facturaQuery: EnviarRecibos) {
        val token = "Bearer ${get(mContext).token}"
        Log.d("tokenCliente", token)
        
        if (isOnline) {
            Log.d("factura1", "$facturaQuery")

            mAPIService?.savePostRecibos(facturaQuery, token)
                ?.enqueue(object : retrofit2.Callback<receipts?> {
                    override fun onResponse(
                        call: retrofit2.Call<receipts?>?,
                        response: retrofit2.Response<receipts?>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let { body ->
                                Log.d("respRecibos", body.toString())

                                codigo = response.code()
                                codigoS = body.code
                                mensajeS = body.message
                                resultS = body.isResult.toString()

                                activity.codigoDeRespuestaRecibos(
                                    codigoS ?: "",
                                    mensajeS,
                                    resultS ?: "",
                                    codigo
                                )
                            }
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<receipts?>?, t: Throwable?) {
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
