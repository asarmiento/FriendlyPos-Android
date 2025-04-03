package com.friendlypos.preventas.util

import android.util.Log
import com.friendlypos.distribucion.modelo.Pivot
import com.friendlypos.login.util.SessionPrefes
import com.friendlypos.preventas.activity.PreventaActivity
import com.friendlypos.preventas.modelo.Bonuses
import com.friendlypos.principal.modelo.Clientes
import com.friendlypos.principal.modelo.Productos
import io.realm.Realm
import io.realm.internal.SyncObjectServerFacade

class TotalizeHelperPreventa(var activity: PreventaActivity?) {
    var customer: String? = null
    var session: SessionPrefes = SessionPrefes(SyncObjectServerFacade.getApplicationContext())

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

    private fun getProductBonusByPivotId(id: String?): String {
        val realm = Realm.getDefaultInstance()
        val bonus = realm.where(Productos::class.java).equalTo("id", id)
            .findFirst()!!
            .bonus
        realm.close()
        return bonus
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
        val ventaDetallePreventa = activity!!.currentVenta
        ventaDetallePreventa.invoice_id
        customer = ventaDetallePreventa.customer_id

        val realm = Realm.getDefaultInstance()

        val clienteFixedDescuento = realm.where(Clientes::class.java)
            .equalTo("id", customer).findFirst()!!
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
        val cantidad: Double

        val clienteFixedDescuento = getClienteFixedDescuentoByPivotId(currentPivot.invoice_id)
        val agrego = currentPivot.amountSinBonus
        val iva = getProductIVAByPivotId(currentPivot.product_id)
        val bonus = getProductBonusByPivotId(currentPivot.product_id)

        // TODO limpiar esBonus y cambiar en resumen el total
        if (bonus == "1" && currentPivot.bonus == 1) {
            val realmBonus = Realm.getDefaultInstance()

            realmBonus.executeTransactionAsync { realmBonus ->
                val productoConBonus = realmBonus.where(Bonuses::class.java)
                    .equalTo("product_id", currentPivot.product_id!!.toInt()).findFirst()
                productosDelBonus =
                    productoConBonus!!.product_bonus!!.toDouble()
                Log.d(
                    "BONIFTOTAL",
                    productoConBonus.product_id.toString() + " " + productosDelBonus
                )
            }

            cantidad = agrego
        } else {
            cantidad = currentPivot.amount!!.toDouble()
        }

        var subGrab = 0.0
        var subGrabConImp = 0.0
        var discountBill = 0.0
        var subGrabm = 0.0
        var subExen = 0.0
        var IvaT = 0.0
        var subt = 0.0
        var total = 0.0

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
        Log.d("discountBill1", discountBill.toString() + "")

        if (subGrab > 0) {
            //  IvaT = (subGrabm - (subGrabm * (clienteFixedDescuento / 100.00))) * (IVA / 100);
            //   IvaT = subGrabConImp - subGrab;

            val impuesto = iva / 100
            Log.d("IvaT", IvaT.toString() + "")
            IvaT = subGrabDesc * impuesto
            Log.d("IvaT", IvaT.toString() + "")
        } else {
            IvaT = 0.0
        }

        subt = subGrab + subExen

        // subTotal = String.format("%,.2f", subt);
        Log.d("subtotal", subt.toString() + "")
        total = (subt + IvaT) - discountBill

        // Total = String.format("%,.2f", total);
        // Log.d("total", total + "");
        activity!!.setTotalizarSubGrabado(subGrab)
        activity!!.setTotalizarSubExento(subExen)
        activity!!.setTotalizarSubTotal(subt)
        activity!!.setTotalizarDescuento(discountBill)

        activity!!.setTotalizarImpuestoIVA(IvaT)
        activity!!.setTotalizarTotal(total)

        //  activity.setTotalizarTotalDouble(total);
    }

    companion object {
        private const val IVA = 13.0
        private var productosDelBonus = 0.0
    }
}
