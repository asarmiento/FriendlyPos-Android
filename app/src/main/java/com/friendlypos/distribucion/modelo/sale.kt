package com.friendlysystemgroup.friendlypos.distribucion.modelo

import com.friendlysystemgroup.friendlypos.principal.modelo.Clientes
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by DelvoM on 06/11/2017.
 */
open class sale : RealmObject() {
    /*  "sale": {
           "id": 43,
                   "invoice_id": "43",
                   "customer_id": "540",
                   "customer_name": "jarquin 2",
                   "cash_desk_id": "8",
                   "sale_type": "2",
                   "viewed": "0",
                   "applied": "1",
                   "created_at": "2017-10-24 17:52:48",
                   "updated_at": "2017-10-25 19:27:41",
                   "reserved": "0"
       }*/
    @JvmField
    @PrimaryKey
    var id: String? = null
    @JvmField
    var invoice_id: String? = null
    @JvmField
    var customer_id: String? = null
    @JvmField
    var customer_name: String? = null
    @JvmField
    var cash_desk_id: String? = null
    @JvmField
    var sale_type: String? = null
    @JvmField
    var viewed: String? = null
    @JvmField
    var applied: String? = null
    @JvmField
    var created_at: String? = null
    @JvmField
    var updated_at: String? = null
    @JvmField
    var reserved: String? = null
    var clientes: Clientes? = null
    var invoice: invoice? = null
    var fantasy: String? = null
    var company: String? = null

    @JvmField
    var devolucion: Int = 0

    @JvmField
    var aplicada: Int = 0
    @JvmField
    var subida: Int = 0
    @JvmField
    var facturaDePreventa: String? = null


    override fun toString(): String {
        return "sale{" +
                "id='" + id + '\'' +
                ", invoice_id='" + invoice_id + '\'' +
                ", customer_id='" + customer_id + '\'' +
                ", customer_name='" + customer_name + '\'' +
                ", cash_desk_id='" + cash_desk_id + '\'' +
                ", sale_type='" + sale_type + '\'' +
                ", viewed='" + viewed + '\'' +
                ", applied='" + applied + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", reserved='" + reserved + '\'' +
                ", aplicada=" + aplicada +
                ", subida=" + subida +
                ", devolucion=" + devolucion +
                ", facturaDePreventa=" + facturaDePreventa +
                ", fantasy=" + fantasy +
                ", company=" + company +
                '}'
    }
}
