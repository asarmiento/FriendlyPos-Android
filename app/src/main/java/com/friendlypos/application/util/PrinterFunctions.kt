package com.friendlysystemgroup.friendlypos.application.util

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.friendlysystemgroup.friendlypos.Recibos.modelo.receipts
import com.friendlysystemgroup.friendlypos.Recibos.modelo.recibos
import com.friendlysystemgroup.friendlypos.application.bluetooth.PrinterService
import com.friendlysystemgroup.friendlypos.application.util.Functions.doubleToString1
import com.friendlysystemgroup.friendlypos.application.util.Functions.paddigTabs
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Inventario
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Pivot
import com.friendlysystemgroup.friendlypos.distribucion.modelo.invoice
import com.friendlysystemgroup.friendlypos.distribucion.modelo.sale
import com.friendlysystemgroup.friendlypos.login.modelo.Usuarios
import com.friendlysystemgroup.friendlypos.login.util.SessionPrefes
import com.friendlysystemgroup.friendlypos.principal.activity.MenuPrincipal
import com.friendlysystemgroup.friendlypos.principal.modelo.Clientes
import com.friendlysystemgroup.friendlypos.principal.modelo.Productos
import com.friendlysystemgroup.friendlypos.principal.modelo.Sysconf
import io.realm.Realm
import io.realm.RealmResults
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Clase de utilidad para manejar las impresiones de comprobantes
 */
object PrinterFunctions {
    // Constantes
    private const val TAG = "PrinterFunctions"
    private const val FACTURA_ELECTRONICA = "01"
    private const val TIQUETE_ELECTRONICO = "04"
    private const val METODO_PAGO_CONTADO = "1"
    private const val METODO_PAGO_CREDITO = "2"
    
    // Handlers para tareas asíncronas
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    
    // Totales para impresión de reportes
    private var printSalesCashTotal = 0.0
    private var printLiqContadoTotal = 0.0
    private var printLiqCreditoTotal = 0.0
    private var printLiqRecibosTotal = 0.0
    private var printRecibosTotal = 0.0
    
    // Información de totales a imprimir
    private var totalGrabado = ""
    private var totalExento = ""
    private var totalSubtotal = ""
    private var totalDescuento = ""
    private var totalImpuesto = ""
    private var totalTotal = ""
    private var totalCancelado = ""
    private var totalVuelto = ""
    private var totalNotas = ""
    private var totalNotasRecibos = ""
    private var precio = 0.0

