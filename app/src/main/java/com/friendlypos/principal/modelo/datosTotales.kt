package com.friendlysystemgroup.friendlypos.principal.modelo

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by DelvoM on 15/11/2018.
 */
open class datosTotales : RealmObject() {
    @JvmField
    @PrimaryKey
    var id: Int = 0
    @JvmField
    var idTotal: Int = 0
    @JvmField
    var nombreTotal: String? = null
    var totalDistribucion: Double = 0.0
    @JvmField
    var totalVentaDirecta: Double = 0.0
    var totalPreventa: Double = 0.0
    var totalProforma: Double = 0.0
    @JvmField
    var totalRecibos: Double = 0.0
    @JvmField
    var date: String? = null

    override fun toString(): String {
        return "datosTotales{" +
                "id=" + id +
                ", idTotal=" + idTotal +
                ", nombreTotal='" + nombreTotal + '\'' +
                ", totalDistribucion=" + totalDistribucion +
                ", totalVentaDirecta=" + totalVentaDirecta +
                ", totalPreventa=" + totalPreventa +
                ", totalProforma=" + totalProforma +
                ", totalRecibos=" + totalRecibos +
                ", date=" + date +
                '}'
    }
}
