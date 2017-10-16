package com.ajdev.aroundme;

import android.app.Application;

import com.ajdev.aroundme.dependencies.Components;
import com.ajdev.aroundme.dependencies.DIModules;
import com.ajdev.aroundme.dependencies.DaggerComponents;

import dagger.Component;
import dagger.internal.DaggerCollections;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Akshay.Jayakumar on 10/13/2017.
 *
 * This is the base application class for this App.
 * This class will be defined in the manifestation file
 * as the application.
 *
 * This class will contain initializations for components such as
 * Realm, Dagger Dependency injections etc. and controls application
 * configurations/ behaviours.
 */

public class BaseApplication extends Application {

    // Component is used by Dagger. It contains modules that provide dependencies.
    private Components components;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize realm database
        initRealm();

        // Initializing dependency injection
        components = DaggerComponents.builder().dIModules(new DIModules(this)).build();

    }

    private void initRealm(){
        /**
         * Initialise Realm database instance for this application
         * Should only be done once when the application starts.
         *
         * Configuration specified below will delete the tables when ever
         * there is a structure change.
         *
         * TODO: Can configure to encrypt data using encryptionKey(byte[] key)
         */
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    public Components getDependencyComponents(){
        return components;
    }
}
