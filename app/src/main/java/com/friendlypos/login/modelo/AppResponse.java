package com.friendlypos.login.modelo;

import com.google.gson.annotations.SerializedName;


public class AppResponse {

    @SerializedName("message")
    String mMessage;

    @SerializedName("error")
    String mError;

    public String getError() {
        return mError;
    }

    public void setError(String mError) {
        this.mError = mError;
    }

    @Override
    public String toString() {
        return "AppResponse{" +
                "Message='" + mMessage + '\'' +
                ", Error='" + mError + '\'' +
                '}';
    }

    public AppResponse(String message, String error) {
        this.mMessage = message;
        this.mError = error;
    }
}