package com.friendlypos.preventas.modelo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DelvoM on 26/04/2018.
 */

public class BonusesResponse {

    private boolean result;
    private String code;
    @SerializedName("bonuses")
    private List<Bonuses> bonuses;

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

    public List<Bonuses> getBonuses() {
        return bonuses;
    }

    public void setBonuses(List<Bonuses> bonuses) {
        this.bonuses = bonuses;
    }
}
