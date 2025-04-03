package com.friendlypos.distribucion.modelo

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/*       "pivot": {
            "invoice_id": "41",
            "product_id": "96",
            "id": 283,
            "price": "792.0000",
            "amount": "12.00",
            "discount": "0.00",
            "delivered": "12"
            }
 */
class Pivot : RealmObject() {
    @JvmField
    @PrimaryKey
    var id: Int = 0
    @JvmField
    var invoice_id: String? = null
    @JvmField
    var product_id: String? = null
    @JvmField
    var price: String? = null
    @JvmField
    var amount: String? = null
    @JvmField
    var discount: String? = null
    @JvmField
    var delivered: String? = null
    @JvmField
    var bonus: Int = 0
    @JvmField
    var devuelvo: Int = 0
    @JvmField
    var amountSinBonus: Double = 0.0


    override fun toString(): String {
        return "Pivot{" +
                "id=" + id +
                ", invoice_id='" + invoice_id + '\'' +
                ", product_id='" + product_id + '\'' +
                ", price='" + price + '\'' +
                ", amount='" + amount + '\'' +
                ", discount='" + discount + '\'' +
                ", delivered='" + delivered + '\'' +
                ", bonus='" + bonus + '\'' +
                ", devuelvo='" + devuelvo + '\'' +
                ", amountSinBonus='" + amountSinBonus + '\'' +
                '}'
    }
}