    /**
     * Prepara y envía los datos para imprimir una factura de distribución
     * 
     * @param type Tipo de impresión
     * @param sale Venta a imprimir
     * @param context Contexto de la aplicación
     * @param ptype Tipo de papel
     * @param cantidadImpresiones Número de copias a imprimir
     */
    fun datosImprimirDistrTotal(
        type: Int,
        sale: sale,
        context: Context,
        ptype: Int,
        cantidadImpresiones: String
    ) {
        val impresiones = cantidadImpresiones.toIntOrNull() ?: 1
        Log.d(TAG, "Imprimiendo $impresiones copias de factura")

        for (i in 1..impresiones) {
            try {
                imprimirCopiaFacturaDistribucion(type, sale, context, ptype, i)
            } catch (e: Exception) {
                Log.e(TAG, "Error al imprimir copia $i: ${e.message}", e)
                Toast.makeText(context, "Error al imprimir copia $i", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    /**
     * Imprime una copia de factura de distribución
     */
    private fun imprimirCopiaFacturaDistribucion(
        type: Int,
        sale: sale,
        context: Context,
        ptype: Int,
        numeroCopia: Int
    ) {
        Realm.getDefaultInstance().use { realm ->
            try {
                // Obtener datos principales
                val sysconf = realm.where(Sysconf::class.java).findFirst()
                val cliente = realm.where(Clientes::class.java)
                    .equalTo("id", sale.customer_id)
                    .findFirst() ?: run {
                        Log.e(TAG, "No se encontró el cliente con ID: ${sale.customer_id}")
                        return
                    }
                
                val factura = realm.where(invoice::class.java)
                    .equalTo("id", sale.invoice_id)
                    .findFirst() ?: run {
                        Log.e(TAG, "No se encontró la factura con ID: ${sale.invoice_id}")
                        return
                    }
                
                // Cargar productos
                val productos = realm.where(Pivot::class.java)
                    .equalTo("invoice_id", sale.invoice_id)
                    .equalTo("devuelvo", 0)
                    .findAll()
                
                // Preparar datos de la factura
                prepararDatosFactura(factura)
                
                // Información del usuario
                val usuario = realm.where(Usuarios::class.java)
                    .equalTo("id", factura.user_id)
                    .findFirst()
                
                // Generar el contenido a imprimir según preferencias
                val contenidoImpresion = generarContenidoImpresion(
                    context,
                    factura,
                    cliente,
                    sysconf,
                    productos,
                    usuario,
                    ptype
                )
                
                // Enviar a imprimir
                enviarAImpresora(context, contenidoImpresion)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error al preparar la impresión: ${e.message}", e)
                Toast.makeText(context, "Error al preparar la impresión", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    /**
     * Prepara los datos formateados de la factura
     */
    private fun prepararDatosFactura(factura: invoice) {
        totalGrabado = doubleToString1(factura.subtotal_taxed.toDouble())
        totalExento = doubleToString1(factura.subtotal_exempt.toDouble())
        totalSubtotal = doubleToString1(factura.subtotal.toDouble())
        totalDescuento = doubleToString1(factura.discount.toDouble())
        totalImpuesto = doubleToString1(factura.tax.toDouble())
        totalTotal = doubleToString1(factura.total.toDouble())
        totalCancelado = doubleToString1(factura.paid.toDouble())
        totalVuelto = doubleToString1(factura.changing.toDouble())
        totalNotas = factura.note ?: ""
    }
    
    /**
     * Genera el contenido a imprimir según el tipo de impresora
     */
    private fun generarContenidoImpresion(
        context: Context,
        factura: invoice,
        cliente: Clientes,
        sysconf: Sysconf?,
        productos: RealmResults<Pivot>,
        usuario: Usuarios?,
        ptype: Int
    ): String {
        // Obtener preferencias de impresora
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val tipoImpresora = sharedPreferences.getString("pref_selec_impresora", "1") ?: "1"
        
        // Determinar tipo de factura
        val tipoFactura = when (factura.type) {
            FACTURA_ELECTRONICA -> "Factura Electronica"
            TIQUETE_ELECTRONICO -> "Tiquete Electronico"
            else -> "Factura"
        }
        
        // Determinar método de pago
        val metodoPago = when (factura.payment_method_id) {
            METODO_PAGO_CONTADO -> "Contado"
            METODO_PAGO_CREDITO -> "Credito"
            else -> "Desconocido"
        }
        
        // Generar contenido según tipo de impresora
        return if (tipoImpresora == "1") {
            generarContenidoZebra(tipoFactura, sysconf, cliente, factura, productos, usuario, metodoPago)
        } else {
            generarContenidoGenerico(tipoFactura, sysconf, cliente, factura, productos, usuario, metodoPago)
        }
    }
    
    /**
     * Genera contenido para impresoras Zebra
     */
    private fun generarContenidoZebra(
        tipoFactura: String,
        sysconf: Sysconf?,
        cliente: Clientes,
        factura: invoice,
        productos: RealmResults<Pivot>,
        usuario: Usuarios?,
        metodoPago: String
    ): String {
        // Datos de empresa
        val nombreNegocio = sysconf?.business_name ?: ""
        val nombre = sysconf?.name ?: ""
        val identificacion = sysconf?.identification ?: ""
        val direccion = sysconf?.direction ?: ""
        val telefono = sysconf?.phone ?: ""
        val correo = sysconf?.email ?: ""
        
        return """! U1 JOURNAl
! U1 SETLP 0 0 0

! U1 SETLP 5 3 70
! U1 LMARGIN 0
$tipoFactura
! U1 SETLP 7 0 14
------------------------------------------------


! U1 SETLP 5 1 35
$nombreNegocio
! U1 SETLP 7 0 14
------------------------------------------------

! U1 SETLP 7 0 14
$nombre
Cedula Juridica: $identificacion
$direccion
Tel. $telefono
Correo Electronico: $correo
------------------------------------------------
"""
        // NOTA: Este es un fragmento inicial del contenido, se continuaría con el resto de la factura
    }
    
    /**
     * Genera contenido para impresoras genéricas
     */
    private fun generarContenidoGenerico(
        tipoFactura: String,
        sysconf: Sysconf?,
        cliente: Clientes,
        factura: invoice,
        productos: RealmResults<Pivot>,
        usuario: Usuarios?,
        metodoPago: String
    ): String {
        // Implementación para impresoras genéricas
        return "Contenido genérico no implementado"
    }
    
    /**
     * Envía el contenido a la impresora
     */
    private fun enviarAImpresora(context: Context, contenido: String) {
        val intent = Intent(PrinterService.BROADCAST_CLASS + "TO_PRINT")
        intent.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true")
        intent.putExtra("bill_to_print", contenido)
        context.sendBroadcast(intent)
    }

    /**
     * Imprime una liquidación desde el menú principal
     */
    fun imprimirLiquidacionMenu(context: Context) {
        try {
            // Implementar lógica para imprimir liquidación
            val contenido = generarContenidoLiquidacion(context)
            enviarAImpresora(context, contenido)
        } catch (e: Exception) {
            Log.e(TAG, "Error al imprimir liquidación: ${e.message}", e)
            Toast.makeText(context, "Error al imprimir liquidación", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Genera el contenido para imprimir una liquidación
     */
    private fun generarContenidoLiquidacion(context: Context): String {
        // Implementar lógica para generar contenido de liquidación
        return "Contenido de liquidación"
    }
    
    /**
     * Imprime una orden de carga desde el menú principal
     */
    fun imprimirOrdenCarga(context: Context) {
        try {
            // Implementar lógica para imprimir orden de carga
            val contenido = generarContenidoOrdenCarga(context)
            enviarAImpresora(context, contenido)
        } catch (e: Exception) {
            Log.e(TAG, "Error al imprimir orden de carga: ${e.message}", e)
            Toast.makeText(context, "Error al imprimir orden de carga", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Genera el contenido para imprimir una orden de carga
     */
    private fun generarContenidoOrdenCarga(context: Context): String {
        // Implementar lógica para generar contenido de orden de carga
        return "Contenido de orden de carga"
    }
    
    /**
     * Imprime una devolución desde el menú principal
     */
    fun imprimirDevoluciónMenu(context: Context) {
        try {
            // Implementar lógica para imprimir devolución
            val contenido = generarContenidoDevolución(context)
            enviarAImpresora(context, contenido)
        } catch (e: Exception) {
            Log.e(TAG, "Error al imprimir devolución: ${e.message}", e)
            Toast.makeText(context, "Error al imprimir devolución", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Genera el contenido para imprimir una devolución
     */
    private fun generarContenidoDevolución(context: Context): String {
        // Implementar lógica para generar contenido de devolución
        return "Contenido de devolución"
    }

    // Métodos de extensión para facilitar la conversión y formateo
    private fun Double.formatoMoneda(): String = doubleToString1(this)
    
    private fun String?.valorOVacio(): String = this ?: ""
    
    // El resto de las funciones de impresión...
}
