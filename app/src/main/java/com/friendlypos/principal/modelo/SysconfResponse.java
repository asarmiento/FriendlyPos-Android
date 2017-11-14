package com.friendlypos.principal.modelo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DelvoM on 14/11/2017.
 */

public class SysconfResponse {

    private boolean result;
    private String code;
    @SerializedName("sysconf")
    private List<Sysconf> sysconf;

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

    public List<Sysconf> getSysconf() {
        return sysconf;
    }

    public void setSysconf(List<Sysconf> sysconf) {
        this.sysconf = sysconf;
    }
}
