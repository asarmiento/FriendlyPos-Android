package com.friendlysystemgroup.friendlypos.application

import android.app.Application
import com.friendlypos.util.AppContextProvider
import io.realm.Realm
import io.realm.RealmConfiguration

class FriendlyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Inicializar AppContextProvider para reemplazar SyncObjectServerFacade
        AppContextProvider.init(this)
        
        Realm.init(this)

        val config = RealmConfiguration.Builder()
            .name("app.realm")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)
    }
}