package com.friendlysystemgroup.friendlypos.distribucion.modelo

import com.google.gson.annotations.SerializedName

/**
 * Created by DelvoM on 02/11/2017.
 */
class ProductoFacturaResponse {
    var isResult: Boolean = false
    var code: String? = null
    var message: String? = null

    @SerializedName("product_invoice")
    var productofactura: List<ProductoFactura>? = null
}
