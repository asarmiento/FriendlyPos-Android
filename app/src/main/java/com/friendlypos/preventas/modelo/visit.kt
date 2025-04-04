package com.friendlysystemgroup.friendlypos.preventas.modelo

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by DelvoM on 16/04/2018.
 */
open class visit : RealmObject() {
    /* {
            "id": 1,
                "name": "Gravado",
                "created_at": "-0001-11-30 00:00:00",
                "updated_at": "-0001-11-30 00:00:00"
        }*/
    @JvmField
    @PrimaryKey
    var id: Int = 0
    @JvmField
    var customer_id: String? = null
    @JvmField
    var visit: String? = null
    @JvmField
    var observation: String? = null
    @JvmField
    var date: String? = null
    @JvmField
    var longitud: Double = 0.0
    @JvmField
    var latitud: Double = 0.0
    @JvmField
    var user_id: String? = null

    @JvmField
    var tipoVisitado: String? = null


    @JvmField
    var subida: Int = 0

    override fun toString(): String {
        return "visit{" +
                "id=" + id +
                ", customer_id='" + customer_id + '\'' +
                ", visit='" + visit + '\'' +
                ", observation='" + observation + '\'' +
                ", date='" + date + '\'' +
                ", longitud=" + longitud +
                ", latitud=" + latitud +
                ", user_id='" + user_id + '\'' +
                ", subida='" + subida + '\'' +
                ", tipoVisitado='" + tipoVisitado + '\'' +
                '}'
    }
}
