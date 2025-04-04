package com.friendlysystemgroup.friendlypos.Recibos.util

import android.util.Log
import com.friendlysystemgroup.friendlypos.Recibos.activity.RecibosActivity
import com.friendlysystemgroup.friendlypos.Recibos.modelo.recibos
import com.friendlysystemgroup.friendlypos.login.util.SessionPrefes
import io.realm.Realm
import io.realm.internal.SyncObjectServerFacade

class TotalizeHelperRecibos(var activity: RecibosActivity?) {
    var customer: String? = null
    var session: SessionPrefes = SessionPrefes(SyncObjectServerFacade.getApplicationContext())
    var facturaId: String? = null

    fun destroy() {
        activity = null
    }


    fun totalizeRecibos(pivotList: List<recibos>) {
        for (p in pivotList) {
            totalize(p)
        }

        val realm2 = Realm.getDefaultInstance()
        val finalTotalPagar = activity!!.getTotalizarCancelado()

        realm2.executeTransaction { realm2 ->
            val recibo_actualizado =
                realm2.where(recibos::class.java).equalTo("invoice_id", facturaId)
                    .findFirst()
            recibo_actualizado!!.porPagar = finalTotalPagar

            realm2.insertOrUpdate(recibo_actualizado)
            realm2.close()
            Log.d("ACTRECIBOPAGAR", recibo_actualizado.toString() + "")
        }
    }

    private fun totalize(currentPivot: recibos) {
        val totalPago = currentPivot.total

        var total = 0.0
        total = totalPago


        val totalPagado = currentPivot.paid

        var totalPagar = 0.0
        totalPagar = totalPago - totalPagado

        Log.d("TOTALTODOS", total.toString() + "")

        activity!!.setTotalizarTotal(total)
        activity!!.setTotalizarCancelado(totalPagar)

        facturaId = currentPivot.invoice_id
    }

    companion object {
        private const val IVA = 13.0
        private const val productosDelBonus = 0.0
    }
}
