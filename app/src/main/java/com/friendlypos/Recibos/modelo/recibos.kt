package com.friendlypos.Recibos.modelo

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey

/**
 * Created by DelvoM on 03/09/2018.
 */
class recibos : RealmObject() {
    /*
            "invoice_id": 7983,
                "numeration": "2702-0000002",
                "date": "2017-08-10",
                "paid_up": 0,
                "customer_id": 3,
                "total": "45642.07000",
                "paid": "0.0000",
                "observation": ""*/
    @JvmField
    @PrimaryKey
    var invoice_id: String? = null
    @JvmField
    var numeration: String? = null
    var date: String? = null
    var paid_up: String? = null

    @Index
    var customer_id: String? = null

    @JvmField
    var total: Double = 0.0
    @JvmField
    var paid: Double = 0.0
    var observaciones: String? = null
    var referencia_receipts: String? = null

    var abonado: Int = 0
    var mostrar: Int = 0
    var montoCancelado: Double = 0.0
    var montoCanceladoPorFactura: Double = 0.0
    var isSelected: Boolean = false

    @JvmField
    var porPagar: Double = 0.0


    override fun toString(): String {
        return "recibos{" +
                "invoice_id='" + invoice_id + '\'' +
                ", numeration='" + numeration + '\'' +
                ", date='" + date + '\'' +
                ", paid_up='" + paid_up + '\'' +
                ", customer_id='" + customer_id + '\'' +
                ", total=" + total +
                ", paid=" + paid +
                ", observation='" + observaciones + '\'' +
                ", referencia_receipts='" + referencia_receipts + '\'' +
                ", abonado=" + abonado +
                ", mostrar=" + mostrar +
                ", montoCancelado=" + montoCancelado +
                ", montoCanceladoPorFactura=" + montoCanceladoPorFactura +
                ", porPagar=" + porPagar +
                '}'
    }
}
