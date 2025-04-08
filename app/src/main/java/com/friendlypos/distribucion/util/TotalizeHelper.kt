package com.friendlysystemgroup.friendlypos.distribucion.util

import android.util.Log
import com.friendlysystemgroup.friendlypos.distribucion.activity.DistribucionActivity
import com.friendlysystemgroup.friendlypos.distribucion.modelo.InvoiceItem
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Pivot
import com.friendlysystemgroup.friendlypos.distribucion.modelo.invoice
import com.friendlysystemgroup.friendlypos.distribucion.modelo.sale
import com.friendlysystemgroup.friendlypos.login.modelo.Usuarios
import com.friendlysystemgroup.friendlypos.login.util.SessionPrefes
import com.friendlysystemgroup.friendlypos.principal.modelo.ConsecutivosNumberFe
import com.friendlysystemgroup.friendlypos.principal.modelo.Sysconf
import com.friendlysystemgroup.friendlypos.principal.modelo.datosTotales
import com.friendlysystemgroup.friendlypos.utils.Utils
import io.realm.Realm
import io.realm.RealmList
import java.util.*

class TotalizeHelper(private val activity: DistribucionActivity?) {
    private var subtotalGrabado = 0.0
    private var subtotalExento = 0.0
    private var subtotal = 0.0
    private var descuento = 0.0
    private var impuesto = 0.0
    private var total = 0.0
    
    private var facturaId: String? = null
    private var numeroConsecutivo: String? = null
    private var keyElectronica: String? = null
    private var session: SessionPrefes? = null
    
    init {
        session = SessionPrefes(activity?.applicationContext)
        calculateTotals()
    }
    
    private fun calculateTotals() {
        activity?.let { act ->
            subtotalGrabado = act.getTotalizarSubGrabado()
            subtotalExento = act.getTotalizarSubExento()
            subtotal = act.getTotalizarSubTotal()
            descuento = act.getTotalizarDescuento()
            impuesto = act.getTotalizarImpuestoIVA()
            total = act.getTotalizarTotal()
            facturaId = act.invoiceId
            
            Log.d("TotalizeHelper", "Calculated totals: $total")
        }
    }
    
    fun getSubtotalGrabado(): Double = subtotalGrabado
    fun getSubtotalExento(): Double = subtotalExento
    fun getSubTotal(): Double = subtotal
    fun getDescuento(): Double = descuento
    fun getTotalTax(): Double = impuesto
    fun getTotal(): Double = total
    
    fun saveInvoice(esEfectivo: Boolean, notas: String, clienteId: String?): Boolean {
        return try {
            val pagoCon = if (esEfectivo) 0.0 else 0.0 // Esto se modificará con el monto real
            val metodoPago = if (esEfectivo) "1" else "2"
            
            createElectronicInvoice()
            updateInvoice(notas, pagoCon, metodoPago)
            updateSale(clienteId)
            updateTotalData()
            
            true
        } catch (e: Exception) {
            Log.e("TotalizeHelper", "Error guardando factura: ${e.message}", e)
            false
        }
    }
    
    private fun createElectronicInvoice() {
        val realm = Realm.getDefaultInstance()
        
        try {
            val sysconf = realm.where(Sysconf::class.java).findFirst()
            val sysSucursal = sysconf?.sucursal ?: ""
            
            val userEmail = session?.usuarioPrefs
            val usuario = realm.where(Usuarios::class.java).equalTo("email", userEmail).findFirst()
            val userTerminal = usuario?.terminal ?: ""
            val userId = usuario?.id
            
            val factura = realm.where(invoice::class.java).equalTo("id", facturaId).findFirst()
            val invType = factura?.type ?: ""
            
            Log.d("TotalizeHelper", "sysSucursal: $sysSucursal")
            Log.d("TotalizeHelper", "userTerminal: $userTerminal")
            Log.d("TotalizeHelper", "invType: $invType")
            
            // Obtener número consecutivo
            var nextId = 1
            realm.executeTransaction { r ->
                val numero = r.where(ConsecutivosNumberFe::class.java)
                    .equalTo("user_id", userId)
                    .equalTo("type_doc", invType)
                    .max("number_consecutive")
                    
                nextId = if (numero == null) 1 else numero.toInt() + 1
                
                // Formatear número consecutivo
                val consConsecutivo = String.format("%010d", nextId)
                numeroConsecutivo = sysSucursal + userTerminal + invType + consConsecutivo
                
                // Actualizar consecutivo
                val consecutivo = r.where(ConsecutivosNumberFe::class.java)
                    .equalTo("user_id", userId)
                    .equalTo("type_doc", invType)
                    .findFirst()
                    
                consecutivo?.number_consecutive = nextId
                r.insertOrUpdate(consecutivo)
            }
            
            // Generar clave electrónica
            val sysIdNumberAtv = sysconf?.id_number_atv ?: 0
            val consConsecutivoATV = String.format("%012d", sysIdNumberAtv)
            
            val codSeguridad = Random().nextInt(90000000) + 10000000
            val dateFormat = Utils.getDateConsecutivo()
            
            keyElectronica = "506$dateFormat$consConsecutivoATV$numeroConsecutivo" + "3$codSeguridad"
            Log.d("TotalizeHelper", "keyElectronica: $keyElectronica")
            
        } finally {
            realm.close()
        }
    }
    
