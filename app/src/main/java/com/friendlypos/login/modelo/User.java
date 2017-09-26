package com.friendlypos.login.modelo;

/**
 * Created by DelvoM on 25/09/2017.
 */

public class User{

    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }


    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }{
}
}