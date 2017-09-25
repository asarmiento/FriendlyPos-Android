package com.friendlypos.principal.modelo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Podisto on 15/05/2016.
 */
public class ClientesResponse {

    private boolean result;
    private String code;
    @SerializedName("customers")
    private List<Clientes> customers;

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

    public List<Clientes> getContents() {
        return customers;
    }

    public void setContents(List<Clientes> contents) {
        this.customers = contents;
    }

}
