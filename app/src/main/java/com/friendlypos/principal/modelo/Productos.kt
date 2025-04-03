package com.friendlypos.principal.modelo

import com.friendlypos.distribucion.modelo.Pivot
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by DelvoM on 21/09/2017.an
 */
class Productos : RealmObject() {
    @JvmField
    @PrimaryKey
    var id: String? = null

    var stock_min: String? = null
    var stock_max: String? = null
    @JvmField
    var barcode: String? = null
    var code: String? = null
    var units_per_box: String? = null
    @JvmField
    var description: String? = null
    var product_category_id: String? = null
    @JvmField
    var product_type_id: String? = null
    var cost: String? = null
    @JvmField
    var suggested: String? = null
    @JvmField
    var bonus: String? = null
    var utility: String? = null
    var percentage_of_utility: String? = null
    @JvmField
    var sale_price: String? = null
    @JvmField
    var iva: Double = 0.0
    @JvmField
    var sale_price2: String? = null
    @JvmField
    var sale_price3: String? = null
    @JvmField
    var sale_price4: String? = null
    @JvmField
    var sale_price5: String? = null
    @JvmField
    var brand_id: String? = null
    var family: String? = null
    var sub_family: String? = null
    var type: String? = null
    var sale_method_id: String? = null
    @JvmField
    var status: String? = null
    var updated_at: String? = null

    @SerializedName("pivot")
    var pivot: Pivot? = null

    override fun toString(): String {
        return "Productos{" +
                "id=" + id +
                ", stock_min='" + stock_min + '\'' +
                ", stock_max='" + stock_max + '\'' +
                ", barcode='" + barcode + '\'' +
                ", code='" + code + '\'' +
                ", units_per_box='" + units_per_box + '\'' +
                ", description='" + description + '\'' +
                ", product_category_id='" + product_category_id + '\'' +
                ", product_type_id='" + product_type_id + '\'' +
                ", cost='" + cost + '\'' +
                ", suggested='" + suggested + '\'' +
                ", bonus='" + bonus + '\'' +
                ", utility='" + utility + '\'' +
                ", percentage_of_utility='" + percentage_of_utility + '\'' +
                ", sale_price='" + sale_price + '\'' +
                ", iva='" + iva + '\'' +
                ", sale_price2='" + sale_price2 + '\'' +
                ", sale_price3='" + sale_price3 + '\'' +
                ", sale_price4='" + sale_price4 + '\'' +
                ", sale_price5='" + sale_price5 + '\'' +
                ", brand_id='" + brand_id + '\'' +
                ", family='" + family + '\'' +
                ", sub_family='" + sub_family + '\'' +
                ", type='" + type + '\'' +
                ", sale_method_id='" + sale_method_id + '\'' +
                ", status='" + status + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", pivot=" + pivot +
                '}'
    }
}


