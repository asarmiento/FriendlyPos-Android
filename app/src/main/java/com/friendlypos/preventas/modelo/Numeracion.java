package com.friendlypos.preventas.modelo;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DelvoM on 24/04/2018.
 */

public class Numeracion extends RealmObject {

    @PrimaryKey
    String sale_type;
    int numeracion_numero;

    public String getSale_type() {
        return sale_type;
    }

    public void setSale_type(String id) {
        this.sale_type = id;
    }

    public int getNumeracion_numero() {
        return numeracion_numero;
    }

    public void setNumeracion_numero(int numeracion_numero) {
        this.numeracion_numero = numeracion_numero;
    }

    @Override
    public String toString() {
        return "Numeracion{" +
                "sale_type='" + sale_type + '\'' +
                ", numeracion_numero=" + numeracion_numero +
                '}';
    }
}
