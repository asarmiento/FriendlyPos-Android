package com.friendlypos.distribucion.modelo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DelvoM on 06/11/2017.
 */

public class PivotResponse {

    private boolean result;
    private String code;
    private String message;
    @SerializedName("pivot")
    private List<Pivot> pivot;

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

    public List<Pivot> getPivot() {
        return pivot;
    }

    public void setPivot(List<Pivot> pivot) {
        this.pivot = pivot;
    }
}
