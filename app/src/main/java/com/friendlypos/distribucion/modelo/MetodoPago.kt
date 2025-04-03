package com.friendlypos.distribucion.modelo

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by DelvoM on 30/11/2017.
 */
class MetodoPago : RealmObject() {
    /* "id": 1,
               "name": "Contado",
            "created_at": "2014-12-24 13:01:26",
             updated_at": "-0001-11-30 00:00:00"*/
    @PrimaryKey
    var id: String? = null

    var name: String? = null
    var created_at: String? = null
    var updated_at: String? = null

    override fun toString(): String {
        return "MetodoPago{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}'
    }
}
