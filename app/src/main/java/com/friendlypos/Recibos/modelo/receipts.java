package com.friendlypos.Recibos.modelo;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DelvoM on 25/09/2018.
 */

public class receipts extends RealmObject {
/*
"customer_id":01,
        "reference":"27-0000001",
        "date":"2018-09-25",
        "sum":"prueba",
        "balance":100 000,
        "notes":"",
    */

    @PrimaryKey
    String receipts_id;

    private boolean result;
    private String code;
    private String messages;

    String customer_id;
    String reference;
    String date;
    String sum;
    double balance;
    String notes;
    double montoCanceladoPorFactura= 0.0;
    String numeration;

    private RealmList<recibos> listaRecibos;
    String porPagarReceipts;
    int aplicado = 0;
    double montoPagado = 0.0;

    public int getAplicado() {
        return aplicado;
    }

    public void setAplicado(int aplicado) {
        this.aplicado = aplicado;
    }

    public String getReceipts_id() {
        return receipts_id;
    }

    public void setReceipts_id(String receipts_id) {
        this.receipts_id = receipts_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public RealmList<recibos> getListaRecibos() {
        return listaRecibos;
    }

    public void setListaRecibos(RealmList<recibos> listaRecibos) {
        this.listaRecibos = listaRecibos;
    }

    public String getPorPagarReceipts() {
        return porPagarReceipts;
    }

    public void setPorPagarReceipts(String porPagarReceipts) {
        this.porPagarReceipts = porPagarReceipts;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return messages;
    }

    public void setMessage(String message) {
        this.messages = message;
    }

    public double getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(double montoPagado) {
        this.montoPagado = montoPagado;
    }

    public double getMontoCanceladoPorFactura() {
        return montoCanceladoPorFactura;
    }

    public void setMontoCanceladoPorFactura(double montoCanceladoPorFactura) {
        this.montoCanceladoPorFactura = montoCanceladoPorFactura;
    }

    public String getNumeration() {
        return numeration;
    }

    public void setNumeration(String numeration) {
        this.numeration = numeration;
    }

    @Override
    public String toString() {
        return "receipts{" +
                "receipts_id='" + receipts_id + '\'' +
                ", customer_id='" + customer_id + '\'' +
                ", reference='" + reference + '\'' +
                ", date='" + date + '\'' +
                ", sum='" + sum + '\'' +
                ", balance=" + balance +
                ", notes='" + notes + '\'' +
                ", porPagarReceipts='" + porPagarReceipts + '\'' +
                ", listaRecibos=" + listaRecibos +
                ", montoPagado=" + montoPagado +
                ", numeration=" + numeration +
                ", montoCanceladoPorFactura=" + montoCanceladoPorFactura +
                '}';
    }
}
