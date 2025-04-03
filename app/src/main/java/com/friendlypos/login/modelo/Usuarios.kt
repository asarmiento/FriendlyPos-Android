package com.friendlypos.login.modelo

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by DelvoM on 29/11/2017.
 */
class Usuarios : RealmObject() {
    @JvmField
    @PrimaryKey
    var id: String? = null

    var employee_id: String? = null
    @JvmField
    var username: String? = null
    var status: String? = null
    @JvmField
    var terminal: String? = null
    var password: String? = null
    var tmp_password: String? = null
    var code: String? = null
    var email: String? = null

    override fun toString(): String {
        return "Usuarios{" +
                "id='" + id + '\'' +
                ", employee_id='" + employee_id + '\'' +
                ", username='" + username + '\'' +
                ", status='" + status + '\'' +
                ", terminal='" + terminal + '\'' +
                ", password='" + password + '\'' +
                ", tmp_password='" + tmp_password + '\'' +
                ", code='" + code + '\'' +
                ", email='" + email + '\'' +
                '}'
    }
}
