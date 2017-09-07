package com.friendlypos.login.interfaces;

import com.friendlypos.login.modelo.AppResponse;

public interface LoginListener {
    void onLoginSuccess(AppResponse appResponse, String user, String pass);

    void onLoginError(String errorMessage);

    void onRegisterSuccess(AppResponse appResponse);

    void onRegisterError(String errorMessage);

    void doLogin(String user, String password, ServiceCallback callback);

    void doRegister(String email, String password, ServiceCallback callback);
}
