package com.friendlypos.login.modelo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DelvoM on 29/11/2017.
 */

public class Usuarios extends RealmObject {
    @PrimaryKey
    private String id;

    private String employee_id;
    private String username;
    private String password;
    private String tmp_password;
    private String code;
    private String email;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    @Override
    public String toString() {
        return "Usuarios{" +
                "id='" + id + '\'' +
                ", employee_id='" + employee_id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", tmp_password='" + tmp_password + '\'' +
                ", code='" + code + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
