package com.friendlysystemgroup.friendlypos.application.util

import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import com.friendlysystemgroup.friendlypos.Recibos.modelo.receipts
import com.friendlysystemgroup.friendlypos.application.bluetooth.PrinterService
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Pivot
import com.friendlysystemgroup.friendlypos.distribucion.modelo.invoice
import com.friendlysystemgroup.friendlypos.distribucion.modelo.sale
import com.friendlysystemgroup.friendlypos.login.modelo.Usuarios
import com.friendlysystemgroup.friendlypos.principal.modelo.Clientes
import com.friendlysystemgroup.friendlypos.principal.modelo.Sysconf
import io.realm.Realm
import io.realm.RealmResults
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Clase principal para manejar operaciones de impresión
 * Implementa patrón Singleton para centralizar las operaciones de impresión
 */
class PrintingManager private constructor() {
    
    companion object {
        private const val TAG = "PrintingManager"
        
        // Singleton instance
        @Volatile
        private var instance: PrintingManager? = null
        
        /**
         * Obtiene o crea la instancia única del administrador de impresión
         */
        fun getInstance(): PrintingManager {
            return instance ?: synchronized(this) {
                instance ?: PrintingManager().also { instance = it }
            }
        }
    }
    
    // Proveedores de contenido de impresión
    private val facturaContentProvider = FacturaContentProvider()
    private val reciboContentProvider = ReciboContentProvider()
    private val liquidacionContentProvider = LiquidacionContentProvider()
    private val devolucionContentProvider = DevolucionContentProvider() 
    private val ordenCargaContentProvider = OrdenCargaContentProvider()
    
