package com.friendlypos.distribucion.modelo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Delvo on 04/11/2017.
 */

public class MarcasResponse {

    private boolean result;
    private String code;
    @SerializedName("brands")
    private List<Marcas> marca;

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

    public List<Marcas> getMarca() {
        return marca;
    }

    public void setMarca(List<Marcas> marca) {
        this.marca = marca;
    }


}
