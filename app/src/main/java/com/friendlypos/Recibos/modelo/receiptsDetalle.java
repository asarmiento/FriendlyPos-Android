package com.friendlypos.Recibos.modelo;

import java.util.List;

import io.realm.RealmList;

/**
 * Created by DelvoM on 26/09/2018.
 */

public class receiptsDetalle {

    String d_receipts_id;
    String d_customer_id;
    String d_reference;
    String d_date;
    String d_sum;
    double d_balance;
    String d_notes;
    private List<recibos> d_listaRecibos;

    public String getD_receipts_id() {
        return d_receipts_id;
    }

    public void setD_receipts_id(String d_receipts_id) {
        this.d_receipts_id = d_receipts_id;
    }

    public String getD_customer_id() {
        return d_customer_id;
    }

    public void setD_customer_id(String d_customer_id) {
        this.d_customer_id = d_customer_id;
    }

    public String getD_reference() {
        return d_reference;
    }

    public void setD_reference(String d_reference) {
        this.d_reference = d_reference;
    }

    public String getD_date() {
        return d_date;
    }

    public void setD_date(String d_date) {
        this.d_date = d_date;
    }

    public String getD_sum() {
        return d_sum;
    }

    public void setD_sum(String d_sum) {
        this.d_sum = d_sum;
    }

    public double getD_balance() {
        return d_balance;
    }

    public void setD_balance(double d_balance) {
        this.d_balance = d_balance;
    }

    public String getD_notes() {
        return d_notes;
    }

    public void setD_notes(String d_notes) {
        this.d_notes = d_notes;
    }

    public List<recibos> getD_listaRecibos() {
        return d_listaRecibos;
    }

    public void setD_listaRecibos(List<recibos> d_listaRecibos) {
        this.d_listaRecibos = d_listaRecibos;
    }

    @Override
    public String toString() {
        return "receiptsDetalle{" +
                "d_receipts_id='" + d_receipts_id + '\'' +
                ", d_customer_id='" + d_customer_id + '\'' +
                ", d_reference='" + d_reference + '\'' +
                ", d_date='" + d_date + '\'' +
                ", d_sum='" + d_sum + '\'' +
                ", d_balance=" + d_balance +
                ", d_notes='" + d_notes + '\'' +
                ", d_listaRecibos=" + d_listaRecibos +
                '}';
    }
}
