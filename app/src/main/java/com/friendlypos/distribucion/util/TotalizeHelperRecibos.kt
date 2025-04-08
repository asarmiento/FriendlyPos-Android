package com.friendlysystemgroup.friendlypos.distribucion.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.friendlysystemgroup.friendlypos.application.util.Functions
import com.friendlysystemgroup.friendlypos.application.util.Session
import com.friendlysystemgroup.friendlypos.distribucion.activity.DistribucionActivity
import com.friendlysystemgroup.friendlypos.distribucion.modelo.DiaTrabajo
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Recibos
import com.friendlysystemgroup.friendlypos.distribucion.modelo.RecibosDistLineItems
import com.friendlysystemgroup.friendlypos.distribucion.modelo.RecibosItems
import com.friendlysystemgroup.friendlypos.distribucion.modelo.RecibosTaxes
import com.friendlysystemgroup.friendlypos.principal.modelo.Clientes
import com.friendlysystemgroup.friendlypos.principal.modelo.ItemShared
import com.friendlysystemgroup.friendlypos.principal.modelo.PaymentMethods
import com.friendlysystemgroup.friendlypos.principal.modelo.Productos
import com.friendlysystemgroup.friendlypos.principal.modelo.Sysconf
import com.friendlysystemgroup.friendlypos.principal.modelo.Usuarios
import io.realm.Realm
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Clase de ayuda para el proceso de totalización de recibos
 * Proporciona métodos para guardar, calcular y actualizar recibos
 */
class TotalizeHelperRecibos(private val context: Context) {
    private var realm: Realm? = null
    
    // Variables para cálculos
    private var subtotalGravado = 0.0
    private var subtotalExento = 0.0
    private var subtotalTaxed = 0.0
    private var subtotalExempt = 0.0
    private var subtotal = 0.0
    private var impuestos = 0.0
    private var descuentoTotal = 0.0
    private var total = 0.0
    
    // Variables para gestión de recibos
    private var clienteId = ""
    private var metodoPagoId = ""
    private var nombreCliente = ""
    private var usuarioId = ""
    private var fechaActual = ""
    private var reciboId = ""
    
    companion object {
        private const val TAG = "TotalizeHelperRecibos"
        
        // Constantes para tipos
        const val TIPO_RECIBO_CONTADO = "1"
        const val TIPO_RECIBO_CREDITO = "2"
        const val TIPO_RECIBO_PROFORMA = "3"
        
        // Constantes para métodos de pago
        const val METODO_PAGO_CONTADO = "1"
        const val METODO_PAGO_CREDITO = "2"
    }
    
    /**
     * Inicializa el helper con el contexto y crea una instancia de Realm
     */
    init {
        try {
            realm = Realm.getDefaultInstance()
            fechaActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        } catch (e: Exception) {
            Log.e(TAG, "Error al inicializar Realm: ${e.message}", e)
        }
    }
    
    /**
     * Libera recursos y cierra la instancia de Realm
     */
    fun cerrar() {
        realm?.close()
        realm = null
    }
    
