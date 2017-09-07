package com.friendlypos.application;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by juandiegoGL on 4/15/17.
 */

public class FriendlyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
            .Builder()
            .deleteRealmIfMigrationNeeded()
            .build();
        Realm.setDefaultConfiguration(config);
    }
}