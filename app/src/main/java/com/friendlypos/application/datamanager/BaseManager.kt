package com.friendlysystemgroup.friendlypos.application.datamanager

import android.content.Context
import com.friendlysystemgroup.friendlypos.login.util.Properties
import com.google.gson.ExclusionStrategy
import java.io.IOException
import java.util.concurrent.TimeUnit

class BaseManager(activity: Activity) {
    private val activity: Activity = activity
    private val mContext: Context = activity

    companion object {
        @JvmStatic
        var api: RequestInterface? = null
            get() {
                properties =
                    Properties(SyncObjectServerFacade.getApplicationContext())
                nombreURL =
                    "http://" + properties!!.urlWebsrv

                val gson: Gson = GsonBuilder()
                    .setExclusionStrategies(object : ExclusionStrategy() {
                        override fun shouldSkipField(f: FieldAttributes): Boolean {
                            return f.getDeclaringClass().equals(RealmObject::class.java)
                        }

                        override fun shouldSkipClass(clazz: Class<*>?): Boolean {
                            return false
                        }
                    })
                    .create()

                val httpClient: OkHttpClient = Builder()
                    .readTimeout(20, TimeUnit.SECONDS)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .addInterceptor(object : Interceptor() {
                        @Throws(IOException::class)
                        override fun intercept(chain: Chain): okhttp3.Response {
                            val ongoing: Request.Builder = chain.request().newBuilder()
                            ongoing.addHeader("Accept", "application/json")
                            return chain.proceed(ongoing.build())
                        }
                    })
                    .build()

                val retrofit: Retrofit = Builder()
                    .baseUrl(nombreURL)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

                field = retrofit.create(RequestInterface::class.java)


                return field
            }
            private set
        var properties: Properties? = null
        var nombreURL: String = "friendlyaccount.com"
    }
}