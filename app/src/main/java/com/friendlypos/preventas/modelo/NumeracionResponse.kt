package com.friendlysystemgroup.friendlypos.preventas.modelo

import com.google.gson.annotations.SerializedName

/**
 * Created by DelvoM on 24/04/2018.
 */
class NumeracionResponse {
    var isResult: Boolean = false
    var code: String? = null

    @JvmField
    @SerializedName("numeration")
    var numeracion: List<Numeracion>? = null
}
