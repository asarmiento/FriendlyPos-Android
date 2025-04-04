package com.friendlysystemgroup.friendlypos.login.modelo

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
open class User(var username: String, var password: String) {
    var id: String? = null
    var employee_id: String? = null
    var tmp_password: String? = null
    var code: String? = null
    var email: String? = null


    @Override
    override fun toString(): String {
        return "UserEntrar{" +
                "id='" + id + '\'' +
                ", employee_id='" + employee_id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", tmp_password='" + tmp_password + '\'' +
                ", code='" + code + '\'' +
                ", email='" + email + '\'' +
                '}'
    }
}