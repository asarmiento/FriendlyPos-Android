package com.friendlypos.distribucion.modelo

import com.google.gson.annotations.SerializedName

/**
 * Created by DelvoM on 02/11/2017.
 */
class FacturasResponse {
    var isResult: Boolean = false
    var code: String? = null
    var message: String? = null

    @JvmField
    @SerializedName("invoices")
    var facturas: List<invoice>? = null
}
