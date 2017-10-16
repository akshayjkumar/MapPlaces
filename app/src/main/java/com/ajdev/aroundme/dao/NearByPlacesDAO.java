package com.ajdev.aroundme.dao;

import com.ajdev.aroundme.model.NearByPlaces;

import java.util.List;

/**
 * Created by Akshay.Jayakumar on 10/12/2017.
 *
 * Data access object for Near By Place information fetched from Google Map API.
 */

public class NearByPlacesDAO extends BaseDAO{

    /**
     * Insert new NearByLocation into the Db or
     * update if data already exist for the location
     */
    public void insertOrUpdate(NearByPlaces nearByPlaces){

    }

    /**
     * Fetch from the DB stored near by places.
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
