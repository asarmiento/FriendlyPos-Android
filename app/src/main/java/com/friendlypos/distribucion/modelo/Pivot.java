package com.friendlypos.distribucion.modelo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

 /*       "pivot": {
            "invoice_id": "41",
            "product_id": "96",
            "id": 283,
            "price": "792.0000",
            "amount": "12.00",
            "discount": "0.00",
            "delivered": "12"
            }
 */

public class Pivot extends RealmObject {

    @PrimaryKey
    int id;
    String invoice_id;
    String product_id;
    String price;
    String amount;
    String discount;
    String delivered;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInvoice_id() {
        return invoice_id;
    }

    public void setInvoice_id(String invoice_id) {
        this.invoice_id = invoice_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDelivered() {
        return delivered;
    }

    public void setDelivered(String delivered) {
        this.delivered = delivered;
    }
}
