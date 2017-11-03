package com.friendlypos.principal.modelo;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class ProductosResponse {
    private boolean result;
    private String code;

    @SerializedName("products")
    private List<Productos> products;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Productos> getProductos() {
        return products;
    }

    public void setProductos(List<Productos> productos) {
        this.products = productos;
    }

}
