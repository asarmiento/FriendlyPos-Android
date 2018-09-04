package com.friendlypos.distribucion.modelo;

import com.friendlypos.principal.modelo.Productos;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DelvoM on 31/10/2017.
 */

public class Inventario extends RealmObject {

    @PrimaryKey
    int id;
    String product_id;
    String initial;
    String amount;
    String amount_dist;
    String distributor;

    String nombre_producto;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmount_dist() {
        return amount_dist;
    }

    public void setAmount_dist(String amount_dist) {
        this.amount_dist = amount_dist;
    }

    public String getDistributor() {
        return distributor;
    }

    public void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    public String getNombre_producto() {
        return nombre_producto;
    }

    public void setNombre_producto(String nombre_producto) {
        this.nombre_producto = nombre_producto;
    }

    @Override
    public String toString() {
        return "Inventario{" +
            "id='" + id + '\'' +
            ", product_id='" + product_id + '\'' +
            ", initial='" + initial + '\'' +
            ", amount='" + amount + '\'' +
            ", amount_dist='" + amount_dist + '\'' +
            ", distributor='" + distributor + '\'' +
            ", nombre_producto='" + nombre_producto + '\'' +
            '}';
    }
}
