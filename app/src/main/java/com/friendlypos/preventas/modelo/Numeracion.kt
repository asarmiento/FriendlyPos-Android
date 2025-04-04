package com.friendlysystemgroup.friendlypos.preventas.modelo

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by DelvoM on 24/04/2018.
 */
open  class Numeracion : RealmObject() {
    @JvmField
    @PrimaryKey
    var sale_type: String? = null
    var numeracion_numero: Int = 0

    var rec_creada: Int = 0
    @JvmField
    var rec_aplicada: Int = 0

    fun setRec_creada(rec_creada: Int) {
        this.rec_creada = rec_creada
    }

    override fun toString(): String {
        return "Numeracion{" +
                "sale_type='" + sale_type + '\'' +
                ", numeracion_numero=" + numeracion_numero +
                ", rec_creada=" + rec_creada +
                ", rec_aplicada=" + rec_aplicada +
                '}'
    }
}
