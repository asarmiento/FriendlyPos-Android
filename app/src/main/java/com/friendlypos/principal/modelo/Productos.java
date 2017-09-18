package com.friendlypos.principal.modelo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DelvoM on 18/09/2017.
 */

public class Productos {

    @SerializedName("products")
    public List<Product> datosProductos = new ArrayList<>();

   /* @SerializedName("products")
    @Expose
    private List<Product> products = null;

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }*/


    public class Product {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("stock_min")
        @Expose
        private String stockMin;
        @SerializedName("stock_max")
        @Expose
        private String stockMax;
        @SerializedName("barcode")
        @Expose
        private String barcode;
        @SerializedName("code")
        @Expose
        private String code;
        @SerializedName("units_per_box")
        @Expose
        private String unitsPerBox;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("product_category_id")
        @Expose
        private String productCategoryId;
        @SerializedName("product_type_id")
        @Expose
        private String productTypeId;
        @SerializedName("cost")
        @Expose
        private String cost;
        @SerializedName("utility")
        @Expose
        private String utility;
        @SerializedName("percentage_of_utility")
        @Expose
        private String percentageOfUtility;
        @SerializedName("sale_price")
        @Expose
        private String salePrice;
        @SerializedName("sale_price2")
        @Expose
        private String salePrice2;
        @SerializedName("sale_price3")
        @Expose
        private String salePrice3;
        @SerializedName("sale_price4")
        @Expose
        private String salePrice4;
        @SerializedName("sale_price5")
        @Expose
        private String salePrice5;
        @SerializedName("brand_id")
        @Expose
        private String brandId;
        @SerializedName("family")
        @Expose
        private String family;
        @SerializedName("sub_family")
        @Expose
        private String subFamily;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("sale_method_id")
        @Expose
        private String saleMethodId;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("updated_at")
        @Expose
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
}
