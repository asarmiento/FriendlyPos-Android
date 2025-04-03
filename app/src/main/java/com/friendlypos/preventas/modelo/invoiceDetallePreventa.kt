package com.friendlypos.preventas.modelo

import com.friendlypos.distribucion.modelo.Pivot
import com.friendlypos.distribucion.modelo.sale

/**
 * Created by DelvoM on 19/03/2018.
 */
class invoiceDetallePreventa {
    var p_id: Int = 0
    var p_branch_office_id: String? = null
    var p_numeration: String? = null

    var p_longitud: Double = 0.0

    var p_latitud: Double = 0.0

    var p_date: String? = null

    var p_times: String? = null

    var p_date_presale: String? = null

    var p_time_presale: String? = null

    var p_due_date: String? = null

    var p_subtotal: String? = null

    var p_subtotal_taxed: String? = null
    var p_subtotal_exempt: String? = null
    var p_discount: String? = null

    var p_percent_discount: String? = null

    var p_tax: String? = null

    var p_total: String? = null

    var p_changing: String? = null
    var p_note: String? = null

    var p_canceled: String? = null
    var p_paid_up: String? = null

    var p_paid: String? = null

    var p_created_at: String? = null

    var p_user_id: String? = null

    var p_user_id_applied: String? = null

    var p_invoice_type_id: String? = null

    var p_sale: sale? = null

    var p_productofacturas: List<Pivot>? = null

    var facturaDePreventa: String? = null


    fun setP_payment_method_id(p_payment_method_id: String?) {
        Companion.p_payment_method_id = p_payment_method_id
    }

    override fun toString(): String {
        return "InvoiceDetallePreventa{" +
                "p_id=" + p_id +
                ", p_branch_office_id='" + p_branch_office_id + '\'' +
                ", p_numeration='" + p_numeration + '\'' +
                ", p_longitud=" + p_longitud +
                ", p_latitud=" + p_latitud +
                ", p_date='" + p_date + '\'' +
                ", p_times='" + p_times + '\'' +
                ", p_date_presale='" + p_date_presale + '\'' +
                ", p_time_presale='" + p_time_presale + '\'' +
                ", p_due_date='" + p_due_date + '\'' +
                ", p_subtotal='" + p_subtotal + '\'' +
                ", p_subtotal_taxed='" + p_subtotal_taxed + '\'' +
                ", p_subtotal_exempt='" + p_subtotal_exempt + '\'' +
                ", p_discount='" + p_discount + '\'' +
                ", p_percent_discount='" + p_percent_discount + '\'' +
                ", p_tax='" + p_tax + '\'' +
                ", p_total='" + p_total + '\'' +
                ", p_changing='" + p_changing + '\'' +
                ", p_note='" + p_note + '\'' +
                ", p_canceled='" + p_canceled + '\'' +
                ", p_paid_up='" + p_paid_up + '\'' +
                ", p_paid='" + p_paid + '\'' +
                ", p_created_at='" + p_created_at + '\'' +
                ", p_user_id='" + p_user_id + '\'' +
                ", p_user_id_applied='" + p_user_id_applied + '\'' +
                ", p_invoice_type_id='" + p_invoice_type_id + '\'' +
                ", p_payment_method_id='" + p_payment_method_id + '\'' +
                ", p_sale=" + p_sale +
                ", p_productofacturas=" + p_productofacturas +
                ",facturaDePreventa=" + facturaDePreventa +
                '}'
    }

    companion object {
        var p_payment_method_id: String? = null
            private set
    }
}
