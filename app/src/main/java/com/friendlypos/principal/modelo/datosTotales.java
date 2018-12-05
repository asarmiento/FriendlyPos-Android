package com.friendlypos.principal.modelo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DelvoM on 15/11/2018.
 */

public class datosTotales extends RealmObject {

    @PrimaryKey
    int id;
    int idTotal;
    String nombreTotal;
    double totalDistribucion;
    double totalVentaDirecta;
    double totalPreventa;
    double totalProforma;
    double totalRecibos;
    String date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdTotal() {
        return idTotal;
    }

    public void setIdTotal(int idTotal) {
        this.idTotal = idTotal;
    }

    public String getNombreTotal() {
        return nombreTotal;
    }

    public void setNombreTotal(String nombreTotal) {
        this.nombreTotal = nombreTotal;
    }

    public double getTotalDistribucion() {
        return totalDistribucion;
    }

    public void setTotalDistribucion(double totalDistribucion) {
        this.totalDistribucion = totalDistribucion;
    }

    public double getTotalVentaDirecta() {
        return totalVentaDirecta;
    }

    public void setTotalVentaDirecta(double totalVentaDirecta) {
        this.totalVentaDirecta = totalVentaDirecta;
    }

    public double getTotalPreventa() {
        return totalPreventa;
    }

    public void setTotalPreventa(double totalPreventa) {
        this.totalPreventa = totalPreventa;
    }

    public double getTotalProforma() {
        return totalProforma;
    }

    public void setTotalProforma(double totalProforma) {
        this.totalProforma = totalProforma;
    }

    public double getTotalRecibos() {
        return totalRecibos;
    }

    public void setTotalRecibos(double totalRecibos) {
        this.totalRecibos = totalRecibos;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "datosTotales{" +
                "id=" + id +
                ", idTotal=" + idTotal +
                ", nombreTotal='" + nombreTotal + '\'' +
                ", totalDistribucion=" + totalDistribucion +
                ", totalVentaDirecta=" + totalVentaDirecta +
                ", totalPreventa=" + totalPreventa +
                ", totalProforma=" + totalProforma +
                ", totalRecibos=" + totalRecibos +
                ", date=" + date +
                '}';
    }
}
