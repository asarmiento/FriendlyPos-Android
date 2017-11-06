package com.friendlypos.principal.modelo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Clientes extends RealmObject {
    @PrimaryKey
    private String id;

    private String card;
    private String placa;
    private String doors;
    private String name;
    private String fantasy_name;
    private String company_name;
    private String phone;
    private String credit_limit;
    private String due;
    private String address;
    private String zoneId;
    private String fixed_discount;
    private String credit_time;
    private String updated_at;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
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

    public String getFantasyName() {
        return fantasy_name;
    }

    public void setFantasyName(String fantasyName) {
        this.fantasy_name = fantasyName;
    }

    public String getCompanyName() {
        return company_name;
    }

    public void setCompanyName(String companyName) {
        this.company_name = companyName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCreditLimit() {
        return credit_limit;
    }

    public void setCreditLimit(String creditLimit) {
        this.credit_limit = creditLimit;
    }

    public String getDue() {
        return due;
    }

    public void setDue(String due) {
        this.due = due;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getFixedDiscount() {
        return fixed_discount;
    }

    public void setFixedDiscount(String fixedDiscount) {
        this.fixed_discount = fixedDiscount;
    }

    public String getCreditTime() {
        return credit_time;
    }

    public void setCreditTime(String creditTime) {
        this.credit_time = creditTime;
    }

    public String getUpdatedAt() {
        return updated_at;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updated_at = updatedAt;
    }

    @Override
    public String toString() {
        return "Clientes{" +
                "id='" + id + '\'' +
                ", card='" + card + '\'' +
                ", placa='" + placa + '\'' +
                ", doors='" + doors + '\'' +
                ", name='" + name + '\'' +
                ", fantasy_name='" + fantasy_name + '\'' +
                ", company_name='" + company_name + '\'' +
                ", phone='" + phone + '\'' +
                ", credit_limit='" + credit_limit + '\'' +
                ", due='" + due + '\'' +
                ", address='" + address + '\'' +
                ", zoneId='" + zoneId + '\'' +
                ", fixed_discount='" + fixed_discount + '\'' +
                ", credit_time='" + credit_time + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}';
    }

    /* private String id;
    private String title;
    private String image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }*/
}
