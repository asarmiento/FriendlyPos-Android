package com.friendlysystemgroup.friendlypos.application.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Gestor de conexiones Bluetooth para la aplicación
 * 
 * Esta clase centraliza la lógica de gestión de conexiones Bluetooth,
 * separando esta responsabilidad del servicio de impresión.
 */
class BluetoothManager private constructor() {
    
    companion object {
        private const val TAG = "BluetoothManager"
        
        // Singleton instance
        @Volatile
        private var instance: BluetoothManager? = null
        
        /**
         * Obtiene o crea la instancia única del gestor Bluetooth
         */
        fun getInstance(): BluetoothManager {
            return instance ?: synchronized(this) {
                instance ?: BluetoothManager().also { instance = it }
            }
        }
    }
    
    // Adaptador Bluetooth del sistema
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        BluetoothAdapter.getDefaultAdapter()
    }
    
    // Dispositivo actualmente seleccionado para imprimir
    private var selectedDevice: BluetoothDevice? = null
    
    // Dirección MAC del dispositivo seleccionado
    private var deviceMacAddress: String? = null
    
    // Listeners para eventos de Bluetooth
    private val connectionListeners = mutableListOf<BluetoothConnectionListener>()
    
    /**
     * Verifica si el Bluetooth está disponible en el dispositivo
     */
    fun isBluetoothAvailable(): Boolean {
        return bluetoothAdapter != null
    }
    
    /**
     * Verifica si el Bluetooth está habilitado
     */
    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }
    
    /**
     * Obtiene la lista de dispositivos Bluetooth emparejados
     */
    fun getPairedDevices(): Set<BluetoothDevice> {
        return bluetoothAdapter?.bondedDevices ?: emptySet()
    }
    
    /**
     * Configura el dispositivo a usar para imprimir
     */
    fun setSelectedDevice(macAddress: String): Boolean {
        if (macAddress.isEmpty()) {
            Log.e(TAG, "Dirección MAC vacía")
            return false
        }
        
        // Buscar dispositivo por MAC
        val device = findDeviceByMacAddress(macAddress)
        if (device == null) {
            Log.e(TAG, "No se encontró dispositivo con dirección: $macAddress")
            return false
        }
        
        // Guardar referencia
        selectedDevice = device
        deviceMacAddress = macAddress
        
        Log.d(TAG, "Dispositivo seleccionado: ${device.name}")
        return true
    }
    
    /**
     * Obtiene el dispositivo actualmente seleccionado
     */
    fun getSelectedDevice(): BluetoothDevice? {
        return selectedDevice
    }
    
    /**
     * Inicia el servicio de impresora con el dispositivo seleccionado
     */
    fun startPrinterService(context: Context): Boolean {
        if (!isBluetoothEnabled()) {
            Log.e(TAG, "Bluetooth no habilitado")
            return false
        }
        
        if (selectedDevice == null && deviceMacAddress != null) {
            // Intentar recuperar dispositivo si solo tenemos la MAC
            selectedDevice = findDeviceByMacAddress(deviceMacAddress!!)
        }
        
        if (selectedDevice == null) {
            Log.e(TAG, "No hay dispositivo seleccionado")
            return false
        }
        
        // Crear intent para iniciar el servicio
        val serviceIntent = Intent(context, PrinterService::class.java)
        serviceIntent.putExtra(PrinterService.BT_DEVICE, deviceMacAddress)
        context.startService(serviceIntent)
        
        Log.d(TAG, "Servicio iniciado para dispositivo: ${selectedDevice?.name}")
        return true
    }
    
    /**
     * Detiene el servicio de impresora
     */
    fun stopPrinterService(context: Context) {
        val serviceIntent = Intent(context, PrinterService::class.java)
        context.stopService(serviceIntent)
        Log.d(TAG, "Servicio de impresora detenido")
    }
    
    /**
     * Busca un dispositivo por su dirección MAC
     */
    private fun findDeviceByMacAddress(macAddress: String): BluetoothDevice? {
        val pairedDevices = getPairedDevices()
        return pairedDevices.find { it.address == macAddress }
    }
    
    /**
     * Agrega un listener para eventos de conexión Bluetooth
     */
    fun addConnectionListener(listener: BluetoothConnectionListener) {
        if (!connectionListeners.contains(listener)) {
            connectionListeners.add(listener)
        }
    }
    
    /**
     * Elimina un listener de eventos de conexión Bluetooth
     */
    fun removeConnectionListener(listener: BluetoothConnectionListener) {
        connectionListeners.remove(listener)
    }
    
    /**
     * Notifica a los listeners sobre un cambio en el estado de la conexión
     */
    fun notifyConnectionChanged(isConnected: Boolean) {
        connectionListeners.forEach { it.onConnectionStatusChanged(isConnected) }
    }
    
    /**
     * Interfaz para escuchar eventos de conexión Bluetooth
     */
    interface BluetoothConnectionListener {
        fun onConnectionStatusChanged(isConnected: Boolean)
    }
} 