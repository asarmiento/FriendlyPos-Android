package com.friendlysystemgroup.friendlypos.principal.modelo

import com.google.gson.annotations.SerializedName

/**
 * Created by DelvoM on 14/11/2017.
 */
class SysconfResponse {
    var isResult: Boolean = false
    var code: String? = null

    @SerializedName("sysconf")
    var sysconf: List<Sysconf>? = null
}
