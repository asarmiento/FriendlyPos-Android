package com.friendlypos.principal.modelo;

import com.friendlypos.distribucion.modelo.Pivot;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DelvoM on 21/09/2017.an
 */

public class Productos extends RealmObject {

    @PrimaryKey
    String id;

    String stock_min;
    String stock_max;
    String barcode;
    String code;
    String units_per_box;
    String description;
    String product_category_id;
    String product_type_id;
    String cost;
    String suggested;
    String bonus;
    String utility;
    String percentage_of_utility;
    String sale_price;
    double iva;
    String sale_price2;
    String sale_price3;
    String sale_price4;
    String sale_price5;
    String brand_id;
    String family;
    String sub_family;
    String type;
    String sale_method_id;
    String status;
    String updated_at;
    @SerializedName("pivot")
    private Pivot pivot;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStock_min() {
        return stock_min;
    }

    public void setStock_min(String stock_min) {
        this.stock_min = stock_min;
    }

    public String getStock_max() {
        return stock_max;
    }

    public void setStock_max(String stock_max) {
        this.stock_max = stock_max;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUnits_per_box() {
        return units_per_box;
    }

    public void setUnits_per_box(String units_per_box) {
        this.units_per_box = units_per_box;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProduct_category_id() {
        return product_category_id;
    }

    public void setProduct_category_id(String product_category_id) {
        this.product_category_id = product_category_id;
    }

    public String getProduct_type_id() {
        return product_type_id;
    }

    public void setProduct_type_id(String product_type_id) {
        this.product_type_id = product_type_id;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getSuggested() {
        return suggested;
    }

    public void setSuggested(String suggested) {
        this.suggested = suggested;
    }

    public String getBonus() {
        return bonus;
    }

    public void setBonus(String bonus) {
        this.bonus = bonus;
    }

    public String getUtility() {
        return utility;
    }

    public void setUtility(String utility) {
        this.utility = utility;
    }

    public String getPercentage_of_utility() {
        return percentage_of_utility;
    }

    public void setPercentage_of_utility(String percentage_of_utility) {
        this.percentage_of_utility = percentage_of_utility;
    }

    public String getSale_price() {
        return sale_price;
    }

    public void setSale_price(String sale_price) {
        this.sale_price = sale_price;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public String getSale_price2() {
        return sale_price2;
    }

    public void setSale_price2(String sale_price2) {
        this.sale_price2 = sale_price2;
    }

    public String getSale_price3() {
        return sale_price3;
    }

    public void setSale_price3(String sale_price3) {
        this.sale_price3 = sale_price3;
    }

    public String getSale_price4() {
        return sale_price4;
    }

    public void setSale_price4(String sale_price4) {
        this.sale_price4 = sale_price4;
    }

    public String getSale_price5() {
        return sale_price5;
    }

    public void setSale_price5(String sale_price5) {
        this.sale_price5 = sale_price5;
    }

    public String getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getSub_family() {
        return sub_family;
    }

    public void setSub_family(String sub_family) {
        this.sub_family = sub_family;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSale_method_id() {
        return sale_method_id;
    }

    public void setSale_method_id(String sale_method_id) {
        this.sale_method_id = sale_method_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public Pivot getPivot() {
        return pivot;
    }

    public void setPivot(Pivot pivot) {
        this.pivot = pivot;
    }

    @Override
    public String toString() {
        return "Productos{" +
                "id=" + id +
                ", stock_min='" + stock_min + '\'' +
                ", stock_max='" + stock_max + '\'' +
                ", barcode='" + barcode + '\'' +
                ", code='" + code + '\'' +
                ", units_per_box='" + units_per_box + '\'' +
                ", description='" + description + '\'' +
                ", product_category_id='" + product_category_id + '\'' +
                ", product_type_id='" + product_type_id + '\'' +
                ", cost='" + cost + '\'' +
                ", suggested='" + suggested + '\'' +
                ", bonus='" + bonus + '\'' +
                ", utility='" + utility + '\'' +
                ", percentage_of_utility='" + percentage_of_utility + '\'' +
                ", sale_price='" + sale_price + '\'' +
                ", iva='" + iva + '\'' +
                ", sale_price2='" + sale_price2 + '\'' +
                ", sale_price3='" + sale_price3 + '\'' +
                ", sale_price4='" + sale_price4 + '\'' +
                ", sale_price5='" + sale_price5 + '\'' +
                ", brand_id='" + brand_id + '\'' +
                ", family='" + family + '\'' +
                ", sub_family='" + sub_family + '\'' +
                ", type='" + type + '\'' +
                ", sale_method_id='" + sale_method_id + '\'' +
                ", status='" + status + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", pivot=" + pivot +
                '}';
    }
}


