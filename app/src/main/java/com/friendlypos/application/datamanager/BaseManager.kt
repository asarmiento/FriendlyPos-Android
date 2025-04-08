package com.friendlysystemgroup.friendlypos.application.datamanager

import android.app.Activity
import android.content.Context
import com.friendlysystemgroup.friendlypos.login.util.Properties
import com.friendlysystemgroup.friendlypos.application.interfaces.RequestInterface
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import io.realm.RealmObject
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Clase base para manejar las conexiones a servicios web
 */
class BaseManager(private val context: Context) {

    companion object {
        private const val DEFAULT_TIMEOUT_SECONDS = 20L
        private const val DEFAULT_BASE_URL = "http://friendlyaccount.com/"
        
        /**
         * Cliente de API inicializado bajo demanda
         */
        val api: RequestInterface by lazy {
            createApiClient(context.applicationContext)
        }
        
        /**
         * Crea y configura el cliente API
         */
        private fun createApiClient(appContext: Context): RequestInterface {
            val properties = Properties(appContext)
            val baseUrl = "http://${properties.urlWebsrv}"
            
            val gson: Gson = createGson()
            val httpClient: OkHttpClient = createHttpClient()
            
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            
            return retrofit.create(RequestInterface::class.java)
        }
        
        /**
         * Crea y configura Gson para excluir propiedades de RealmObject
         */
        private fun createGson(): Gson {
            return GsonBuilder()
                .setExclusionStrategies(object : ExclusionStrategy {
                    override fun shouldSkipField(f: FieldAttributes): Boolean {
                        return f.declaringClass == RealmObject::class.java
                    }
                    
                    override fun shouldSkipClass(clazz: Class<*>?): Boolean {
                        return false
                    }
                })
                .create()
        }
        
        /**
         * Crea y configura el cliente HTTP con timeouts y cabeceras
         */
        private fun createHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .readTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .connectTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val ongoing: Request.Builder = chain.request().newBuilder()
                    ongoing.addHeader("Accept", "application/json")
                    chain.proceed(ongoing.build())
                }
                .build()
        }
    }
    
    /**
     * Constructor alternativo que acepta una Activity
     */
    constructor(activity: Activity) : this(activity.applicationContext)
}