package com.friendlysystemgroup.friendlypos.Recibos.modelo

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by DelvoM on 25/09/2018.
 */
open class receipts : RealmObject() {
    /*
   "customer_id":01,
           "reference":"27-0000001",
           "date":"2018-09-25",
           "sum":"prueba",
           "balance":100 000,
           "notes":"",
       */
    @PrimaryKey
    var receipts_id: String? = null

    var isResult: Boolean = false
    var code: String? = null
    var message: String? = null

    @JvmField
    var customer_id: String? = null
    @JvmField
    var reference: String? = null
    var date: String? = null
    var sum: String? = null
    @JvmField
    var balance: Double = 0.0
    var notes: String? = null
    @JvmField
    var montoCanceladoPorFactura: Double = 0.0
    @JvmField
    var numeration: String? = null

    var listaRecibos: RealmList<recibos>? = null
    @JvmField
    var porPagarReceipts: Double = 0.0
    var aplicado: Int = 0
    @JvmField
    var montoPagado: Double = 0.0

    override fun toString(): String {
        return "receipts{" +
                "receipts_id='" + receipts_id + '\'' +
                ", customer_id='" + customer_id + '\'' +
                ", reference='" + reference + '\'' +
                ", date='" + date + '\'' +
                ", sum='" + sum + '\'' +
                ", balance=" + balance +
                ", notes='" + notes + '\'' +
                ", porPagarReceipts='" + porPagarReceipts + '\'' +
                ", listaRecibos=" + listaRecibos +
                ", montoPagado=" + montoPagado +
                ", numeration=" + numeration +
                ", montoCanceladoPorFactura=" + montoCanceladoPorFactura +
                '}'
    }
}
