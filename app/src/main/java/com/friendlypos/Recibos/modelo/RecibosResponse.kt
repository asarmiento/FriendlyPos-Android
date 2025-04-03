package com.friendlypos.Recibos.modelo

import com.google.gson.annotations.SerializedName

/**
 * Created by DelvoM on 03/09/2018.
 */
class RecibosResponse {
    var isResult: Boolean = false
    var code: String? = null
    var message: String? = null

    @SerializedName("invoices")
    var recibos: List<recibos>? = null
}

