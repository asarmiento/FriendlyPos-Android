package com.friendlypos.Recibos.modelo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DelvoM on 03/09/2018.
 */

public class recibos extends RealmObject {
/*
        "invoice_id": 7983,
            "numeration": "2702-0000002",
            "date": "2017-08-10",
            "paid_up": 0,
            "customer_id": 3,
            "total": "45642.07000",
            "paid": "0.0000",
            "observation": ""*/


    @PrimaryKey
    String invoice_id;
    String numeration;
    String date;
    String paid_up;
    String customer_id;

    double total;
    double paid;
    String observation;

    int abonado = 0;
    double montoCancelado= 0.0;

    public double getMontoCancelado() {
        return montoCancelado;
    }

    public void setMontoCancelado(double montoCancelado) {
        this.montoCancelado = montoCancelado;
    }

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

    public int getAbonado() {
        return abonado;
    }

    public void setAbonado(int abonado) {
        this.abonado = abonado;
    }

    public String getObservaciones() {
        return observation;
    }

    public void setObservaciones(String observaciones) {
        this.observation = observaciones;
    }

    @Override
    public String toString() {
        return "recibos{" +
                "invoice_id=" + invoice_id +
                ", numeration='" + numeration + '\'' +
                ", date='" + date + '\'' +
                ", paid_up='" + paid_up + '\'' +
                ", customer_id='" + customer_id + '\'' +
                ", total='" + total + '\'' +
                ", paid='" + paid + '\'' +
                ", observation='" + observation + '\'' +
                '}';
    }
}
