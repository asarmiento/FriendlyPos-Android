package com.friendlypos.principal.modelo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DelvoM on 15/11/2018.
 */

public class datosTotales extends RealmObject {

    @PrimaryKey
    int id;
    String nombreTotal;
    double totalDistribucion;
    double totalVentaDirecta;
    double totalPreventa;
    double totalProforma;
    double totalRecibos;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "datosTotales{" +
                "id=" + id +
                ", nombreTotal='" + nombreTotal + '\'' +
                ", totalDistribucion=" + totalDistribucion +
                ", totalVentaDirecta=" + totalVentaDirecta +
                ", totalPreventa=" + totalPreventa +
                ", totalProforma=" + totalProforma +
                ", totalRecibos=" + totalRecibos +
                '}';
    }
}
