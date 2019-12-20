package com.friendlypos.reenvio_email.modelo;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class invoices extends RealmObject {
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


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumeration() {
        return numeration;
    }

    public void setNumeration(String numeration) {
        this.numeration = numeration;
    }

    public String getConsecutive_number() {
        return consecutive_number;
    }

    public void setConsecutive_number(String consecutive_number) {
        this.consecutive_number = consecutive_number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTotal_voucher() {
        return total_voucher;
    }

    public void setTotal_voucher(double total_voucher) {
        this.total_voucher = total_voucher;
    }

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
