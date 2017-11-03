package com.friendlypos.distribucion.modelo;

import io.realm.RealmObject;

/**
 * Created by DelvoM on 31/10/2017.
 */

public class Inventario extends RealmObject {

    private String id;
    private String product_id;
    private String initial;
    private String amount;
    private String amount_dist;
    private String distributor;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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


    @Override
    public String toString() {
        return "Inventario{" +
            "id='" + id + '\'' +
            ", product_id='" + product_id + '\'' +
            ", initial='" + initial + '\'' +
            ", amount='" + amount + '\'' +
            ", amount_dist='" + amount_dist + '\'' +
            ", distributor='" + distributor + '\'' +
            '}';
    }
}
