package com.friendlysystemgroup.friendlypos.principal.modelo

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by DelvoM on 24/10/2018.
 */
open class customer_location : RealmObject() {
    var longitud: Double = 0.0
    var latitud: Double = 0.0

    @PrimaryKey
    var id: String? = null

    var subidaEdit: Int = 0

    var isResult: Boolean = false
    var code: String? = null
    var message: String? = null


    override fun toString(): String {
        return "customer_location{" +
                "longitud=" + longitud +
                ", latitud=" + latitud +
                ", id='" + id + '\'' +
                ", subidaEdit=" + subidaEdit +
                '}'
    }
}
