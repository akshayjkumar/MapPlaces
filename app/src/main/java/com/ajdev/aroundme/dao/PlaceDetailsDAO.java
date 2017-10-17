package com.ajdev.aroundme.dao;

import com.ajdev.aroundme.model.NearByPlaces;
import com.ajdev.aroundme.model.PlaceDetails;

import io.realm.Realm;

/**
 * Created by Akshay.Jayakumar on 10/14/2017.
 *
 * Data access object for detailed place information fetched from Google Places API.
 *
 * This is not implemented as part of this project, as persisting data is not required
 * primarily because, data involved are search results from Google Places APIs.
 */

public class PlaceDetailsDAO extends BaseDAO{

    /**
     * Insert new PlaceDetails data into the Db or update if data already exists.
     * If update policy is not required, flush the realm before adding fresh data.
     */
    public void insertOrUpdate(final PlaceDetails placeDetails){
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
                realm.insertOrUpdate(placeDetails);
            }
        });

        // Close realm instance after use.
        realm.close();
    }

    /**
     * Fetch from the DB, stored  place details. However
     * this is not used in the project. This is to demonstrate Realm implementation.
     * @return
     */
    public PlaceDetails getPlaceDetails(){
        /**
         * Fetch PlaceDetails information from Realm
         **/
        // Get the realm instance
        Realm realm = Realm.getDefaultInstance();
        // Query data for PlaceDetails from realm
        PlaceDetails placeDetails = realm.copyFromRealm(realm.where(PlaceDetails.class).findFirst());
        // Close realm instance after use
        realm.close();
        // If no data has been retrieved, returns null.
        return placeDetails;
    }

    /**
     * Remove data from Realm Database
     */
    @Override
    public void deleteFromDB() {
        /**
         * Delete all PlaceDetails information from Realm. However
         * this is not used in the project. This is to demonstrate Realm implementation.
         **/
        // Get the realm instance
        Realm realm = Realm.getDefaultInstance();
        // Delete all data from realm. Perform this action in a transaction
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(PlaceDetails.class);
            }
        });
        // Close realm instance after use
        realm.close();
    }
}
