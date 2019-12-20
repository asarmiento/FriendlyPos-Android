package com.friendlypos.distribucion.modelo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Delvo on 04/11/2017.
 */

public class Marcas extends RealmObject {

    /*   {
           "id": 1,
               "name": "PILSEN"
         }*/

    @PrimaryKey
    String id;
     String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Marcas{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
