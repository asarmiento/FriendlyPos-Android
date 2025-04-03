package com.friendlypos.Recibos.modelo

/**
 * Created by DelvoM on 26/09/2018.
 */
class receiptsDetalle {
    var d_receipts_id: String? = null
    var d_customer_id: String? = null
    var d_reference: String? = null
    var d_date: String? = null
    var d_sum: String? = null
    var d_balance: Double = 0.0
    var d_notes: String? = null
    var d_listaRecibos: List<recibos>? = null

    override fun toString(): String {
        return "receiptsDetalle{" +
                "d_receipts_id='" + d_receipts_id + '\'' +
                ", d_customer_id='" + d_customer_id + '\'' +
                ", d_reference='" + d_reference + '\'' +
                ", d_date='" + d_date + '\'' +
                ", d_sum='" + d_sum + '\'' +
                ", d_balance=" + d_balance +
                ", d_notes='" + d_notes + '\'' +
                ", d_listaRecibos=" + d_listaRecibos +
                '}'
    }
}
