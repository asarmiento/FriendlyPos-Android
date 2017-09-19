package com.friendlypos.principal.modelo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DelvoM on 19/09/2017.
 */

public class Clientes {

    @SerializedName("customers")
    public List<Client> datosClientes = new ArrayList<>();

    public class Client {
        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("card")
        @Expose
        public String card;
        @SerializedName("placa")
        @Expose
        public String placa;
        @SerializedName("doors")
        @Expose
        public String doors;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("fantasy_name")
        @Expose
        public String fantasyName;
        @SerializedName("company_name")
        @Expose
        public String companyName;
        @SerializedName("phone")
        @Expose
        public String phone;
        @SerializedName("credit_limit")
        @Expose
        public String creditLimit;
        @SerializedName("due")
        @Expose
        public String due;
        @SerializedName("address")
        @Expose
        public String address;
        @SerializedName("zone_id")
        @Expose
        public String zoneId;
        @SerializedName("fixed_discount")
        @Expose
        public String fixedDiscount;
        @SerializedName("credit_time")
        @Expose
        public String creditTime;
        @SerializedName("updated_at")
        @Expose
        public String updatedAt;


        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
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
            return fantasyName;
        }

        public void setFantasyName(String fantasyName) {
            this.fantasyName = fantasyName;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getCreditLimit() {
            return creditLimit;
        }

        public void setCreditLimit(String creditLimit) {
            this.creditLimit = creditLimit;
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
            return fixedDiscount;
        }

        public void setFixedDiscount(String fixedDiscount) {
            this.fixedDiscount = fixedDiscount;
        }

        public String getCreditTime() {
            return creditTime;
        }

        public void setCreditTime(String creditTime) {
            this.creditTime = creditTime;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}


