package com.friendlypos.Recibos.modelo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DelvoM on 03/09/2018.
 */

public class Recibos extends RealmObject {
/*
     "invoice_id": 75,
             "numeration": "7",
             "date": "2017-10-25",
             "paid_up": 0,
             "customer_id": 605,
             "total": "20312.9817",
             "paid": "20313.9600"*/


    @PrimaryKey
    String invoice_id;
    String numeration;
    String date;
    String paid_up;
    String customer_id;
    double total;
    double paid;

    public String getInvoice_id() {
        return invoice_id;
    }

    public void setInvoice_id(String invoice_id) {
        this.invoice_id = invoice_id;
    }

    public String getNumeration() {
        return numeration;
    }

    public void setNumeration(String numeration) {
        this.numeration = numeration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPaid_up() {
        return paid_up;
    }

    public void setPaid_up(String paid_up) {
        this.paid_up = paid_up;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getPaid() {
        return paid;
    }

    public void setPaid(double paid) {
        this.paid = paid;
    }

    @Override
    public String toString() {
        return "Recibos{" +
                "invoice_id=" + invoice_id +
                ", numeration='" + numeration + '\'' +
                ", date='" + date + '\'' +
                ", paid_up='" + paid_up + '\'' +
                ", customer_id='" + customer_id + '\'' +
                ", total='" + total + '\'' +
                ", paid='" + paid + '\'' +
                '}';
    }
}
