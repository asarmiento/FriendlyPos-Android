package com.friendlypos.distribucion.modelo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmList;

/**
 * Created by DelvoM on 02/11/2017.
 */

public class ProductoFacturaResponse {

    private boolean result;
    private String code;
    private String message;
    @SerializedName("products")
    private List<ProductoFactura> facturas;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ProductoFactura> getProductofactura() {
        return facturas;
    }

    public void setProductofactura(List<ProductoFactura> facturas) {
        this.facturas = facturas;
    }
}
