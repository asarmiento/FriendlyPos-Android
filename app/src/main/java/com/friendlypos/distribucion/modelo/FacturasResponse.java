package com.friendlypos.distribucion.modelo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DelvoM on 02/11/2017.
 */

public class FacturasResponse {

    private boolean result;
    private String code;
    private String message;
    @SerializedName("invoices")
    private List<Facturas> facturas;

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

    public List<Facturas> getFacturas() {
        return facturas;
    }

    public void setFacturas(List<Facturas> facturas) {
        this.facturas = facturas;
    }
}
