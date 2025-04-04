package com.friendlysystemgroup.friendlypos.distribucion.modelo

import com.google.gson.annotations.SerializedName

/**
 * Created by DelvoM on 31/10/2017.
 */
class InventarioResponse {
    var isResult: Boolean = false
    var code: String? = null
    var message: String? = null

    @JvmField
    @SerializedName("inventory")
    var inventarios: List<Inventario>? = null
}
