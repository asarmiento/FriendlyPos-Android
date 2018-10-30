package com.friendlypos.crearCliente.modelo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DelvoM on 30/10/2018.
 */

public class ClienteNuevo extends RealmObject {

    @PrimaryKey
    private String id;

    private String idtype;
    private String card;
    private String fe;
    private double longitud;
    private double latitud;
    private String placa;
    private String model;
    private String doors;
    private String name;
    private String email;
    private String fantasy_name;
    private String company_name;
    private String phone;
    private String credit_limit;
    private String address;
    private String credit_time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdtype() {
        return idtype;
    }

    public void setIdtype(String idtype) {
        this.idtype = idtype;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getFe() {
        return fe;
    }

    public void setFe(String fe) {
        this.fe = fe;
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

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDoors() {
        return doors;
    }

    public void setDoors(String doors) {
        this.doors = doors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFantasy_name() {
        return fantasy_name;
    }

    public void setFantasy_name(String fantasy_name) {
        this.fantasy_name = fantasy_name;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCredit_limit() {
        return credit_limit;
    }

    public void setCredit_limit(String credit_limit) {
        this.credit_limit = credit_limit;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCredit_time() {
        return credit_time;
    }

    public void setCredit_time(String credit_time) {
        this.credit_time = credit_time;
    }

    @Override
    public String toString() {
        return "ClienteNuevo{" +
                "id='" + id + '\'' +
                ", idtype='" + idtype + '\'' +
                ", card='" + card + '\'' +
                ", fe='" + fe + '\'' +
                ", longitud=" + longitud +
                ", latitud=" + latitud +
                ", placa='" + placa + '\'' +
                ", model='" + model + '\'' +
                ", doors='" + doors + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", fantasy_name='" + fantasy_name + '\'' +
                ", company_name='" + company_name + '\'' +
                ", phone='" + phone + '\'' +
                ", credit_limit='" + credit_limit + '\'' +
                ", address='" + address + '\'' +
                ", credit_time='" + credit_time + '\'' +
                '}';
    }
}
