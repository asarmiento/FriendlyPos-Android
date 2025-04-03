package com.friendlypos.reenvio_email.modelo

import com.google.gson.annotations.SerializedName

class EmailResponse(
    @field:SerializedName("customer") var customer: customer, @field:SerializedName(
        "invoices"
    ) var facturas: List<invoices>
) {
    override fun toString(): String {
        return "EmailResponse{" +
                "customer='" + customer + '\'' +
                ", facturas=" + facturas +
                '}'
    }
}
