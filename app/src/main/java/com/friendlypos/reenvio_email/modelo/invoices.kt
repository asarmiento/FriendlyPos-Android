package com.friendlypos.reenvio_email.modelo

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

class invoices : RealmObject() {
    /*
       "id": 14138,
               "numeration": "3301-0000021",
               "consecutive_number": "00100001010000001964",
               "date": "2018-10-08",
               "total_voucher": "78499.99877"*/
    @PrimaryKey
    var id: String? = null

    @SerializedName("numeration")
    var numeration: String? = null

    @SerializedName("consecutive_number")
    var consecutive_number: String? = null

    @SerializedName("date")
    var date: String? = null

    @SerializedName("total_voucher")
    var total_voucher: Double = 0.0


    override fun toString(): String {
        return "invoices{" +
                "id='" + id + '\'' +
                ", numeration='" + numeration + '\'' +
                ", consecutive_number='" + consecutive_number + '\'' +
                ", date='" + date + '\'' +
                ", total_voucher=" + total_voucher +
                '}'
    }
}
