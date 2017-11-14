package com.friendlypos.distribucion.modelo;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DelvoM on 14/11/2017.
 */

public class ProductoFactura extends RealmObject {

    @PrimaryKey
    String id;
  /*  @SerializedName("pivot")
    private Pivot pivot;*/


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

/*
    public Pivot getPivot() {
        return pivot;
    }

    public void setPivot(Pivot pivot) {
        this.pivot = pivot;
    }*/

    @Override
    public String toString() {
        return "Productos{" +
                "id=" + id +

                '}';
    }
}
