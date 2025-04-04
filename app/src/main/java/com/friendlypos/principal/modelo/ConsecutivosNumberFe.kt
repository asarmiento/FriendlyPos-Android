package com.friendlysystemgroup.friendlypos.principal.modelo

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by DelvoM on 04/12/2018.
 */
open class ConsecutivosNumberFe : RealmObject() {
    /*
            "id": 1,
                    "number_consecutive": 1077,
                    "number_clave": 1077,
                    "type_doc": "1",
                    "user_id": 1,
                    "api": 0,
                    "created_at": null,
                    "updated_at": "2018-11-29 11:05:08"*/
    @PrimaryKey
    var id: String? = null

    @JvmField
    var number_consecutive: Int = 0
    var number_clave: String? = null
    var type_doc: String? = null
    var user_id: String? = null
    var api: String? = null
    var created_at: String? = null
    var updated_at: String? = null

    override fun toString(): String {
        return "ConsecutivosNumberFe{" +
                "id='" + id + '\'' +
                ", number_consecutive='" + number_consecutive + '\'' +
                ", number_clave='" + number_clave + '\'' +
                ", type_doc='" + type_doc + '\'' +
                ", user_id='" + user_id + '\'' +
                ", api='" + api + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}'
    }
}
