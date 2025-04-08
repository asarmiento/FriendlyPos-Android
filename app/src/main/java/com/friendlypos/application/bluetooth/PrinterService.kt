package com.friendlysystemgroup.friendlypos.application.bluetooth

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.friendlysystemgroup.friendlypos.principal.activity.MenuPrincipal
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

/**
 * Servicio para manejar la comunicación con la impresora Bluetooth
 */
class PrinterService : Service() {
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mConnectThread: ConnectThread? = null
    private var mMacAddress: String? = null
    private var mContext: Context? = null
    private var onSocket: BluetoothSocket? = null
    
    override fun onCreate() {
        Log.i(TAG, "Service started")
        registerReceiver(onBroadcast, IntentFilter(BROADCAST_CLASS))
        
        mContext = this
        mHandler2 = Handler(Looper.getMainLooper())
        
        super.onCreate()
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "ONBIND")
        return mBinder
    }

    inner class LocalBinder : Binder() {
        val service: PrinterService
            get() = this@PrinterService
    }

    private val mBinder: IBinder = LocalBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Onstart Command")
        
        // Verificar que la intent no sea nula
        if (intent == null) {
            Log.e(TAG, "Intent nula, deteniendo servicio")
            stopSelf()
            return START_NOT_STICKY
        }
        
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val chosenDevice = intent.getStringExtra(BT_DEVICE)
        
        if (mBluetoothAdapter != null && !chosenDevice.isNullOrEmpty()) {
            val pairedDevices = mBluetoothAdapter?.bondedDevices ?: emptySet()
            
            if (pairedDevices.isNotEmpty()) {
                for (d in pairedDevices) {
                    if (d.address == chosenDevice) device = d
                }
            }
            
            if (device == null) {
                Log.d(TAG, "No se encontró el dispositivo, deteniendo servicio")
                return START_NOT_STICKY
            }
            
            deviceName = device?.name
            mMacAddress = device?.address
            
            if (!mMacAddress.isNullOrEmpty()) {
                Log.d(TAG, "Conectando a: $deviceName")
                connectToDevice(mMacAddress)
            } else {
                Log.d(TAG, "No hay dirección MAC, deteniendo servicio")
                stopSelf()
                return START_NOT_STICKY
            }
        } else {
            Log.d(TAG, "No hay adaptador Bluetooth o dispositivo elegido")
            stopSelf()
            return START_NOT_STICKY
        }

        return START_STICKY
    }

    @Synchronized
    private fun connectToDevice(macAddress: String?) {
        if (macAddress.isNullOrEmpty() || mBluetoothAdapter == null) {
            Log.e(TAG, "Dirección MAC nula o adaptador Bluetooth no disponible")
            return
        }
        
        Log.d(TAG, "Conectando... ")
        val device = mBluetoothAdapter?.getRemoteDevice(macAddress)
        
        // Cancela el hilo de conexión si está en progreso
        if (mState == STATE_CONNECTING) {
            mConnectThread?.cancel()
            mConnectThread = null
        }

        // Cancela cualquier hilo que esté ejecutando una conexión
        mConnectedThread?.cancel()
        mConnectedThread = null
        
        // Inicia nuevo hilo de conexión
        device?.let {
            mConnectThread = ConnectThread(it)
            mConnectThread?.start()
            setState(STATE_CONNECTING)
        }
    }

    private fun setState(state: Int) {
        mState = state
    }

    @Synchronized
    fun stop() {
        setState(STATE_NONE)
        mHandler2?.removeCallbacks(reconnect)

        mConnectThread?.cancel()
        mConnectThread = null

        mConnectedThread?.cancel()
        mConnectedThread = null
        
        mBluetoothAdapter?.cancelDiscovery()
        
        stopSelf()
    }

    override fun stopService(name: Intent): Boolean {
        setState(STATE_NONE)
        
        mConnectThread?.cancel()
        mConnectThread = null

        mConnectedThread?.cancel()
        mConnectedThread = null
        
        mBluetoothAdapter?.cancelDiscovery()
        
        return super.stopService(name)
    }

    private fun connectionFailed() {
        Log.d(TAG, "Conexión fallida")
        if (mState == STATE_NONE) return
        reconnect()
    }

    fun connectionLost() {
        Log.d(TAG, "Conexión perdida")
        
        mConnectThread?.cancel()
        mConnectThread = null

        mConnectedThread?.cancel()
        mConnectedThread = null

        try {
            onSocket?.close()
            onSocket = null
        } catch (e: Exception) {
            Log.e(TAG, "Error al cerrar socket", e)
        }
        
        reconnect()
    }

    private val reconnect: Runnable = Runnable { 
        connectToDevice(mMacAddress) 
    }

    fun reconnect() {
        stopSelf()
        // Si se desea reconectar automáticamente después de un tiempo:
        // mHandler2?.postDelayed(reconnect, 5000) // 5 segundos
    }

    @Synchronized
    private fun connected(mmSocket: BluetoothSocket?, mmDevice: BluetoothDevice) {
        if (mmSocket == null) {
            Log.e(TAG, "Socket nulo en connected()")
            connectionFailed()
            return
        }
        
        Log.d(TAG, "Conectado")
        
        // Cancela el hilo que completó la conexión
        mConnectThread?.cancel()
        mConnectThread = null

        // Cancela cualquier hilo que esté ejecutando una conexión
        mConnectedThread?.cancel()
        mConnectedThread = null

        // Inicia el hilo para manejar la conexión
        mConnectedThread = ConnectedThread(mmSocket)
        mConnectedThread?.start()
        onSocket = mmSocket
        setState(STATE_CONNECTED)
    }

    private inner class ConnectThread(private val mmDevice: BluetoothDevice) : Thread() {
        private var mmSocket: BluetoothSocket? = null

        override fun run() {
            name = "ConnectThread"
            mBluetoothAdapter?.cancelDiscovery()
            
            try {
                mmSocket = device?.createRfcommSocketToServiceRecord(UUID_SPP)
                mmSocket?.connect()
            } catch (e: IOException) {
                Log.e(TAG, "Error al conectar socket", e)
                try {
                    mmSocket?.close()
                } catch (e1: IOException) {
                    Log.e(TAG, "Error al cerrar socket después de fallo de conexión", e1)
                }
                connectionFailed()
                return
            }
            
            synchronized(this@PrinterService) {
                mConnectThread = null
            }
            
            connected(mmSocket, mmDevice)
        }

        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Error al cerrar socket de conexión", e)
            }
        }

        fun sendData(data: String) {
            mConnectedThread?.write(data.toByteArray())
        }
    }

    private inner class ConnectedThread(socket: BluetoothSocket) : Thread() {
        private val mmOutStream: OutputStream?
        private val mmInStream: InputStream?

        init {
            var tmpOut: OutputStream? = null
            var tmpIn: InputStream? = null
            
            try {
                tmpOut = socket.outputStream
                tmpIn = socket.inputStream
            } catch (e: IOException) {
                Log.e(TAG, "Error al crear streams temporales", e)
            }
            
            mmOutStream = tmpOut
            mmInStream = tmpIn
        }

        override fun run() {
            Log.i(TAG, "Iniciando hilo conectado")
            val buffer = ByteArray(1024)
            var bytes: Int

            // Escuchar el InputStream mientras esté conectado
            while (true) {
                try {
                    // Leer del InputStream
                    mmInStream?.let {
                        bytes = it.read(buffer)
                        // Enviar los bytes obtenidos a la actividad UI
                        mHandler?.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                            ?.sendToTarget()
                    } ?: break
                } catch (e: IOException) {
                    Log.e(TAG, "Desconectado", e)
                    connectionLost()
                    break
                }
            }
        }

        fun write(buffer: ByteArray?) {
            if (buffer == null) {
                Log.e(TAG, "Intento de escritura con buffer nulo")
                return
            }
            
            try {
                mmOutStream?.write(buffer)
            } catch (e: IOException) {
                Log.e(TAG, "Excepción durante la escritura", e)
            }
        }

        fun cancel() {
            try {
                setState(STATE_NONE)
            } catch (e: Exception) {
                Log.e(TAG, "Error al cancelar ConnectedThread", e)
            }
        }
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(onBroadcast)
        } catch (e: Exception) {
            Log.e(TAG, "Error al desregistrar receptor", e)
        }

        setState(STATE_NONE)
        stop()
        super.onDestroy()
    }

    // Receptor para la comunicación Servicio->UI
    private val onBroadcast: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("$BROADCAST_CLASS$TO_PRINT")) {
                if (mState != STATE_CONNECTED) {
                    Log.d(TAG, "No conectado, ignorando mensaje para imprimir")
                    return
                }

                val extras = intent.extras ?: return
                val data = extras.getString("$BROADCAST_CLASS$TO_PRINT") ?: return

                if (data == "true") {
                    try {
                        val bill = extras.getString("bill_to_print")
                        if (!bill.isNullOrEmpty()) {
                            mConnectedThread?.write(bill.toByteArray())
                        } else {
                            Log.d(TAG, "Factura para imprimir está vacía")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error al procesar factura para imprimir", e)
                    }
                }
            }
        }
    }

    companion object {
        const val CLASS_NAME = "com.friendlypos.application.bluetooth.PrinterService"
        const val BROADCAST_CLASS = "com.friendlypos.printbill"
        private const val TO_PRINT = "TO_PRINT"
        private val UUID_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        
        // Constantes de estado
        const val STATE_NONE = 0 // No haciendo nada
        const val STATE_LISTEN = 1 // Escuchando conexiones entrantes
        const val STATE_CONNECTING = 2 // Iniciando una conexión saliente
        const val STATE_CONNECTED = 3 // Conectado a un dispositivo remoto
        
        // Tipos de mensajes
        const val MESSAGE_READ = 1
        
        // Constante para el dispositivo Bluetooth
        const val BT_DEVICE = "btdevice"

        private const val TAG = "PrinterService"
        
        // Variables compartidas
        private var mConnectedThread: ConnectedThread? = null
        var mHandler: Handler? = null
        var mHandler2: Handler? = null
        var mState = STATE_NONE
        var deviceName: String? = null
        var device: BluetoothDevice? = null
        private val syncObject = Any()

        /**
         * Detiene el servicio de impresora Bluetooth
         */
        fun stop(context: MenuPrincipal): Boolean {
            if (context.isServiceRunning(CLASS_NAME)) {
                val serviceIntent = Intent(context, PrinterService::class.java)
                context.stopService(serviceIntent)
                return true
            }
            return false
        }

        /**
         * Inicia el servicio de impresora Bluetooth
         */
        fun startRDService(context: Context, prefExternalDisplay: String?) {
            if (prefExternalDisplay.isNullOrEmpty()) {
                Log.d(TAG, "No hay dirección de impresora configurada")
                return
            }

            val serviceIntent = Intent(context, PrinterService::class.java).apply {
                putExtra(BT_DEVICE, prefExternalDisplay)
            }
            context.startService(serviceIntent)
        }

        /**
         * Escribe datos en la impresora conectada
         */
        fun write(out: ByteArray?) {
            if (out == null) {
                Log.e(TAG, "Intento de escritura con datos nulos")
                return
            }
            
            // Crea un objeto temporal
            val r: ConnectedThread?
            
            // Sincroniza una copia del ConnectedThread
            synchronized(syncObject) {
                if (mState != STATE_CONNECTED) {
                    Log.d(TAG, "No conectado, no se puede escribir")
                    return
                }
                r = mConnectedThread
            }
            
            // Realiza la escritura no sincronizada
            r?.write(out)
        }
    }
}