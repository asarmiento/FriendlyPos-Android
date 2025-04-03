package com.friendlypos.principal.modelo

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by DelvoM on 14/11/2017.
 */
class Sysconf : RealmObject() {
    /*  "id": 3,
                "identification": "3-101-692713",
                "name": "DISTRIBUIDORA A&V",
                "business_name": "",
                "logo": "/tmp/php4PQNQ9",
                "direction": "SAN JOAQUIN DE FLORES, HEREDIA",
                "phone": "85988444/72688823",
                "email": "ayvdistribuidoracr@gmail.com",
                "fax": "000000",
                "official_currency": "₡",
                "tax_rate": "13",
                "cost_of_return": "max",
                "initial_month": "10",
                "final_month": "09",
                "created_at": "2017-08-09 20:38:47",
                "updated_at": "2016-12-18 00:24:43",
                "type": "distribuidor"*/
    @PrimaryKey
    var id: String? = null

    @JvmField
    var identification: String? = null
    var fe: String? = null
    var data_base: String? = null
    var type_of_cedula: String? = null
    @JvmField
    var id_number_atv: String? = null
    @JvmField
    var name: String? = null
    var short_name: String? = null
    var code: String? = null
    var ftp: String? = null
    @JvmField
    var business_name: String? = null
    var logo: String? = null
    var subdomin: String? = null
    @JvmField
    var sucursal: String? = null
    @JvmField
    var direction: String? = null
    @JvmField
    var phone: String? = null
    @JvmField
    var email: String? = null
    var fax: String? = null
    var official_currency: String? = null
    var tax_rate: String? = null
    var cost_of_return: String? = null
    var initial_month: String? = null
    var final_month: String? = null
    var created_at: String? = null
    var updated_at: String? = null
    var type: String? = null

    override fun toString(): String {
        return "Sysconf{" +
                "id='" + id + '\'' +
                ", identification='" + identification + '\'' +
                ", fe='" + fe + '\'' +
                ", data_base='" + data_base + '\'' +
                ", type_of_cedula='" + type_of_cedula + '\'' +
                ", id_number_atv='" + id_number_atv + '\'' +
                ", name='" + name + '\'' +
                ", short_name='" + short_name + '\'' +
                ", code='" + code + '\'' +
                ", ftp='" + ftp + '\'' +
                ", business_name='" + business_name + '\'' +
                ", logo='" + logo + '\'' +
                ", subdomin='" + subdomin + '\'' +
                ", sucursal='" + sucursal + '\'' +
                ", direction='" + direction + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", fax='" + fax + '\'' +
                ", official_currency='" + official_currency + '\'' +
                ", tax_rate='" + tax_rate + '\'' +
                ", cost_of_return='" + cost_of_return + '\'' +
                ", initial_month='" + initial_month + '\'' +
                ", final_month='" + final_month + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", type='" + type + '\'' +
                '}'
    }
}