    private fun updateInvoice(notas: String, pagoCon: Double, metodoPago: String) {
        val realm = Realm.getDefaultInstance()
        
        try {
            realm.executeTransaction { r ->
                val factura = r.where(invoice::class.java).equalTo("id", facturaId).findFirst()
                val userEmail = session?.usuarioPrefs
                val usuario = r.where(Usuarios::class.java).equalTo("email", userEmail).findFirst()
                
                factura?.apply {
                    date = Utils.getDateToday()
                    times = Utils.getCurrentTime()
                    
                    key = keyElectronica
                    consecutive_number = numeroConsecutivo
                    
                    subtotal_taxed = subtotalGrabado.toString()
                    subtotal_exempt = subtotalExento.toString()
                    subtotal = this@TotalizeHelper.subtotal.toString()
                    discount = descuento.toString()
                    tax = impuesto.toString()
                    total = this@TotalizeHelper.total.toString()
                    
                    this.paid = pagoCon.toString()
                    this.changing = "0.0" // Se calculará después
                    user_id_applied = usuario?.id
                    note = notas
                    canceled = "1"
                    aplicada = 1
                    subida = 1
                    facturaDePreventa = "Distribucion"
                    
                    // Obtener productos de la factura
                    val productos = r.where(Pivot::class.java)
                        .equalTo("invoice_id", facturaId)
                        .findAll()
                    
                    val productosRealmList = RealmList<Pivot>()
                    productosRealmList.addAll(productos)
                    productofactura = productosRealmList
                    
                    Log.d("TotalizeHelper", "Factura actualizada: $this")
                }
            }
        } finally {
            realm.close()
        }
    }
    
    private fun updateSale(clienteId: String?) {
        val realm = Realm.getDefaultInstance()
        
        try {
            realm.executeTransaction { r ->
                val venta = r.where(sale::class.java).equalTo("invoice_id", facturaId).findFirst()
                
                venta?.apply {
                    sale_type = "1"
                    applied = "1"
                    updated_at = "${Utils.getDateToday()} ${Utils.getCurrentTime()}"
                    aplicada = 1
                    subida = 1
                    facturaDePreventa = "Distribucion"
                    
                    // Actualizar cliente si es necesario
                    customer_id = clienteId
                    
                    Log.d("TotalizeHelper", "Venta actualizada: $this")
                }
            }
        } finally {
            realm.close()
        }
    }
    
    private fun updateTotalData() {
        val realm = Realm.getDefaultInstance()
        
        try {
            realm.beginTransaction()
            
            // Obtener siguiente ID
            val currentIdNum = realm.where(datosTotales::class.java).max("id")
            val nextId = if (currentIdNum == null) 1 else currentIdNum.toInt() + 1
            
            // Crear nuevo registro de datos totales
            val datosTotales = datosTotales()
            datosTotales.id = nextId
            datosTotales.idTotal = 1
            datosTotales.nombreTotal = "Distribucion"
            datosTotales.totalDistribucion = total
            datosTotales.date = Utils.getDateToday()
            
            realm.copyToRealmOrUpdate(datosTotales)
            realm.commitTransaction()
            
            Log.d("TotalizeHelper", "Datos totales actualizados: $datosTotales")
        } finally {
            realm.close()
        }
    }
}
