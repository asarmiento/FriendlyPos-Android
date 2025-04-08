package com.friendlysystemgroup.friendlypos.application.util

import android.app.Activity
import android.content.Context
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.friendlysystemgroup.friendlypos.Recibos.modelo.receipts
import com.friendlysystemgroup.friendlypos.application.bluetooth.BluetoothManager
import com.friendlysystemgroup.friendlypos.distribucion.modelo.sale
import com.friendlysystemgroup.friendlypos.principal.activity.BluetoothActivity

/**
 * Clase de utilidad para facilitar operaciones de impresión en actividades y fragmentos
 * 
 * Esta clase proporciona métodos convenientes para operaciones comunes relacionadas
 * con la impresión, verificando la disponibilidad de Bluetooth y manejando errores.
 */
object PrintingHelper {
    
    private const val TAG = "PrintingHelper"
    
    // Instancia del administrador de impresión
    private val printingManager = PrintingManager.getInstance()
    
    // Instancia del administrador Bluetooth
    private val bluetoothManager = BluetoothManager.getInstance()
    
    /**
     * Verifica si la impresión está disponible (Bluetooth habilitado y configurado)
     */
    fun isPrintingAvailable(context: Context): Boolean {
        // Verificar si Bluetooth está habilitado
        if (!bluetoothManager.isBluetoothEnabled()) {
            Toast.makeText(
                context, 
                "Bluetooth no habilitado. Active Bluetooth para imprimir.", 
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        
        // Verificar si hay dispositivo seleccionado para imprimir
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val printerAddress = sharedPref.getString("pref_conectar_bluetooth", "")
        
        if (printerAddress.isNullOrEmpty()) {
            Toast.makeText(
                context, 
                "No hay impresora configurada. Configure en Preferencias.", 
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        
        return true
    }
    
    /**
     * Configura la conexión Bluetooth para impresión
     */
    fun setupPrinterConnection(activity: BluetoothActivity): Boolean {
        // Cargar preferencias
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
        val printerEnabled = sharedPref.getBoolean("pref_usar_impresora", true)
        val printerAddress = sharedPref.getString("pref_conectar_bluetooth", "")
        
        if (!printerEnabled || printerAddress.isNullOrEmpty()) {
            return false
        }
        
        // Seleccionar dispositivo
        if (!bluetoothManager.setSelectedDevice(printerAddress)) {
            return false
        }
        
        // Iniciar servicio
        return bluetoothManager.startPrinterService(activity)
    }
    
    /**
     * Muestra diálogo para solicitar número de impresiones y luego imprime factura
     */
    fun promptAndPrintInvoice(
        activity: Activity,
        sale: sale,
        tipoImpresion: Int,
        titulo: String
    ) {
        if (!isPrintingAvailable(activity)) {
            return
        }
        
        val builder = AlertDialog.Builder(activity)
            .setTitle(titulo)
            .setMessage("Escriba el número de impresiones requeridas")
            
        // Crear layout con input
        val dialogLayout = activity.layoutInflater.inflate(
            com.friendlysystemgroup.friendlypos.R.layout.dialog_impresiones, null
        )
        val editText = dialogLayout.findViewById<android.widget.EditText>(
            com.friendlysystemgroup.friendlypos.R.id.et_num_impresiones
        )
        
        builder.setView(dialogLayout)
            .setPositiveButton("Imprimir") { dialog, _ ->
                val cantidad = editText.text.toString().ifEmpty { "1" }
                printingManager.imprimirFactura(sale, activity, tipoImpresion, cantidad)
                Toast.makeText(activity, "Imprimiendo factura...", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    /**
     * Muestra diálogo para solicitar número de impresiones y luego imprime recibo
     */
    fun promptAndPrintReceipt(
        activity: Activity,
        receipt: receipts,
        titulo: String
    ) {
        if (!isPrintingAvailable(activity)) {
            return
        }
        
        val builder = AlertDialog.Builder(activity)
            .setTitle(titulo)
            .setMessage("Escriba el número de impresiones requeridas")
            
        // Crear layout con input
        val dialogLayout = activity.layoutInflater.inflate(
            com.friendlysystemgroup.friendlypos.R.layout.dialog_impresiones, null
        )
        val editText = dialogLayout.findViewById<android.widget.EditText>(
            com.friendlysystemgroup.friendlypos.R.id.et_num_impresiones
        )
        
        builder.setView(dialogLayout)
            .setPositiveButton("Imprimir") { dialog, _ ->
                val cantidad = editText.text.toString().ifEmpty { "1" }
                printingManager.imprimirRecibo(receipt, activity, cantidad)
                Toast.makeText(activity, "Imprimiendo recibo...", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    /**
     * Imprime una liquidación
     */
    fun printLiquidacion(context: Context) {
        if (!isPrintingAvailable(context)) {
            return
        }
        
        printingManager.imprimirLiquidacion(context)
        Toast.makeText(context, "Imprimiendo liquidación...", Toast.LENGTH_SHORT).show()
    }
    
    /**
     * Imprime una devolución
     */
    fun printDevolucion(context: Context) {
        if (!isPrintingAvailable(context)) {
            return
        }
        
        printingManager.imprimirDevolucion(context)
        Toast.makeText(context, "Imprimiendo devolución...", Toast.LENGTH_SHORT).show()
    }
    
    /**
     * Imprime una orden de carga
     */
    fun printOrdenCarga(context: Context) {
        if (!isPrintingAvailable(context)) {
            return
        }
        
        printingManager.imprimirOrdenCarga(context)
        Toast.makeText(context, "Imprimiendo orden de carga...", Toast.LENGTH_SHORT).show()
    }
} 