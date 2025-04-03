package com.friendlypos.distribucion.modelo

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by DelvoM on 02/11/2017.
 */
class invoice : RealmObject() {
    //TODO REVISAR ESTAS KEYS
    @JvmField
    @PrimaryKey
    var id: String? = null

    var isResult: Boolean = false
    @JvmField
    var code: String? = null
    var message: String? = null

    @JvmField
    @SerializedName("type")
    var type: String? = null

    @JvmField
    @SerializedName("branch_office_id")
    var branch_office_id: String? = null

    @JvmField
    @SerializedName("numeration")
    var numeration: String? = null

    @JvmField
    @SerializedName("key")
    var key: String? = null

    @JvmField
    @SerializedName("consecutive_number")
    var consecutive_number: String? = null

    @JvmField
    @SerializedName("longitud")
    var longitud: Double = 0.0

    @JvmField
    @SerializedName("latitud")
    var latitud: Double = 0.0

    @JvmField
    @SerializedName("date")
    var date: String? = null

    @JvmField
    @SerializedName("times")
    var times: String? = null

    @JvmField
    @SerializedName("date_presale")
    var date_presale: String? = null

    @JvmField
    @SerializedName("time_presale")
    var time_presale: String? = null

    @JvmField
    @SerializedName("due_date")
    var due_date: String? = null

    @JvmField
    @SerializedName("subtotal")
    var subtotal: String? = null

    @JvmField
    @SerializedName("subtotal_taxed")
    var subtotal_taxed: String? = null

    @JvmField
    @SerializedName("subtotal_exempt")
    var subtotal_exempt: String? = null

    @JvmField
    @SerializedName("discount")
    var discount: String? = null

    @JvmField
    @SerializedName("percent_discount")
    var percent_discount: String? = null

    @JvmField
    @SerializedName("tax")
    var tax: String? = null

    @JvmField
    @SerializedName("total")
    var total: String? = null

    @JvmField
    @SerializedName("changing")
    var changing: String? = null

    @JvmField
    @SerializedName("note")
    var note: String? = null

    @JvmField
    @SerializedName("canceled")
    var canceled: String? = null

    @JvmField
    @SerializedName("paid_up")
    var paid_up: String? = null

    @JvmField
    @SerializedName("paid")
    var paid: String? = null

    @JvmField
    @SerializedName("created_at")
    var created_at: String? = null

    @JvmField
    @SerializedName("user_id")
    var user_id: String? = null

    @JvmField
    @SerializedName("user_id_applied")
    var user_id_applied: String? = null

    @JvmField
    @SerializedName("invoice_type_id")
    var invoice_type_id: String? = null

    @JvmField
    @SerializedName("payment_method_id")
    var payment_method_id: String? = null

    @JvmField
    @SerializedName("sale")
    var sale: sale? = null

    @SerializedName("product_invoice")
    var productofactura: RealmList<Pivot>? = null

    @JvmField
    var devolucionInvoice: Int = 0

    @JvmField
    var aplicada: Int = 0
    @JvmField
    var subida: Int = 0
    @JvmField
    var facturaDePreventa: String? = null

    override fun toString(): String {
        return "invoice{" +
                "id='" + id + '\'' +
                ", result=" + isResult +
                ", code='" + code + '\'' +
                ", messages='" + message + '\'' +
                ", type='" + type + '\'' +
                ", branch_office_id='" + branch_office_id + '\'' +
                ", numeration='" + numeration + '\'' +
                ", key='" + key + '\'' +
                ", consecutive_number='" + consecutive_number + '\'' +
                ", longitud=" + longitud +
                ", latitud=" + latitud +
                ", date='" + date + '\'' +
                ", times='" + times + '\'' +
                ", date_presale='" + date_presale + '\'' +
                ", time_presale='" + time_presale + '\'' +
                ", due_date='" + due_date + '\'' +
                ", subtotal='" + subtotal + '\'' +
                ", subtotal_taxed='" + subtotal_taxed + '\'' +
                ", subtotal_exempt='" + subtotal_exempt + '\'' +
                ", discount='" + discount + '\'' +
                ", percent_discount='" + percent_discount + '\'' +
                ", tax='" + tax + '\'' +
                ", total='" + total + '\'' +
                ", changing='" + changing + '\'' +
                ", note='" + note + '\'' +
                ", canceled='" + canceled + '\'' +
                ", paid_up='" + paid_up + '\'' +
                ", paid='" + paid + '\'' +
                ", created_at='" + created_at + '\'' +
                ", user_id='" + user_id + '\'' +
                ", user_id_applied='" + user_id_applied + '\'' +
                ", invoice_type_id='" + invoice_type_id + '\'' +
                ", payment_method_id='" + payment_method_id + '\'' +
                ", sale=" + sale +
                ", productofacturas=" + productofactura +
                ", devolucionInvoice=" + devolucionInvoice +
                ", aplicada=" + aplicada +
                ", subida=" + subida +
                ", facturaDePreventa='" + facturaDePreventa + '\'' +
                '}'
    }
}
