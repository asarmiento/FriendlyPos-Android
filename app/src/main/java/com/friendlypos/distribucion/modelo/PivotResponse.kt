package com.friendlypos.distribucion.modelo

import com.google.gson.annotations.SerializedName

/**
 * Created by DelvoM on 06/11/2017.
 */
class PivotResponse {
    var isResult: Boolean = false
    var code: String? = null
    var message: String? = null

    @SerializedName("product_invoice")
    var pivot: List<Pivot>? = null
}
