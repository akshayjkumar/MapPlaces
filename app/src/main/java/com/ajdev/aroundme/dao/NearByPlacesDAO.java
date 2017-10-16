package com.ajdev.aroundme.dao;

import com.ajdev.aroundme.model.NearByPlaces;

import java.util.List;

import io.realm.Realm;

/**
 * Created by Akshay.Jayakumar on 10/12/2017.
 *
 * Data access object for Near By Place information fetched from Google Map API.
 *
 * This is not implemented as part of this project, as persisting data is not required
 * primarily because, data involved are search results from Google Maps APIs.
 *
 */

public class NearByPlacesDAO extends BaseDAO{

    /**
     * Insert new NearByPlaces data into the Db or update if data already exists.
     * If update policy is not required, flush the realm before adding fresh data.
     */
    public void insertOrUpdate(final NearByPlaces nearByPlaces){
        /**
         * This is a the actual implementation of data persistence using Realm. However
         * this is not used in the project. This is to demonstrate Realm implementation.
         *
         **/

        // Get realm instance
        Realm realm = Realm.getDefaultInstance();
        // All DB writes are to be performed in transactions. Transaction is "all or nothing".
        // This helps in data consistency and thread safety. Transaction once successful will
        // commit automatically.
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(nearByPlaces);
            }
        });

        // Close realm instance after use.
        realm.close();
    }

    /**
     * Fetch from the DB, stored NearByPlaces information. However
     * this is not used in the project. This is to demonstrate Realm implementation.
     * @return
     */
    public NearByPlaces getNearByPlaces(){
        /**
         * Fetch NearByPlace information from Realm
         **/
         // Get the realm instance
        Realm realm = Realm.getDefaultInstance();
        // Query data for NearByPlace from realm
        NearByPlaces nearByPlaces = realm.copyFromRealm(realm.where(NearByPlaces.class).findFirst());
        // Close realm instance after use
        realm.close();
        // If no data has been retrieved, returns null.
        return nearByPlaces;
    }

    /**
     * Remove data from Realm Database
     */
    @Override
    public void deleteFromDB() {
        /**
         * Delete all NearByPlace information from Realm. However
         * this is not used in the project. This is to demonstrate Realm implementation.
         **/
        // Get the realm instance
        Realm realm = Realm.getDefaultInstance();
        // Delete all data from realm. Perform this action in a transaction
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(NearByPlaces.class);
            }
        });
        // Close realm instance after use
        realm.close();
    }
}
