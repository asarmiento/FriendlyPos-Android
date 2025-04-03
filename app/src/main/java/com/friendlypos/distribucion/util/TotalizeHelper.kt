package com.friendlypos.distribucion.util

import android.util.Log
import com.friendlypos.distribucion.activity.DistribucionActivity
import com.friendlypos.distribucion.modelo.Pivot
import com.friendlypos.distribucion.modelo.sale
import com.friendlypos.principal.modelo.Clientes
import com.friendlypos.principal.modelo.Productos
import io.realm.Realm

class TotalizeHelper(var activity: DistribucionActivity?) {
    fun destroy() {
        activity = null
    }

    private fun getProductTypeByPivotId(id: String): String {
        val realm = Realm.getDefaultInstance()
        val tipo = realm.where(Productos::class.java).equalTo("id", id)
            .findFirst()!!
            .product_type_id
        realm.close()
        return tipo
    }

    private fun getProductIVAByPivotId(id: String?): Double {
        val realm = Realm.getDefaultInstance()
        val iva = realm.where(Productos::class.java).equalTo("id", id)
            .findFirst()!!
            .iva
        realm.close()
        return iva
    }

    private fun getClienteFixedDescuentoByPivotId(id: String?): Double {
        val realm = Realm.getDefaultInstance()

        val venta = realm.where(sale::class.java).equalTo("invoice_id", id).findFirst()
        val clienteFixedDescuento = realm.where(Clientes::class.java)
            .equalTo("id", venta!!.customer_id).findFirst()!!
            .fixedDiscount.toDouble()

        realm.close()
        return clienteFixedDescuento
    }

    fun totalize(pivotList: List<Pivot>) {
        for (p in pivotList) {
            totalize(p)
        }
    }

    private fun totalize(currentPivot: Pivot) {
        val clienteFixedDescuento = getClienteFixedDescuentoByPivotId(currentPivot.invoice_id)
        val iva = getProductIVAByPivotId(currentPivot.product_id)


        var subGrab = 0.0
        var subGrabConImp = 0.0
        var discountBill = 0.0
        var subGrabm = 0.0
        var subExen = 0.0
        var IvaT = 0.0
        var subt = 0.0
        var total = 0.0
        val cantidad = currentPivot.amount!!.toDouble()
        val precio = currentPivot.price!!.toDouble()
        val descuento = currentPivot.discount!!.toDouble()
        var subGrabDesc = 0.0

        if (iva > 0.0) {
            subGrabConImp = subGrab + (precio) * (cantidad)

            Log.d("IVA", iva.toString() + "")
            val ivaConvertido = (iva / 100) + 1
            Log.d("ivaa", ivaConvertido.toString() + "")

            subGrab = (subGrab + (precio) * (cantidad)) / ivaConvertido


            subGrabm =
                subGrabm + ((precio) * (cantidad) - ((descuento / 100) * (precio) * (cantidad)))
            discountBill += ((descuento / 100) * subGrab)
            Log.d("discountBillGr", discountBill.toString() + "")

            subGrabDesc = subGrab - discountBill
            Log.d("subGrabDesc", subGrabDesc.toString() + "")
        } else {
            subExen = subExen + ((precio) * (cantidad))
            discountBill += ((descuento / 100) * subExen)
            Log.d("discountBillEx", discountBill.toString() + "")
        }

        discountBill += ((subExen * (clienteFixedDescuento / 100.00)) + (subGrabm * (clienteFixedDescuento / 100.00)))


        if (subGrab > 0) {
            //  IvaT = (subGrabm - (subGrabm * (clienteFixedDescuento / 100.00))) * (IVA / 100);
            // IvaT = subGrabConImp - subGrab;
            val impuesto = iva / 100
            IvaT = subGrabDesc * impuesto
            Log.d("IvaT", IvaT.toString() + "")
        } else {
            IvaT = 0.0
        }

        subt = subGrab + subExen

        Log.d("subtotal", subt.toString() + "")
        total = (subt + IvaT) - discountBill

        activity!!.setTotalizarSubGrabado(subGrab)
        activity!!.setTotalizarSubExento(subExen)
        activity!!.setTotalizarSubTotal(subt)
        activity!!.setTotalizarDescuento(discountBill)

        activity!!.setTotalizarImpuestoIVA(IvaT)
        activity!!.setTotalizarTotal(total)
    }

    companion object {
        private const val IVA = 13.0
    }
}
