package com.friendlypos.distribucion.modelo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DelvoM on 30/11/2017.
 */

public class MetodoPagoResponse {

    private boolean result;
    private String code;
    private String message;
    @SerializedName("payment_methods")
    private List<MetodoPago> metodoPago;

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

    public List<MetodoPago> getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(List<MetodoPago> metodoPago) {
        this.metodoPago = metodoPago;
    }


}
