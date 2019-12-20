package com.friendlypos.Recibos.modelo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DelvoM on 03/09/2018.
 */

public class RecibosResponse {

    private boolean result;
    private String code;
    private String message;
    @SerializedName("invoices")
    private List<com.friendlypos.Recibos.modelo.recibos> recibos;

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

    public List<com.friendlypos.Recibos.modelo.recibos> getRecibos() {
        return recibos;
    }

    public void setRecibos(List<com.friendlypos.Recibos.modelo.recibos> recibo) {
        this.recibos = recibo;
    }
}

