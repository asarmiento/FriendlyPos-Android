package com.friendlysystemgroup.friendlypos.distribucion.modelo

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by DelvoM on 14/11/2017.
 */
open class ProductoFactura : RealmObject() {
    @PrimaryKey
    var id: String? = null

    @SerializedName("product_invoice")
    var pivot: Pivot? = null


    override fun toString(): String {
        return "Productos{" +
                "id=" + id +
                "pivot=" + pivot +
                '}'
    }
}
