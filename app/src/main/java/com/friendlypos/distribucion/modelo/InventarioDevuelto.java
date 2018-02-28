package com.friendlypos.distribucion.modelo;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DelvoM on 31/10/2017.
 */

public class InventarioDevuelto extends RealmObject {

    @PrimaryKey
    int id_devuelto;
    String product_id_devuelto;
    String initial_devuelto;
    String amount_devuelto;
    String amount_dist_devuelto;
    String distributor_devuelto;
    String nombre_producto_devuelto;
    String date_devuelto;
    String times_devuelto;

    public int getId_devuelto() {
        return id_devuelto;
    }

    public void setId_devuelto(int id_devuelto) {
        this.id_devuelto = id_devuelto;
    }

    public String getProduct_id_devuelto() {
        return product_id_devuelto;
    }

    public void setProduct_id_devuelto(String product_id_devuelto) {
        this.product_id_devuelto = product_id_devuelto;
    }

    public String getInitial_devuelto() {
        return initial_devuelto;
    }

    public void setInitial_devuelto(String initial_devuelto) {
        this.initial_devuelto = initial_devuelto;
    }

    public String getAmount_devuelto() {
        return amount_devuelto;
    }

    public void setAmount_devuelto(String amount_devuelto) {
        this.amount_devuelto = amount_devuelto;
    }

    public String getAmount_dist_devuelto() {
        return amount_dist_devuelto;
    }

    public void setAmount_dist_devuelto(String amount_dist_devuelto) {
        this.amount_dist_devuelto = amount_dist_devuelto;
    }

    public String getDistributor_devuelto() {
        return distributor_devuelto;
    }

    public void setDistributor_devuelto(String distributor_devuelto) {
        this.distributor_devuelto = distributor_devuelto;
    }

    public String getNombre_producto_devuelto() {
        return nombre_producto_devuelto;
    }

    public void setNombre_producto_devuelto(String nombre_producto_devuelto) {
        this.nombre_producto_devuelto = nombre_producto_devuelto;
    }

    public String getDate_devuelto() {
        return date_devuelto;
    }

    public void setDate_devuelto(String date_devuelto) {
        this.date_devuelto = date_devuelto;
    }

    public String getTimes_devuelto() {
        return times_devuelto;
    }

    public void setTimes_devuelto(String times_devuelto) {
        this.times_devuelto = times_devuelto;
    }

    @Override
    public String toString() {
        return "InventarioDevuelto{" +
                "id_devuelto=" + id_devuelto +
                ", product_id_devuelto='" + product_id_devuelto + '\'' +
                ", initial_devuelto='" + initial_devuelto + '\'' +
                ", amount_devuelto='" + amount_devuelto + '\'' +
                ", amount_dist_devuelto='" + amount_dist_devuelto + '\'' +
                ", distributor_devuelto='" + distributor_devuelto + '\'' +
                ", nombre_producto_devuelto='" + nombre_producto_devuelto + '\'' +
                ", date_devuelto='" + date_devuelto + '\'' +
                ", times_devuelto='" + times_devuelto + '\'' +
                '}';
    }
}
