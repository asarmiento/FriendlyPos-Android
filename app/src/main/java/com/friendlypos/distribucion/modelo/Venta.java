package com.friendlypos.distribucion.modelo;

import com.friendlypos.principal.modelo.Clientes;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DelvoM on 06/11/2017.
 */

public class Venta extends RealmObject {

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

    @PrimaryKey
    String id;
    String invoice_id;
    String customer_id;
    String customer_name;
    String cash_desk_id;
    String sale_type;
    String viewed;
    String applied;
    String created_at;
    String updated_at;
    String reserved;
    public Clientes clientes;
    public Facturas facturas;

    private int aplicada = 0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInvoice_id() {
        return invoice_id;
    }

    public void setInvoice_id(String invoice_id) {
        this.invoice_id = invoice_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCash_desk_id() {
        return cash_desk_id;
    }

    public void setCash_desk_id(String cash_desk_id) {
        this.cash_desk_id = cash_desk_id;
    }

    public String getSale_type() {
        return sale_type;
    }

    public void setSale_type(String sale_type) {
        this.sale_type = sale_type;
    }

    public String getViewed() {
        return viewed;
    }

    public void setViewed(String viewed) {
        this.viewed = viewed;
    }

    public String getApplied() {
        return applied;
    }

    public void setApplied(String applied) {
        this.applied = applied;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    public int getAplicada() {
        return aplicada;
    }

    public void setAplicada(int aplicada) {
        this.aplicada = aplicada;
    }

    @Override
    public String toString() {
        return "Venta{" +
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
                '}';
    }
}
