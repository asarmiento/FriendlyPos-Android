package com.friendlysystemgroup.friendlypos.distribucion.modelo

import com.google.gson.annotations.SerializedName

/**
 * Created by DelvoM on 30/11/2017.
 */
class MetodoPagoResponse {
    var isResult: Boolean = false
    var code: String? = null
    var message: String? = null

    @JvmField
    @SerializedName("payment_methods")
    var metodoPago: List<MetodoPago>? = null
}
