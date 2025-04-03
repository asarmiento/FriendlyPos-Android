package com.friendlypos.distribucion.modelo

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by DelvoM on 31/10/2017.
 */
class Inventario : RealmObject() {
    @JvmField
    @PrimaryKey
    var id: Int = 0
    @JvmField
    var product_id: String? = null
    @JvmField
    var initial: String? = null
    @JvmField
    var amount: String? = null
    @JvmField
    var amount_dist: String? = null
    @JvmField
    var distributor: String? = null
    var description: String? = null
    var barcode: String? = null

    @JvmField
    var devuelvo: Int = 0

    var isResult: Boolean = false
    @JvmField
    var code: String? = null
    @JvmField
    var messages: String? = null


    override fun toString(): String {
        return "Inventario{" +
                "id=" + id +
                ", product_id='" + product_id + '\'' +
                ", initial='" + initial + '\'' +
                ", amount='" + amount + '\'' +
                ", amount_dist='" + amount_dist + '\'' +
                ", distributor='" + distributor + '\'' +
                ", descripcion='" + description + '\'' +
                ", barcode='" + barcode + '\'' +
                ", devuelvo=" + devuelvo +
                ", result=" + isResult +
                ", code='" + code + '\'' +
                ", messages='" + messages + '\'' +
                '}'
    }
}
