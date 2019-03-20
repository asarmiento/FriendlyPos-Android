package com.friendlypos.reenvio_email.modelo;

import io.realm.annotations.PrimaryKey;

public class customer {

    /*  "id": 8,
              "card": "",
              "company_name": "ALFREDO QUINTERO QUINTERO"*/

    @PrimaryKey
    String id;
    String card;
    String company_name;

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

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    @Override
    public String toString() {
        return "customer{" +
                "id='" + id + '\'' +
                ", card='" + card + '\'' +
                ", company_name='" + company_name + '\'' +
                '}';
    }
}
