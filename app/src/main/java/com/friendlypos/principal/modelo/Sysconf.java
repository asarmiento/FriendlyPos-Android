package com.friendlypos.principal.modelo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DelvoM on 14/11/2017.
 */

public class Sysconf extends RealmObject {

    /*  "id": 3,
            "identification": "3-101-692713",
            "name": "DISTRIBUIDORA A&V",
            "business_name": "",
            "logo": "/tmp/php4PQNQ9",
            "direction": "SAN JOAQUIN DE FLORES, HEREDIA",
            "phone": "85988444/72688823",
            "email": "ayvdistribuidoracr@gmail.com",
            "fax": "000000",
            "official_currency": "₡",
            "tax_rate": "13",
            "cost_of_return": "max",
            "initial_month": "10",
            "final_month": "09",
            "created_at": "2017-08-09 20:38:47",
            "updated_at": "2016-12-18 00:24:43",
            "type": "distribuidor"*/

    @PrimaryKey
    private String id;

    private String identification;
    private String fe;
    private String data_base;
    private String type_of_cedula;
    private String id_number_atv;
    private String name;
    private String short_name;
    private String code;
    private String ftp;
    private String business_name;
    private String logo;
    private String subdomin;
    private String sucursal;
    private String direction;
    private String phone;
    private String email;
    private String fax;
    private String official_currency;
    private String tax_rate;
    private String cost_of_return;
    private String initial_month;
    private String final_month;
    private String created_at;
    private String updated_at;
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getOfficial_currency() {
        return official_currency;
    }

    public void setOfficial_currency(String official_currency) {
        this.official_currency = official_currency;
    }

    public String getTax_rate() {
        return tax_rate;
    }

    public void setTax_rate(String tax_rate) {
        this.tax_rate = tax_rate;
    }

    public String getCost_of_return() {
        return cost_of_return;
    }

    public void setCost_of_return(String cost_of_return) {
        this.cost_of_return = cost_of_return;
    }

    public String getInitial_month() {
        return initial_month;
    }

    public void setInitial_month(String initial_month) {
        this.initial_month = initial_month;
    }

    public String getFinal_month() {
        return final_month;
    }

    public void setFinal_month(String final_month) {
        this.final_month = final_month;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFe() {
        return fe;
    }

    public void setFe(String fe) {
        this.fe = fe;
    }

    public String getData_base() {
        return data_base;
    }

    public void setData_base(String data_base) {
        this.data_base = data_base;
    }

    public String getType_of_cedula() {
        return type_of_cedula;
    }

    public void setType_of_cedula(String type_of_cedula) {
        this.type_of_cedula = type_of_cedula;
    }

    public String getId_number_atv() {
        return id_number_atv;
    }

    public void setId_number_atv(String id_number_atv) {
        this.id_number_atv = id_number_atv;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFtp() {
        return ftp;
    }

    public void setFtp(String ftp) {
        this.ftp = ftp;
    }

    public String getSubdomin() {
        return subdomin;
    }

    public void setSubdomin(String subdomin) {
        this.subdomin = subdomin;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    @Override
    public String toString() {
        return "Sysconf{" +
                "id='" + id + '\'' +
                ", identification='" + identification + '\'' +
                ", fe='" + fe + '\'' +
                ", data_base='" + data_base + '\'' +
                ", type_of_cedula='" + type_of_cedula + '\'' +
                ", id_number_atv='" + id_number_atv + '\'' +
                ", name='" + name + '\'' +
                ", short_name='" + short_name + '\'' +
                ", code='" + code + '\'' +
                ", ftp='" + ftp + '\'' +
                ", business_name='" + business_name + '\'' +
                ", logo='" + logo + '\'' +
                ", subdomin='" + subdomin + '\'' +
                ", sucursal='" + sucursal + '\'' +
                ", direction='" + direction + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", fax='" + fax + '\'' +
                ", official_currency='" + official_currency + '\'' +
                ", tax_rate='" + tax_rate + '\'' +
                ", cost_of_return='" + cost_of_return + '\'' +
                ", initial_month='" + initial_month + '\'' +
                ", final_month='" + final_month + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
