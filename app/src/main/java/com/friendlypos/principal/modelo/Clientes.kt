package com.friendlysystemgroup.friendlypos.principal.modelo

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Clientes : RealmObject() {
    @JvmField
    @PrimaryKey
    var id: String? = null

    @JvmField
    var card: String? = null
    @JvmField
    var fe: String? = null
    var placa: String? = null
    var doors: String? = null
    @JvmField
    var name: String? = null
    var longitud: Double = 0.0
    var latitud: Double = 0.0
    var fantasyName: String? = null
    var companyName: String? = null
    @JvmField
    var phone: String? = null
    var creditLimit: String? = null
    @JvmField
    var due: String? = null
    @JvmField
    var address: String? = null
    var zoneId: String? = null
    var fixedDiscount: String? = null
    var creditTime: String? = null
    var updatedAt: String? = null


    override fun toString(): String {
        return "Clientes{" +
                "id='" + id + '\'' +
                ", card='" + card + '\'' +
                ", fe='" + fe + '\'' +
                ", placa='" + placa + '\'' +
                ", doors='" + doors + '\'' +
                ", name='" + name + '\'' +
                ", longitud='" + longitud + '\'' +
                ", latitud='" + latitud + '\'' +
                ", fantasy_name='" + fantasyName + '\'' +
                ", company_name='" + companyName + '\'' +
                ", phone='" + phone + '\'' +
                ", credit_limit='" + creditLimit + '\'' +
                ", due='" + due + '\'' +
                ", address='" + address + '\'' +
                ", zoneId='" + zoneId + '\'' +
                ", fixed_discount='" + fixedDiscount + '\'' +
                ", credit_time='" + creditTime + '\'' +
                ", updated_at='" + updatedAt + '\'' +
                '}'
    } /* private String id;
    private String title;
    private String image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }*/
}