    /**
     * Guarda un recibo con la información proporcionada
     * 
     * @param context Contexto de la aplicación
     * @param tipoRecibo Tipo de recibo (contado, crédito, proforma)
     * @param monto Monto del recibo
     * @param metodoPago Método de pago utilizado
     * @param notas Notas adicionales para el recibo
     * @param clienteRecibo Cliente asociado al recibo
     * @param cambio Cambio devuelto al cliente
     * @param distribucionActivity Actividad desde la que se llama
     * @return El ID del recibo generado o null si hubo error
     */
    fun guardarRecibo(
        context: Context,
        tipoRecibo: String,
        monto: String,
        metodoPago: String,
        notas: String,
        clienteRecibo: String,
        cambio: String,
        distribucionActivity: DistribucionActivity?
    ): String? {
        try {
            // Inicializar variables
            realm = Realm.getDefaultInstance()
            usuarioId = Session.id
            clienteId = clienteRecibo
            metodoPagoId = metodoPago
            
            // Obtener detalles del cliente
            val cliente = realm?.where(Clientes::class.java)
                ?.equalTo("id", clienteId)
                ?.findFirst()
            
            nombreCliente = cliente?.name ?: ""
            
            // Configurar fecha y generar ID único
            val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val hora = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            val fechaHora = "$fecha $hora"
            
            // Generar ID único para el recibo
            reciboId = UUID.randomUUID().toString()
            
            // Iniciar cálculos
            calcularTotales(clienteId)
            
            // Guardar recibo en Realm
            realm?.executeTransaction { r ->
                // Crear objeto Recibos
                val recibo = r.createObject(Recibos::class.java, reciboId)
                
                // Establecer propiedades básicas
                recibo.apply {
                    receipt_type = tipoRecibo
                    user_id = usuarioId
                    customer_id = clienteId
                    customer_name = nombreCliente
                    subtotal_taxed = subtotalTaxed.toString()
                    subtotal_exempt = subtotalExempt.toString()
                    subtotal = this@TotalizeHelperRecibos.subtotal.toString()
                    tax = impuestos.toString()
                    discount = descuentoTotal.toString()
                    total = this@TotalizeHelperRecibos.total.toString()
                    paid = monto
                    changing = cambio
                    payment_method_id = metodoPagoId
                    note = notas
                    state = "1"
                    created_at = fechaHora
                    updated_at = fechaHora
                    date = fecha
                    numeracion = obtenerNumeracion()
                }
                
                // Guardar impuestos
                guardarImpuestos(r, reciboId)
                
                // Actualizar estado de ítems compartidos
                actualizarEstadoItems(r)
                
                // Actualizar día de trabajo
                actualizarDiaTrabajo(r, fechaActual, tipoRecibo)
            }
            
            // Obtener numeración actualizada
            val reciboGuardado = realm?.where(Recibos::class.java)
                ?.equalTo("id", reciboId)
                ?.findFirst()
            
            val numeracion = reciboGuardado?.numeracion ?: "0"
            
            // Mostrar mensaje de éxito
            Toast.makeText(
                context,
                "Recibo #$numeracion guardado exitosamente",
                Toast.LENGTH_SHORT
            ).show()
            
            // Limpiar ítems compartidos no utilizados
            limpiarItemsCompartidos()
            
            // Notificar a la actividad que el recibo fue guardado
            distribucionActivity?.mostrarResumenRecibo(reciboId)
            
            return reciboId
        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar recibo: ${e.message}", e)
            Toast.makeText(
                context,
                "Error al guardar recibo: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
            return null
        } finally {
            realm?.close()
        }
    }
    
    /**
     * Calcula los totales para un cliente específico
     * 
     * @param clienteId ID del cliente para el cual calcular los totales
     */
    private fun calcularTotales(clienteId: String) {
        try {
            // Reiniciar variables
            subtotalGravado = 0.0
            subtotalExento = 0.0
            subtotalTaxed = 0.0
            subtotalExempt = 0.0
            subtotal = 0.0
            impuestos = 0.0
            descuentoTotal = 0.0
            total = 0.0
            
            // Obtener lista de ítems compartidos
            val itemsShared = realm?.where(ItemShared::class.java)
                ?.equalTo("customer_id", clienteId)
                ?.equalTo("selected", true)
                ?.equalTo("state", "1")
                ?.findAll()
                
            // Calcular totales si hay ítems
            itemsShared?.forEach { item ->
                // Obtener producto
                val producto = realm?.where(Productos::class.java)
                    ?.equalTo("id", item.product_id)
                    ?.findFirst()
                
                producto?.let { prod ->
                    // Obtener valores
                    val cantidad = item.amount?.toDoubleOrNull() ?: 0.0
                    val precio = item.price?.toDoubleOrNull() ?: 0.0
                    val tipoProducto = prod.product_type_id
                    
                    // Calcular subtotales según tipo de producto
                    val subtotalItem = cantidad * precio
                    
                    when (tipoProducto) {
                        "1" -> { // Gravado
                            subtotalGravado += subtotalItem
                            subtotalTaxed += subtotalItem / 1.13
                            impuestos += subtotalItem - (subtotalItem / 1.13)
                        }
                        "2" -> { // Exento
                            subtotalExento += subtotalItem
                            subtotalExempt += subtotalItem
                        }
                    }
                    
                    // Calcular descuento
                    val descuentoItem = item.discount?.toDoubleOrNull() ?: 0.0
                    descuentoTotal += descuentoItem
                    
                    // Guardar línea de recibo
                    guardarRecibosItems(item)
                }
            }
            
            // Calcular totales finales
            subtotal = subtotalTaxed + subtotalExempt
            total = subtotalGravado + subtotalExento - descuentoTotal
            
            Log.d(TAG, "Totales calculados - Subtotal: $subtotal, Impuestos: $impuestos, Total: $total")
        } catch (e: Exception) {
            Log.e(TAG, "Error al calcular totales: ${e.message}", e)
        }
    }
    
    /**
     * Guarda los ítems del recibo
     * 
     * @param item Item compartido a guardar
     */
    private fun guardarRecibosItems(item: ItemShared) {
        try {
            // Obtener producto
            val producto = realm?.where(Productos::class.java)
                ?.equalTo("id", item.product_id)
                ?.findFirst()
            
            val nombreProducto = producto?.description ?: ""
            val tipoProducto = producto?.product_type_id ?: ""
            
            // Crear entrada en RecibosItems
            realm?.executeTransaction { r ->
                val reciboItem = r.createObject(RecibosItems::class.java, UUID.randomUUID().toString())
                
                // Configurar propiedades
                reciboItem.apply {
                    receipt_id = reciboId
                    product_id = item.product_id
                    product_name = nombreProducto
                    product_type_id = tipoProducto
                    amount = item.amount
                    price = item.price
                    discount = item.discount
                    state = "1"
                }
                
                // Crear línea de distribución si es necesario
                if (item.distribution_id?.isNotEmpty() == true) {
                    val lineItem = r.createObject(RecibosDistLineItems::class.java, UUID.randomUUID().toString())
                    
                    lineItem.apply {
                        receipt_id = reciboId
                        receipt_item_id = reciboItem.id
                        distribution_id = item.distribution_id
                        product_id = item.product_id
                        item_id = item.id
                        state = "1"
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar ítem de recibo: ${e.message}", e)
        }
    }
    
    /**
     * Guarda los impuestos asociados al recibo
     * 
     * @param realm Instancia de Realm
     * @param reciboId ID del recibo
     */
    private fun guardarImpuestos(realm: Realm, reciboId: String) {
        try {
            // Solo guardar si hay impuestos
            if (impuestos > 0) {
                val impuesto = realm.createObject(RecibosTaxes::class.java, UUID.randomUUID().toString())
                
                impuesto.apply {
                    receipt_id = reciboId
                    name = "Impuesto de venta"
                    value = impuestos.toString()
                    created_at = fechaActual
                    updated_at = fechaActual
                    state = "1"
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar impuestos: ${e.message}", e)
        }
    }
    
    /**
     * Actualiza el estado de los ítems compartidos utilizados en el recibo
     * 
     * @param realm Instancia de Realm
     */
    private fun actualizarEstadoItems(realm: Realm) {
        try {
            // Buscar ítems a actualizar
            val items = realm.where(ItemShared::class.java)
                .equalTo("customer_id", clienteId)
                .equalTo("selected", true)
                .equalTo("state", "1")
                .findAll()
            
            // Actualizar estado a "2" (usado)
            items.forEach { item ->
                item.state = "2"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar estado de ítems: ${e.message}", e)
        }
    }
    
    /**
     * Actualiza el día de trabajo con la venta realizada
     * 
     * @param realm Instancia de Realm
     * @param fecha Fecha actual
     * @param tipoRecibo Tipo de recibo (contado, crédito)
     */
    private fun actualizarDiaTrabajo(realm: Realm, fecha: String, tipoRecibo: String) {
        try {
            // Obtener fecha en formato yyyy-MM-dd
            val fechaSolo = fecha.split(" ")[0]
            
            // Buscar día de trabajo existente
            var diaTrabajo = realm.where(DiaTrabajo::class.java)
                .equalTo("date", fechaSolo)
                .findFirst()
            
            // Crear día de trabajo si no existe
            if (diaTrabajo == null) {
                diaTrabajo = realm.createObject(DiaTrabajo::class.java, UUID.randomUUID().toString())
                diaTrabajo.date = fechaSolo
                diaTrabajo.created_at = fecha
                diaTrabajo.updated_at = fecha
                
                // Inicializar valores
                diaTrabajo.apply {
                    sales_cash = "0"
                    sales_cash_count = "0"
                    sales_credit = "0"
                    sales_credit_count = "0"
                    receipts_cash = "0"
                    receipts_cash_count = "0"
                    receipts_credit = "0"
                    receipts_credit_count = "0"
                }
            }
            
            // Actualizar valores según tipo de recibo
            when (tipoRecibo) {
                TIPO_RECIBO_CONTADO -> {
                    val contadoValor = diaTrabajo.receipts_cash?.toDoubleOrNull() ?: 0.0
                    val contadoContador = diaTrabajo.receipts_cash_count?.toIntOrNull() ?: 0
                    
                    diaTrabajo.receipts_cash = (contadoValor + total).toString()
                    diaTrabajo.receipts_cash_count = (contadoContador + 1).toString()
                }
                TIPO_RECIBO_CREDITO -> {
                    val creditoValor = diaTrabajo.receipts_credit?.toDoubleOrNull() ?: 0.0
                    val creditoContador = diaTrabajo.receipts_credit_count?.toIntOrNull() ?: 0
                    
                    diaTrabajo.receipts_credit = (creditoValor + total).toString()
                    diaTrabajo.receipts_credit_count = (creditoContador + 1).toString()
                }
            }
            
            // Actualizar fecha de modificación
            diaTrabajo.updated_at = fecha
            
            Log.d(TAG, "Día de trabajo actualizado para fecha: $fechaSolo")
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar día de trabajo: ${e.message}", e)
        }
    }
    
    /**
     * Limpia los ítems compartidos no utilizados
     */
    private fun limpiarItemsCompartidos() {
        try {
            realm?.executeTransaction { r ->
                // Buscar ítems a limpiar (seleccionados pero no utilizados)
                val items = r.where(ItemShared::class.java)
                    .equalTo("customer_id", clienteId)
                    .equalTo("selected", true)
                    .equalTo("state", "1")
                    .findAll()
                
                // Eliminar ítems
                items.deleteAllFromRealm()
                
                Log.d(TAG, "Items compartidos no utilizados eliminados")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al limpiar ítems compartidos: ${e.message}", e)
        }
    }
    
    /**
     * Obtiene la numeración para el recibo
     * 
     * @return Número de recibo
     */
    private fun obtenerNumeracion(): String {
        var numeracion = "1"
        
        try {
            // Obtener configuración del sistema
            val sysconf = realm?.where(Sysconf::class.java)?.findFirst()
            
            // Obtener último número de recibo
            val ultimoNumero = sysconf?.receipts_numeration?.toIntOrNull() ?: 0
            val nuevoNumero = ultimoNumero + 1
            
            // Actualizar numeración en el sistema
            realm?.executeTransaction { r ->
                sysconf?.receipts_numeration = nuevoNumero.toString()
            }
            
            numeracion = nuevoNumero.toString()
            Log.d(TAG, "Nueva numeración de recibo: $numeracion")
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener numeración: ${e.message}", e)
        }
        
        return numeracion
    }
    
    /**
     * Obtiene el nombre del método de pago según su ID
     * 
     * @param id ID del método de pago
     * @return Nombre del método de pago o cadena vacía si no se encuentra
     */
    fun obtenerNombreMetodoPago(id: String): String {
        var nombre = ""
        
        try {
            realm = Realm.getDefaultInstance()
            
            // Buscar método de pago por ID
            val metodoPago = realm?.where(PaymentMethods::class.java)
                ?.equalTo("id", id)
                ?.findFirst()
            
            nombre = metodoPago?.name ?: ""
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener nombre de método de pago: ${e.message}", e)
        } finally {
            realm?.close()
        }
        
        return nombre
    }
    
    /**
     * Obtiene el nombre de usuario según su ID
     * 
     * @param id ID del usuario
     * @return Nombre del usuario o cadena vacía si no se encuentra
     */
    fun obtenerNombreUsuario(id: String): String {
        var nombre = ""
        
        try {
            realm = Realm.getDefaultInstance()
            
            // Buscar usuario por ID
            val usuario = realm?.where(Usuarios::class.java)
                ?.equalTo("id", id)
                ?.findFirst()
            
            nombre = usuario?.name ?: ""
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener nombre de usuario: ${e.message}", e)
        } finally {
            realm?.close()
        }
        
        return nombre
    }
} 