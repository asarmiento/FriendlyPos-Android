package com.friendlypos.login.modelo

import com.google.gson.annotations.SerializedName

/**
 * Created by DelvoM on 29/11/2017.
 */
class UsuariosResponse {
    var isResult: Boolean = false
    var code: String? = null

    @JvmField
    @SerializedName("users")
    var usuarios: List<Usuarios>? = null
}
