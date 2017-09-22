package com.friendlypos.principal.modelo;

import io.realm.RealmObject;

/**
 * Created by DelvoM on 21/09/2017.
 */

public class Productos extends RealmObject {

    private Integer id;
    private String stockMin;
    private String stockMax;
    private String barcode;
    private String code;
    private String unitsPerBox;
    private String description;
    private String productCategoryId;
    private String productTypeId;
    private String cost;
    private String utility;
    private String percentageOfUtility;
    private String salePrice;
    private String salePrice2;
    private String salePrice3;
    private String salePrice4;
    private String salePrice5;
    private String brandId;
    private String family;
    private String subFamily;
    private String type;
    private String saleMethodId;
    private String status;
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStockMin() {
        return stockMin;
    }

    public void setStockMin(String stockMin) {
        this.stockMin = stockMin;
    }

    public String getStockMax() {
        return stockMax;
    }

    public void setStockMax(String stockMax) {
        this.stockMax = stockMax;
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

    public String getUnitsPerBox() {
        return unitsPerBox;
    }

    public void setUnitsPerBox(String unitsPerBox) {
        this.unitsPerBox = unitsPerBox;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(String productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public String getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getUtility() {
        return utility;
    }

    public void setUtility(String utility) {
        this.utility = utility;
    }

    public String getPercentageOfUtility() {
        return percentageOfUtility;
    }

    public void setPercentageOfUtility(String percentageOfUtility) {
        this.percentageOfUtility = percentageOfUtility;
    }

    public String getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(String salePrice) {
        this.salePrice = salePrice;
    }

    public String getSalePrice2() {
        return salePrice2;
    }

    public void setSalePrice2(String salePrice2) {
        this.salePrice2 = salePrice2;
    }

    public String getSalePrice3() {
        return salePrice3;
    }

    public void setSalePrice3(String salePrice3) {
        this.salePrice3 = salePrice3;
    }

    public String getSalePrice4() {
        return salePrice4;
    }

    public void setSalePrice4(String salePrice4) {
        this.salePrice4 = salePrice4;
    }

    public String getSalePrice5() {
        return salePrice5;
    }

    public void setSalePrice5(String salePrice5) {
        this.salePrice5 = salePrice5;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getSubFamily() {
        return subFamily;
    }

    public void setSubFamily(String subFamily) {
        this.subFamily = subFamily;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSaleMethodId() {
        return saleMethodId;
    }

    public void setSaleMethodId(String saleMethodId) {
        this.saleMethodId = saleMethodId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}


