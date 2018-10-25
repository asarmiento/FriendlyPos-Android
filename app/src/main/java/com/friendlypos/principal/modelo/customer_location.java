package com.friendlypos.principal.modelo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DelvoM on 24/10/2018.
 */

public class customer_location extends RealmObject {


    double longitud;
    double latitud;
    String id;

    int subidaEdit = 0;

    private boolean result;
    private String code;
    private String messages;


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

    public String getId() {
        return id;
    }

    public void setId(String id_cliente) {
        this.id = id_cliente;
    }

    public int getSubidaEdit() {
        return subidaEdit;
    }

    public void setSubidaEdit(int subidaEdit) {
        this.subidaEdit = subidaEdit;
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

    public void setMessage(String messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "customer_location{" +
                "longitud=" + longitud +
                ", latitud=" + latitud +
                ", id='" + id + '\'' +
                ", subidaEdit=" + subidaEdit +
                '}';
    }
}
