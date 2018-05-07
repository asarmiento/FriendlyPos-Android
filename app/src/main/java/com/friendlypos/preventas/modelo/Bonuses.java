package com.friendlypos.preventas.modelo;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DelvoM on 26/04/2018.
 */

public class Bonuses extends RealmObject {

    /*   {
        "id": 1,
            "reference": "18-000001",
            "product_id": 2,
            "product_sale": "12.00",
            "bonus_product_id": 2,
            "product_bonus": "2.00",
            "expiration": "2018-04-30",
            "created_at": "2018-04-26 11:29:12",
            "updated_at": "2018-04-26 11:29:12"
    },*/

    @PrimaryKey
    int id;
    String reference;
    int product_id;
    String product_sale;
    int bonus_product_id;
    String product_bonus;
    int expiration;
    String created_at;
    String updated_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getProduct_sale() {
        return product_sale;
    }

    public void setProduct_sale(String product_sale) {
        this.product_sale = product_sale;
    }

    public int getBonus_product_id() {
        return bonus_product_id;
    }

    public void setBonus_product_id(int bonus_product_id) {
        this.bonus_product_id = bonus_product_id;
    }

    public String getProduct_bonus() {
        return product_bonus;
    }

    public void setProduct_bonus(String product_bonus) {
        this.product_bonus = product_bonus;
    }

    public int getExpiration() {
        return expiration;
    }

    public void setExpiration(int expiration) {
        this.expiration = expiration;
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

    @Override
    public String toString() {
        return "Bonuses{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", product_id=" + product_id +
                ", product_sale='" + product_sale + '\'' +
                ", bonus_product_id=" + bonus_product_id +
                ", product_bonus='" + product_bonus + '\'' +
                ", expiration='" + expiration + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}';
    }
}
