package com.friendlysystemgroup.friendlypos.base

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.friendlysystemgroup.friendlypos.application.util.Configuracion
import com.friendlysystemgroup.friendlypos.application.util.Functions
import com.friendlysystemgroup.friendlypos.application.util.IpConfig
import com.friendlysystemgroup.friendlypos.application.util.PrinterServiceImpl
import com.friendlysystemgroup.friendlypos.application.util.Session
import com.friendlysystemgroup.friendlypos.application.util.Sincronizacion
import com.friendlysystemgroup.friendlypos.principal.api.ApiApp
import com.friendlysystemgroup.friendlypos.principal.api.ApiService
import com.friendlysystemgroup.friendlypos.principal.modelo.Sysconf
import io.realm.Realm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

/**
 * Clase base para los managers de la aplicación
 * Proporciona funcionalidades comunes como manejo de API, configuración y registro
 */
abstract class BaseManager {
    // Variables de API y configuración
    private var apiService: ApiService? = null
    private var configuracion: Configuracion? = null
    
    // Variables de impresión y sincronización
    private var printerService: PrinterServiceImpl? = null
    private var sincronizacion: Sincronizacion? = null
    
    // Constantes para los mensajes de error
    companion object {
        private const val TAG = "BaseManager"
        private const val ERROR_NO_INTERNET = "Sin conexión a internet"
        private const val ERROR_API_FAILURE = "Error en la comunicación con el servidor"
    }
    
    /**
     * Inicializa los servicios básicos
     * 
     * @param context Contexto de la aplicación
     */
    fun initBaseServices(context: Context) {
        try {
            configuracion = Configuracion(context)
            apiService = ApiApp.getInstanceApiService()
            printerService = PrinterServiceImpl(context)
            sincronizacion = Sincronizacion()
        } catch (e: Exception) {
            Log.e(TAG, "Error inicializando servicios: ${e.message}", e)
        }
    }
    
    /**
     * Obtiene la instancia de ApiService
     * 
     * @return ApiService o null si no está inicializado
     */
    fun getApiService(): ApiService? = apiService
    
    /**
     * Obtiene la instancia de configuración
     * 
     * @return Configuracion o null si no está inicializado
     */
    fun getConfiguracion(): Configuracion? = configuracion
    
    /**
     * Obtiene la instancia de PrinterServiceImpl
     * 
     * @return PrinterServiceImpl o null si no está inicializado
     */
    fun getPrinterService(): PrinterServiceImpl? = printerService
    
    /**
     * Obtiene la instancia de sincronización
     * 
     * @return Sincronizacion o null si no está inicializado
     */
    fun getSincronizacion(): Sincronizacion? = sincronizacion
    
    /**
     * Verifica si hay conexión a internet
     * 
     * @param context Contexto de la aplicación
     * @return true si hay conexión, false en caso contrario
     */
    fun isConnected(context: Context): Boolean {
        return try {
            Functions.isConnected(context)
        } catch (e: Exception) {
            Log.e(TAG, "Error al verificar conexión: ${e.message}", e)
            false
        }
    }
    
    /**
     * Obtiene la URL base de configuración
     * 
     * @return URL base o cadena vacía si no está disponible
     */
    fun getBaseUrl(): String {
        return try {
            configuracion?.baseUrl ?: ""
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener URL base: ${e.message}", e)
            ""
        }
    }
    
    /**
     * Obtiene la configuración del servidor
     * 
     * @param context Contexto de la aplicación
     * @param onConfigLoaded Callback para cuando se carga la configuración
     * @param onError Callback para cuando ocurre un error
     */
    fun getServerConfig(
        context: Context,
        onConfigLoaded: (Sysconf) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            if (!isConnected(context)) {
                onError(ERROR_NO_INTERNET)
                return
            }
            
            val apiService = getApiService()
            if (apiService == null) {
                onError("ApiService no inicializado")
                return
            }
            
            val token = "Bearer ${Session.token}"
            val call = apiService.getServerConfig(token)
            
            call.enqueue(object : Callback<Sysconf> {
                override fun onResponse(call: Call<Sysconf>, response: Response<Sysconf>) {
                    if (response.isSuccessful) {
                        response.body()?.let { sysconf ->
                            onConfigLoaded(sysconf)
                        } ?: onError("Respuesta vacía del servidor")
                    } else {
                        onError("Error ${response.code()}: ${response.message()}")
                    }
                }
                
                override fun onFailure(call: Call<Sysconf>, t: Throwable) {
                    Log.e(TAG, "Error en getServerConfig: ${t.message}", t)
                    onError(ERROR_API_FAILURE)
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener configuración: ${e.message}", e)
            onError("Error: ${e.message}")
        }
    }
    
    /**
     * Actualiza la configuración del sistema en la base de datos local
     * 
     * @param sysconf Objeto de configuración del sistema
     */
    fun updateLocalConfig(sysconf: Sysconf) {
        var realm: Realm? = null
        try {
            realm = Realm.getDefaultInstance()
            
            realm.executeTransaction { r ->
                val localSysconf = r.where(Sysconf::class.java).findFirst()
                
                if (localSysconf != null) {
                    // Actualizar configuración existente
                    localSysconf.apply {
                        company_name = sysconf.company_name
                        lema = sysconf.lema
                        legal_id = sysconf.legal_id
                        legal_name = sysconf.legal_name
                        legal_id_masked = sysconf.legal_id_masked
                        sales_numeration = sysconf.sales_numeration
                        quotes_numeration = sysconf.quotes_numeration
                        receipts_numeration = sysconf.receipts_numeration
                        logo = sysconf.logo
                        branch_id = sysconf.branch_id
                        branch_name = sysconf.branch_name
                        branch_code = sysconf.branch_code
                        branch_address = sysconf.branch_address
                        branch_phone = sysconf.branch_phone
                        branch_email = sysconf.branch_email
                    }
                } else {
                    // Crear nueva configuración si no existe
                    r.copyToRealm(sysconf)
                }
            }
            
            Log.d(TAG, "Configuración local actualizada correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar configuración local: ${e.message}", e)
        } finally {
            realm?.close()
        }
    }
    
    /**
     * Muestra un mensaje Toast
     * 
     * @param context Contexto de la aplicación
     * @param message Mensaje a mostrar
     */
    fun showToast(context: Context, message: String) {
        try {
            (context as? Activity)?.runOnUiThread {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            } ?: run {
                Handler().post {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al mostrar Toast: ${e.message}", e)
        }
    }
    
    /**
     * Obtiene la configuración IP
     * 
     * @return IpConfig o null si no está disponible
     */
    fun getIpConfig(): IpConfig? {
        return try {
            configuracion?.ipConfig
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener configuración IP: ${e.message}", e)
            null
        }
    }
    
    /**
     * Obtiene la URL SOAP del servidor
     * 
     * @return URL SOAP o cadena vacía si no está disponible
     */
    fun getSoapUrl(): String {
        return try {
            configuracion?.soapUrl ?: ""
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener URL SOAP: ${e.message}", e)
            ""
        }
    }
    
    /**
     * Formatea un número como moneda
     * 
     * @param value Valor a formatear
     * @return Cadena formateada como moneda
     */
    fun formatCurrency(value: Double): String {
        return try {
            String.format(Locale.getDefault(), "%.2f", value)
        } catch (e: Exception) {
            Log.e(TAG, "Error al formatear moneda: ${e.message}", e)
            "0.00"
        }
    }
} 