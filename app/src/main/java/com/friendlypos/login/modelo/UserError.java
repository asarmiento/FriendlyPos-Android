package com.friendlypos.login.modelo;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * Created by DelvoM on 25/09/2017.
 */

public class UserError {

    private String error;
    private String message;

    public UserError(String error, String message) {
        this.error = error;
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static UserError fromResponseBody(ResponseBody responseBody) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(responseBody.string(), UserError.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
