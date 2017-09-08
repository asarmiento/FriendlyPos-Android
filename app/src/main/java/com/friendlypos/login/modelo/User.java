package com.friendlypos.login.modelo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by jd on 9/6/17.
 */

public class User extends RealmObject {

    @PrimaryKey
    private String name;

    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
            "name='" + name + '\'' +
            ", password='" + password + '\'' +
            '}';
    }
}
