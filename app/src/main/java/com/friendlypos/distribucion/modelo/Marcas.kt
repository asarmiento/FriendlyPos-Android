package com.friendlypos.distribucion.modelo

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Delvo on 04/11/2017.
 */
class Marcas : RealmObject() {
    /*   {
               "id": 1,
                   "name": "PILSEN"
             }*/
    @PrimaryKey
    var id: String? = null
    @JvmField
    var name: String? = null

    override fun toString(): String {
        return "Marcas{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}'
    }
}
