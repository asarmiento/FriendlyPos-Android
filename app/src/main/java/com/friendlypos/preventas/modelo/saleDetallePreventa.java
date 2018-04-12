package com.friendlypos.preventas.modelo;

import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.principal.modelo.Clientes;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DelvoM on 06/11/2017.
 */

public class saleDetallePreventa{

    String p_id;
    String p_invoice_id;
    String p_customer_id;
    String p_customer_name;
    String p_cash_desk_id;
    String p_sale_type;
    String p_viewed;
    String p_applied;
    String p_created_at;
    String p_updated_at;
    String p_reserved;

    public Clientes clientes;
    public invoice invoice;

    private int aplicada = 0;
    private int subida = 0;

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public String getP_invoice_id() {
        return p_invoice_id;
    }

    public void setP_invoice_id(String p_invoice_id) {
        this.p_invoice_id = p_invoice_id;
    }

    public String getP_customer_id() {
        return p_customer_id;
    }

    public void setP_customer_id(String p_customer_id) {
        this.p_customer_id = p_customer_id;
    }

    public String getP_customer_name() {
        return p_customer_name;
    }

    public void setP_customer_name(String p_customer_name) {
        this.p_customer_name = p_customer_name;
    }

    public String getP_cash_desk_id() {
        return p_cash_desk_id;
    }

    public void setP_cash_desk_id(String p_cash_desk_id) {
        this.p_cash_desk_id = p_cash_desk_id;
    }

    public String getP_sale_type() {
        return p_sale_type;
    }

    public void setP_sale_type(String p_sale_type) {
        this.p_sale_type = p_sale_type;
    }

    public String getP_viewed() {
        return p_viewed;
    }

    public void setP_viewed(String p_viewed) {
        this.p_viewed = p_viewed;
    }

    public String getP_applied() {
        return p_applied;
    }

    public void setP_applied(String p_applied) {
        this.p_applied = p_applied;
    }

    public String getP_created_at() {
        return p_created_at;
    }

    public void setP_created_at(String p_created_at) {
        this.p_created_at = p_created_at;
    }

    public String getP_updated_at() {
        return p_updated_at;
    }

    public void setP_updated_at(String p_updated_at) {
        this.p_updated_at = p_updated_at;
    }

    public String getP_reserved() {
        return p_reserved;
    }

    public void setP_reserved(String p_reserved) {
        this.p_reserved = p_reserved;
    }

    public Clientes getClientes() {
        return clientes;
    }

    public void setClientes(Clientes clientes) {
        this.clientes = clientes;
    }

    public com.friendlypos.distribucion.modelo.invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(com.friendlypos.distribucion.modelo.invoice invoice) {
        this.invoice = invoice;
    }

    public int getAplicada() {
        return aplicada;
    }

    public void setAplicada(int aplicada) {
        this.aplicada = aplicada;
    }

    public int getSubida() {
        return subida;
    }

    public void setSubida(int subida) {
        this.subida = subida;
    }

    @Override
    public String toString() {
        return "saleDetallePreventa{" +
                "p_id='" + p_id + '\'' +
                ", p_invoice_id='" + p_invoice_id + '\'' +
                ", p_customer_id='" + p_customer_id + '\'' +
                ", p_customer_name='" + p_customer_name + '\'' +
                ", p_cash_desk_id='" + p_cash_desk_id + '\'' +
                ", p_sale_type='" + p_sale_type + '\'' +
                ", p_viewed='" + p_viewed + '\'' +
                ", p_applied='" + p_applied + '\'' +
                ", p_created_at='" + p_created_at + '\'' +
                ", p_updated_at='" + p_updated_at + '\'' +
                ", p_reserved='" + p_reserved + '\'' +
                ", aplicada=" + aplicada +
                ", subida=" + subida +
                '}';
    }
}
