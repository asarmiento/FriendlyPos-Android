package com.friendlypos.preventas.modelo;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DelvoM on 16/04/2018.
 */

public class ClienteVisitado extends RealmObject {

   /* {
        "id": 1,
            "name": "Gravado",
            "created_at": "-0001-11-30 00:00:00",
            "updated_at": "-0001-11-30 00:00:00"
    }*/

    @PrimaryKey
    int id;
    String id_invoice;
    String pedido;
    double longitud;
    double latitud;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getId_invoice() {
        return id_invoice;
    }

    public void setId_invoice(String id_invoice) {
        this.id_invoice = id_invoice;
    }

    public String getPedido() {
        return pedido;
    }

    public void setPedido(String pedido) {
        this.pedido = pedido;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    @Override
    public String toString() {
        return "ClienteVisitado{" +
                "id='" + id + '\'' +
                ", id_invoice='" + id_invoice + '\'' +
                ", pedido='" + pedido + '\'' +
                ", longitud=" + longitud +
                ", latitud=" + latitud +
                '}';
    }
}
