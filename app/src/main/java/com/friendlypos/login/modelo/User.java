package com.friendlypos.login.modelo;

/**
 * Created by DelvoM on 25/09/2017.
 */
/*
        "id": 1,
        "employee_id": "3",
        "username": "admin",
        "status": "1",
        "tmp_password": "$2y$10$H/6H7.pTeVnEo0K66j7WZOHM5uHEia0e.QLD6AkImtpyTuYtCgHGS",
        "code": "rhuPLvQBXZ4MNAtJTYa2g5y5K28lcJ7Y5Na6WWIA3aZ0LouGhw8ooqqrqtPO",
        "email": "anwarsarmiento@gmail.com"*/

public class User{

    private String id;
    private String employee_id;
    private String username;
    private String password;
    private String tmp_password;
    private String code;
    private String email;


    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(String employee_id) {
        this.employee_id = employee_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTmp_password() {
        return tmp_password;
    }

    public void setTmp_password(String tmp_password) {
        this.tmp_password = tmp_password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }{
}
}