package com.friendlypos.reenvio_email.modelo;

import com.google.gson.annotations.SerializedName;

import io.realm.annotations.PrimaryKey;

public class invoices {
   /*
    "id": 14138,
            "numeration": "3301-0000021",
            "consecutive_number": "00100001010000001964",
            "date": "2018-10-08",
            "total_voucher": "78499.99877"*/

    @PrimaryKey
    private String id;
    @SerializedName("numeration")
    private String numeration;
    @SerializedName("consecutive_number")
    private String consecutive_number;
    @SerializedName("date")
    private String date;
    @SerializedName("total_voucher")
    private double total_voucher;

    @Override
    public String toString() {
        return "invoices{" +
                "id='" + id + '\'' +
                ", numeration='" + numeration + '\'' +
                ", consecutive_number='" + consecutive_number + '\'' +
                ", date='" + date + '\'' +
                ", total_voucher=" + total_voucher +
                '}';
    }
}
