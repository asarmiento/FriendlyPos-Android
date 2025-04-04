package com.friendlysystemgroup.friendlypos.application

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class FriendlyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Realm.init(this)

        val config = RealmConfiguration.Builder()
            .name("app.realm")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)
    }
}