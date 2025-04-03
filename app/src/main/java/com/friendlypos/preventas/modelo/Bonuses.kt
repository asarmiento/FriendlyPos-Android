package com.friendlypos.preventas.modelo

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.Date

/**
 * Created by DelvoM on 26/04/2018.
 */
class Bonuses : RealmObject() {
    @PrimaryKey
    var id: Int = 0
    var reference: String? = null
    @JvmField
    var product_id: Int = 0
    @JvmField
    var product_sale: String? = null
    var bonus_product_id: Int = 0
    @JvmField
    var product_bonus: String? = null
    @JvmField
    var expiration: Date? = null
    var created_at: String? = null
    var updated_at: String? = null

    override fun toString(): String {
        return "Bonuses{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", product_id=" + product_id +
                ", product_sale='" + product_sale + '\'' +
                ", bonus_product_id=" + bonus_product_id +
                ", product_bonus='" + product_bonus + '\'' +
                ", expiration='" + expiration + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}'
    }
}
