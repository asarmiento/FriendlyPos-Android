package com.friendlypos.distribucion.modelo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DelvoM on 06/11/2017.
 */

public class VentaResponse {

    private boolean result;
    private String code;
    private String message;
    @SerializedName("sale")
    private List<sale> sale;

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

  public List<sale> getSale() {
        return sale;
    }

    public void setSale(List<sale> sale) {
        this.sale = sale;
    }
}
