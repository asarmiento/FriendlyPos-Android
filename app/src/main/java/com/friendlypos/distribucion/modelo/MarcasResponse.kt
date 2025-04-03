package com.friendlypos.distribucion.modelo

import com.google.gson.annotations.SerializedName

/**
 * Created by Delvo on 04/11/2017.
 */
class MarcasResponse {
    var isResult: Boolean = false
    var code: String? = null

    @JvmField
    @SerializedName("brands")
    var marca: List<Marcas>? = null
}
