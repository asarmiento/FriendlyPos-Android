package com.friendlypos.preventas.modelo;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DelvoM on 16/04/2018.
 */

public class visit extends RealmObject {

   /* {
        "id": 1,
            "name": "Gravado",
            "created_at": "-0001-11-30 00:00:00",
            "updated_at": "-0001-11-30 00:00:00"
    }*/

    @PrimaryKey
    int id;
    String customer_id;
    String visit;
    String date;
    String observation;
    double longitud;
    double latitud;
    String user_id;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getVisit() {
        return visit;
    }

    public void setVisit(String visit) {
        this.visit = visit;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "visit{" +
                "id=" + id +
                ", customer_id='" + customer_id + '\'' +
                ", visit='" + visit + '\'' +
                ", date='" + date + '\'' +
                ", observation='" + observation + '\'' +
                ", longitud=" + longitud +
                ", latitud=" + latitud +
                ", user_id='" + user_id + '\'' +
                '}';
    }
}
