package com.friendlysystemgroup.friendlypos.principal.modelo

import com.google.gson.annotations.SerializedName

/**
 * Created by Podisto on 15/05/2016.
 */
class ClientesResponse {
    var isResult: Boolean = false
    var code: String? = null

    @SerializedName("customers")
    var contents: List<Clientes>? = null
}
