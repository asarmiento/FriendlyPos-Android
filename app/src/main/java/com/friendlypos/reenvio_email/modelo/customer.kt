package com.friendlysystemgroup.friendlypos.reenvio_email.modelo

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class customer : RealmObject() {
    /*  "id": 8,
                 "card": "",
                 "company_name": "ALFREDO QUINTERO QUINTERO"*/
    @PrimaryKey
    var id: String? = null
    var card: String? = null
    var company_name: String? = null

    override fun toString(): String {
        return "customer{" +
                "id='" + id + '\'' +
                ", card='" + card + '\'' +
                ", company_name='" + company_name + '\'' +
                '}'
    }
}
