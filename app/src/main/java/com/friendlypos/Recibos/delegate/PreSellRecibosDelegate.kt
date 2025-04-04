package com.friendlysystemgroup.friendlypos.Recibos.delegate

import android.util.Log
import com.friendlysystemgroup.friendlypos.Recibos.activity.RecibosActivity
import com.friendlysystemgroup.friendlypos.Recibos.modelo.receipts
import com.friendlysystemgroup.friendlypos.Recibos.modelo.receiptsDetalle
import com.friendlysystemgroup.friendlypos.Recibos.modelo.recibos
import io.realm.RealmList

class PreSellRecibosDelegate(var recibosActivity: RecibosActivity?) {
    var currentRecibos: receiptsDetalle? = null
    var productofacturas: MutableList<recibos> = ArrayList()

    fun initReciboDetalle(
        receipts_id: String?, customer_id: String?, reference: String?,
        date: String?, sum: String?, balance: Double, notes: String?
    ) {
        currentRecibos = receiptsDetalle()

        currentRecibos!!.d_receipts_id = receipts_id
        currentRecibos!!.d_customer_id = customer_id
        currentRecibos!!.d_reference = reference
        currentRecibos!!.d_date = date
        currentRecibos!!.d_sum = sum
        currentRecibos!!.d_balance = balance
        currentRecibos!!.d_notes = notes
        currentRecibos!!.d_listaRecibos = productofacturas

        Log.d("invoice1", currentRecibos.toString() + "")
    }


    fun insertRecibo(recibo: recibos) {
        productofacturas.add(recibo)

        Log.d("recibo2", productofacturas.toString() + "")
    }


    fun initRecibo(pos: Int): List<recibos> {
        if (productofacturas.isEmpty()) {
            Log.d("vacio", "vacio")
            // send = "No hay invoice emitidas";
        } else {
            val abonado = productofacturas[pos].abonado

            Log.d("invoiceDev", abonado.toString() + "")
            if (abonado == 1) {
                productofacturas[pos].abonado = 0
            } else {
                //  productofacturas.get(pos).removeAllChangeListeners();
                Log.d("vacio", "No hay productos")
            }
        }
        Log.d("invoiceremover", productofacturas.toString() + "")
        return productofacturas
    }


    val allRecibos: List<recibos>
        get() {
            if (productofacturas.isEmpty()) {
                Log.d("vacio", "vacio")
                // send = "No hay invoice emitidas";
            } else {
                for (i in productofacturas.indices) {
                    val abonado = productofacturas[i].abonado
                    Log.d("invoiceDev", abonado.toString() + "")
                    if (abonado == 1) {
                        productofacturas[i]
                    } else {
                        productofacturas.removeAt(i)
                        Log.d("vacio", "No hay productos")
                    }
                }
            }
            Log.d("invoice4", productofacturas.toString() + "")
            return productofacturas
        }


    fun destroy() {
        this.recibosActivity = null
        this.currentRecibos = null
    }

    val receiptsByReceiptsDetalle: receipts
        get() {
            val recibo = receipts()

            recibo.receipts_id = currentRecibos!!.d_receipts_id.toString()
            recibo.customer_id = currentRecibos!!.d_customer_id
            recibo.reference = currentRecibos!!.d_reference
            recibo.date = currentRecibos!!.d_date
            recibo.sum = currentRecibos!!.d_sum
            recibo.balance = currentRecibos!!.d_balance
            recibo.notes = currentRecibos!!.d_notes

            val results = RealmList<recibos>()

            results.addAll(productofacturas)
            recibo.listaRecibos = results


            Log.d("CREAR RECIBO", recibo.toString() + "")

            return recibo
        }
}
