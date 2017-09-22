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

    /*
    private String title;
    private String type;
    private double version;
    private List<Clientes> contents;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public List<Clientes> getContents() {
        return contents;
    }

    public void setContents(List<Clientes> contents) {
        this.contents = contents;
    }*/
}
