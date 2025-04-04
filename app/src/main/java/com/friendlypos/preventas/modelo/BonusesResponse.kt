package com.friendlysystemgroup.friendlypos.preventas.modelo

import com.google.gson.annotations.SerializedName

/**
 * Created by DelvoM on 26/04/2018.
 */
class BonusesResponse {
    var isResult: Boolean = false
    var code: String? = null

    @JvmField
    @SerializedName("bonuses")
    var bonuses: List<Bonuses>? = null
}
