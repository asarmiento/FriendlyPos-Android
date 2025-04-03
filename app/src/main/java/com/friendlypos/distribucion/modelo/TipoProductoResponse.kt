package com.friendlypos.distribucion.modelo

import com.google.gson.annotations.SerializedName

/**
 * Created by Delvo on 04/11/2017.
 */
class TipoProductoResponse {
    var isResult: Boolean = false
    var code: String? = null

    @JvmField
    @SerializedName("product_types")
    var tipoProducto: List<TipoProducto>? = null
}
