package com.friendlypos.util

import android.content.Context
import androidx.fragment.app.Fragment

/**
 * Esta clase reemplaza la funcionalidad de SyncObjectServerFacade.getApplicationContext()
 * que ya no está disponible en versiones recientes de Realm
 */
object AppContextProvider {
    private var applicationContext: Context? = null
    
    fun init(context: Context) {
        applicationContext = context.applicationContext
    }
    
    fun getApplicationContext(): Context {
        return applicationContext ?: throw IllegalStateException("AppContextProvider no ha sido inicializado")
    }
}

/**
 * Extensiones útiles para Fragments
 */
fun Fragment.requireApplicationContext(): Context {
    return AppContextProvider.getApplicationContext()
} 