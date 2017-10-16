package com.ajdev.aroundme.dao;

import com.ajdev.aroundme.model.NearByPlaces;

/**
 * Created by Akshay.Jayakumar on 10/14/2017.
 *
 * Data access object for detailed place information fetched from Google Places API.
 */

public class PlaceDetailsDAO extends BaseDAO{

    /**
     * Insert new Place Details into the Db or
     * update if data already exist for the location
     */
    public void insertOrUpdate(NearByPlaces nearByPlaces){

    }

    /**
     * Fetch from the DB, stored  place details.
     * @return
     */
    public NearByPlaces getNearByPlaces(){
        return null;
    }

    /**
     * Remove data from Realm Database
     */
    @Override
    public void deleteFromDB() {

    }
}
