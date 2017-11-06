package com.friendlypos.distribucion.modelo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Delvo on 04/11/2017.
 */

public class TipoProductoResponse {

    private boolean result;
    private String code;
    @SerializedName("product_types")
    private List<TipoProducto> tipoProducto;

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

    public List<TipoProducto> getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(List<TipoProducto> tipoProducto) {
        this.tipoProducto = tipoProducto;
    }
}
