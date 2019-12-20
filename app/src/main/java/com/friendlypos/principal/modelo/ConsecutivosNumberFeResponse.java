package com.friendlypos.principal.modelo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DelvoM on 04/12/2018.
 */

public class ConsecutivosNumberFeResponse {

    private boolean result;
    private String code;
    @SerializedName("consecutivos_number_fe")
    private List<ConsecutivosNumberFe> consecutivosNumberFe;

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

    public List<ConsecutivosNumberFe> getConsecutivosNumberFe() {
        return consecutivosNumberFe;
    }

    public void setConsecutivosNumberFe(List<ConsecutivosNumberFe> consecutivosNumberFe) {
        this.consecutivosNumberFe = consecutivosNumberFe;
    }
}
