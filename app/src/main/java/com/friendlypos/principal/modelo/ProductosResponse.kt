package com.friendlysystemgroup.friendlypos.principal.modelo

import com.google.gson.annotations.SerializedName

class ProductosResponse {
    var isResult: Boolean = false
    var code: String? = null

    @SerializedName("products")
    var productos: List<Productos>? = null
}
