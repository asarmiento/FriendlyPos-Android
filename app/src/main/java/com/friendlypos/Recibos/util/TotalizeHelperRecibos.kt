package com.friendlysystemgroup.friendlypos.Recibos.util

import android.content.Context
import android.util.Log
import com.friendlysystemgroup.friendlypos.Recibos.activity.RecibosActivity
import com.friendlysystemgroup.friendlypos.Recibos.modelo.recibos
import com.friendlysystemgroup.friendlypos.login.util.SessionPrefes
import io.realm.Realm

/**
 * Clase auxiliar para totalizar recibos
 */
class TotalizeHelperRecibos(
    private var activity: RecibosActivity?,
    context: Context
) {
    private var customer: String? = null
    private var facturaId: String? = null
    private val session: SessionPrefes = SessionPrefes(context)

    /**
     * Libera recursos cuando ya no se necesita la instancia
     */
    fun destroy() {
        activity = null
    }

    /**
     * Totaliza una lista de recibos
     * @param pivotList Lista de recibos a totalizar
     */
    fun totalizeRecibos(pivotList: List<recibos>) {
        if (pivotList.isEmpty()) {
            Log.d("TotalizeHelper", "No hay recibos para totalizar")
            return
        }
        
        // Totalizar cada recibo individualmente
        pivotList.forEach { recibo ->
            totalize(recibo)
        }

        // Actualizar el monto por pagar en la base de datos
        actualizarMontoPorPagar()
    }
    
    /**
     * Actualiza el monto por pagar en la base de datos
     */
    private fun actualizarMontoPorPagar() {
        if (facturaId.isNullOrEmpty()) {
            Log.e("TotalizeHelper", "ID de factura no disponible para actualización")
            return
        }
        
        val finalTotalPagar = activity?.getTotalizarCancelado() ?: 0.0
        
        val realm = Realm.getDefaultInstance()
        try {
            realm.executeTransaction { r ->
                val reciboActualizado = r.where(recibos::class.java)
                    .equalTo("invoice_id", facturaId)
                    .findFirst()
                    
                reciboActualizado?.let {
                    it.porPagar = finalTotalPagar
                    r.insertOrUpdate(it)
                    Log.d("ReciboActualizado", "Recibo con ID $facturaId actualizado: ${it.porPagar}")
                } ?: Log.e("TotalizeHelper", "No se encontró el recibo con ID $facturaId")
            }
        } finally {
            realm.close()
        }
    }

    /**
     * Totaliza un recibo individual
     * @param currentPivot Recibo a totalizar
     */
    private fun totalize(currentPivot: recibos) {
        val totalPago = currentPivot.total
        val totalPagado = currentPivot.paid
        val totalPagar = totalPago - totalPagado

        Log.d("Totalizando", "Total: $totalPago, Pagado: $totalPagado, Por pagar: $totalPagar")

        activity?.apply {
            setTotalizarTotal(totalPago)
            setTotalizarCancelado(totalPagar)
        }

        facturaId = currentPivot.invoice_id
    }

    companion object {
        private const val IVA = 13.0
    }
}
