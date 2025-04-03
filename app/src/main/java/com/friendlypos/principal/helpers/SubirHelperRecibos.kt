package com.friendlypos.principal.helpers

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.friendlypos.Recibos.modelo.EnviarRecibos
import com.friendlypos.Recibos.modelo.receipts
import com.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver
import com.friendlypos.application.datamanager.BaseManager.Companion.api
import com.friendlypos.application.interfaces.RequestInterface
import com.friendlypos.login.util.SessionPrefes.Companion.get
import com.friendlypos.principal.activity.MenuPrincipal

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
        val token = "Bearer " + get(mContext).token
        Log.d("tokenCliente", "$token ")
        if (isOnline) {
            Log.d("factura1", "$facturaQuery ")

            mAPIService!!.savePostRecibos(facturaQuery, token)
                .enqueue(object : retrofit2.Callback<receipts?> {
                    override fun onResponse(
                        call: retrofit2.Call<receipts?>?,
                        response: retrofit2.Response<receipts?>
                    ) {
                        if (response.isSuccessful()) {
                            Log.d("respRecibos", response.body().toString())

                            codigo = response.code()
                            codigoS = response.body().getCode()
                            mensajeS = response.body().getMessage()
                            resultS = response.body().isResult().toString()

                            activity.codigoDeRespuestaRecibos(
                                codigoS!!,
                                mensajeS,
                                resultS!!,
                                codigo
                            )
                        } else {
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