    /**
     * Imprime una factura de distribución
     */
    fun imprimirFactura(
        sale: sale, 
        context: Context, 
        tipoImpresion: Int, 
        cantidadImpresiones: String
    ) {
        val impresiones = cantidadImpresiones.toIntOrNull() ?: 1
        Log.d(TAG, "Imprimiendo $impresiones copias de factura")
        
        for (i in 1..impresiones) {
            try {
                imprimirCopiaFactura(sale, context, tipoImpresion, i)
            } catch (e: Exception) {
                Log.e(TAG, "Error al imprimir copia $i: ${e.message}", e)
                Toast.makeText(context, "Error al imprimir copia $i", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    /**
     * Imprime una copia de factura
     */
    private fun imprimirCopiaFactura(
        sale: sale,
        context: Context,
        tipoImpresion: Int,
        numeroCopia: Int
    ) {
        Realm.getDefaultInstance().use { realm ->
            try {
                // Obtener datos principales
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
                
                // Obtener configuración del sistema y productos
                val sysconf = realm.where(Sysconf::class.java).findFirst()
                val productos = realm.where(Pivot::class.java)
                    .equalTo("invoice_id", sale.invoice_id)
                    .equalTo("devuelvo", 0)
                    .findAll()
                
                // Obtener usuario
                val usuario = realm.where(Usuarios::class.java)
                    .equalTo("id", factura.user_id)
                    .findFirst()
                
                // Generar contenido
                val contenido = facturaContentProvider.generarContenido(
                    context, factura, cliente, sysconf, productos, usuario, tipoImpresion
                )
                
                // Enviar a impresora
                enviarAImpresora(context, contenido)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error generando impresión: ${e.message}", e)
                Toast.makeText(context, "Error al preparar la impresión", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    /**
     * Imprime un recibo
     */
    fun imprimirRecibo(
        recibo: receipts,
        context: Context,
        cantidadImpresiones: String
    ) {
        val impresiones = cantidadImpresiones.toIntOrNull() ?: 1
        
        for (i in 1..impresiones) {
            try {
                val contenido = reciboContentProvider.generarContenido(context, recibo)
                enviarAImpresora(context, contenido)
            } catch (e: Exception) {
                Log.e(TAG, "Error al imprimir recibo: ${e.message}", e)
                Toast.makeText(context, "Error al imprimir recibo", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    /**
     * Imprime una liquidación
     */
    fun imprimirLiquidacion(context: Context) {
        try {
            val contenido = liquidacionContentProvider.generarContenido(context)
            enviarAImpresora(context, contenido)
        } catch (e: Exception) {
            Log.e(TAG, "Error al imprimir liquidación: ${e.message}", e)
            Toast.makeText(context, "Error al imprimir liquidación", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Imprime una devolución
     */
    fun imprimirDevolucion(context: Context) {
        try {
            val contenido = devolucionContentProvider.generarContenido(context)
            enviarAImpresora(context, contenido)
        } catch (e: Exception) {
            Log.e(TAG, "Error al imprimir devolución: ${e.message}", e)
            Toast.makeText(context, "Error al imprimir devolución", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Imprime una orden de carga
     */
    fun imprimirOrdenCarga(context: Context) {
        try {
            val contenido = ordenCargaContentProvider.generarContenido(context)
            enviarAImpresora(context, contenido)
        } catch (e: Exception) {
            Log.e(TAG, "Error al imprimir orden de carga: ${e.message}", e)
            Toast.makeText(context, "Error al imprimir orden de carga", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Método común para enviar a la impresora
     */
    private fun enviarAImpresora(context: Context, contenido: String) {
        val intent = Intent(PrinterService.BROADCAST_CLASS + "TO_PRINT")
        intent.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true")
        intent.putExtra("bill_to_print", contenido)
        context.sendBroadcast(intent)
    }
}

/**
 * Interfaz para proveedores de contenido de impresión
 */
interface PrintContentProvider {
    fun generarContenido(context: Context, vararg params: Any): String
}

/**
 * Proveedor de contenido para facturas
 */
class FacturaContentProvider : PrintContentProvider {
    
    companion object {
        private const val FACTURA_ELECTRONICA = "01"
        private const val TIQUETE_ELECTRONICO = "04"
        private const val METODO_PAGO_CONTADO = "1"
        private const val METODO_PAGO_CREDITO = "2"
    }
    
    override fun generarContenido(context: Context, vararg params: Any): String {
        // Extraer parámetros
        val factura = params[0] as invoice
        val cliente = params[1] as Clientes
        val sysconf = params[2] as Sysconf?
        val productos = params[3] as RealmResults<Pivot>
        val usuario = params[4] as Usuarios?
        val tipoImpresion = params[5] as Int
        
        // Obtener tipo de impresora de preferencias
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
        
        // Formato de fecha
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaFormateada = sdf.format(Date())
        
        // Aquí construirías el contenido completo para la impresora Zebra
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

Fecha: $fechaFormateada
Cliente: ${cliente.fantasyName}
Cedula: ${cliente.identification}
Documento: ${factura.numeration}
------------------------------------------------

DETALLE DE PRODUCTOS
------------------------------------------------
"""
        // Este es un ejemplo básico, debería expandirse con más detalles
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
        return "Factura genérica para impresora estándar"
    }
}

/**
 * Proveedor de contenido para recibos
 */
class ReciboContentProvider : PrintContentProvider {
    override fun generarContenido(context: Context, vararg params: Any): String {
        val recibo = params[0] as receipts
        // Implementar generación de contenido para recibos
        return "Contenido de recibo (implementar)"
    }
}

/**
 * Proveedor de contenido para liquidaciones
 */
class LiquidacionContentProvider : PrintContentProvider {
    override fun generarContenido(context: Context, vararg params: Any): String {
        // Implementar generación de contenido para liquidaciones
        return "Contenido de liquidación (implementar)"
    }
}

/**
 * Proveedor de contenido para devoluciones
 */
class DevolucionContentProvider : PrintContentProvider {
    override fun generarContenido(context: Context, vararg params: Any): String {
        // Implementar generación de contenido para devoluciones
        return "Contenido de devolución (implementar)"
    }
}

/**
 * Proveedor de contenido para órdenes de carga
 */
class OrdenCargaContentProvider : PrintContentProvider {
    override fun generarContenido(context: Context, vararg params: Any): String {
        // Implementar generación de contenido para órdenes de carga
        return "Contenido de orden de carga (implementar)"
    }
} 