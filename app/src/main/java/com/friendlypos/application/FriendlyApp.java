package com.friendlypos.application;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class FriendlyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Configure Realm for the application

        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("app.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

    }
}