package com.friendlypos.login.controller;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.friendlypos.login.modelo.User;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by juandiegoGL on 4/15/17.
 */

public class RealmController {

    public static final String TAG = RealmController.class.getSimpleName();

    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {
        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {
        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {
        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {
        return instance;
    }

    public Realm getRealm() {
        return realm;
    }

    public void refresh() {
        realm.refresh();
    }

    public RealmResults<User> getUsers() {
        return realm.where(User.class).findAll();
    }


    public RealmResults<User> getUsersByParam(String param) {
        return realm.where(User.class).equalTo(param, true).findAll();
    }

    public User getUserByName(String mac_address) {
        return realm.where(User.class).equalTo("name", mac_address).findFirst();
    }

    public void removeUserByName(final String name) {
        realm.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {
                RealmResults<User> rows = realm.where(User.class).equalTo("name", name).findAll();
                rows.deleteAllFromRealm();
            }
        });
    }

    public void saveUser(String name, String pass) {
        realm.beginTransaction();
        User user = realm.createObject(User.class, name);
        user.setPassword(pass);
        realm.commitTransaction();
        Log.d(TAG, "password updated " + user.toString());
    }

    public void updatePass(String name, String pass) {
        realm.beginTransaction();
        User user = getUserByName(name);
        if (user != null) {
            user.setPassword(pass);
            Log.d(TAG, "password updated " + user.getPassword());
            realm.commitTransaction();
        }
        else {
            realm.cancelTransaction();
        }
    }

}