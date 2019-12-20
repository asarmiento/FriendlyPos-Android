package com.friendlypos.preventas.modelo;

import com.friendlypos.distribucion.modelo.Marcas;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DelvoM on 24/04/2018.
 */

public class NumeracionResponse {

    private boolean result;
    private String code;
    @SerializedName("numeration")
    private List<Numeracion> numeracion;

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

    public List<Numeracion> getNumeracion() {
        return numeracion;
    }

    public void setNumeracion(List<Numeracion> numeracion) {
        this.numeracion = numeracion;
    }


}
