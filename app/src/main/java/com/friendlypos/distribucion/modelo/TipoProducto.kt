package com.friendlysystemgroup.friendlypos.distribucion.modelo

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Delvo on 04/11/2017.
 */
open class TipoProducto : RealmObject() {
    /* {
            "id": 1,
                "name": "Gravado",
                "created_at": "-0001-11-30 00:00:00",
                "updated_at": "-0001-11-30 00:00:00"
        }*/
    @PrimaryKey
    var id: String? = null

    @JvmField
    var name: String? = null
    var created_at: String? = null
    var updated_at: String? = null

    override fun toString(): String {
        return "TipoProducto{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}'
    }
}
