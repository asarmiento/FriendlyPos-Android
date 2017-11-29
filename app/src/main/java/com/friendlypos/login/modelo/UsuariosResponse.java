package com.friendlypos.login.modelo;

import com.friendlypos.principal.modelo.Productos;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DelvoM on 29/11/2017.
 */

public class UsuariosResponse {

    private boolean result;
    private String code;

    @SerializedName("users")
    private List<Usuarios> usuarios;

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

    public List<Usuarios> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuarios> usuarios) {
        this.usuarios = usuarios;
    }
}
