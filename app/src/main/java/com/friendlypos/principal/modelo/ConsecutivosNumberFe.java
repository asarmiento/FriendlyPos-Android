package com.friendlypos.principal.modelo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DelvoM on 04/12/2018.
 */

public class ConsecutivosNumberFe extends RealmObject {

    /*
        "id": 1,
                "number_consecutive": 1077,
                "number_clave": 1077,
                "type_doc": "1",
                "user_id": 1,
                "api": 0,
                "created_at": null,
                "updated_at": "2018-11-29 11:05:08"*/

    @PrimaryKey
    private String id;

    private int number_consecutive;
    private String number_clave;
    private String type_doc;
    private String user_id;
    private String api;
    private String created_at;
    private String updated_at;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNumber_consecutive() {
        return number_consecutive;
    }

    public void setNumber_consecutive(int number_consecutive) {
        this.number_consecutive = number_consecutive;
    }

    public String getNumber_clave() {
        return number_clave;
    }

    public void setNumber_clave(String number_clave) {
        this.number_clave = number_clave;
    }

    public String getType_doc() {
        return type_doc;
    }

    public void setType_doc(String type_doc) {
        this.type_doc = type_doc;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public String toString() {
        return "ConsecutivosNumberFe{" +
                "id='" + id + '\'' +
                ", number_consecutive='" + number_consecutive + '\'' +
                ", number_clave='" + number_clave + '\'' +
                ", type_doc='" + type_doc + '\'' +
                ", user_id='" + user_id + '\'' +
                ", api='" + api + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}';
    }
}
