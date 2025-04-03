package com.friendlypos.crearCliente.modelo

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by DelvoM on 30/10/2018.
 */
class customer_new : RealmObject() {
    @JvmField
    @PrimaryKey
    var id: Int = 0

    var idtype: String? = null
    var card: String? = null
    var fe: String? = null
    var longitud: Double = 0.0
    var latitud: Double = 0.0
    var placa: String? = null
    var model: String? = null
    var doors: String? = null
    var name: String? = null
    var email: String? = null
    var fantasy_name: String? = null
    var company_name: String? = null
    var phone: String? = null
    var credit_limit: String? = null
    var address: String? = null
    var credit_time: String? = null
    @JvmField
    var subidaNuevo: Int = 0
    var isResult: Boolean = false
    @JvmField
    var code: String? = null
    @JvmField
    var messages: String? = null


    override fun toString(): String {
        return "customer_new{" +
                "id='" + id + '\'' +
                ", idtype='" + idtype + '\'' +
                ", card='" + card + '\'' +
                ", fe='" + fe + '\'' +
                ", longitud=" + longitud +
                ", latitud=" + latitud +
                ", placa='" + placa + '\'' +
                ", model='" + model + '\'' +
                ", doors='" + doors + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", fantasy_name='" + fantasy_name + '\'' +
                ", company_name='" + company_name + '\'' +
                ", phone='" + phone + '\'' +
                ", credit_limit='" + credit_limit + '\'' +
                ", address='" + address + '\'' +
                ", credit_time='" + credit_time + '\'' +
                ", subidaNuevo=" + subidaNuevo +
                ", result=" + isResult +
                ", code='" + code + '\'' +
                ", messages='" + messages + '\'' +
                '}'
    }
}
