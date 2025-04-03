package com.friendlypos.principal.modelo

import com.google.gson.annotations.SerializedName

/**
 * Created by DelvoM on 04/12/2018.
 */
class ConsecutivosNumberFeResponse {
    var isResult: Boolean = false
    var code: String? = null

    @SerializedName("consecutivos_number_fe")
    var consecutivosNumberFe: List<ConsecutivosNumberFe>? = null
}
