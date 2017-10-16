package com.ajdev.aroundme.network;

import com.ajdev.aroundme.model.NearByPlaces;
import com.ajdev.aroundme.model.PlaceDetails;

import java.util.List;

import retrofit2.http.Query;
import rx.Observable;

import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Akshay.Jayakumar on 10/12/2017.
 *
 * This is a Retrofit API Interface.
 * This interface provides API end-points for fetching NearByPlace
 * information from Google Map APIs.
 *
 */

public interface ConnectionAPIInterface {
    /** Get near by places by search using type (Bus, atm, movie_theater etc.) **/
    @GET("maps/api/place/nearbysearch/json")
    Observable<NearByPlaces> getNearByPlaces(
            @Query("location") String location,
            @Query("radius") String radius,
            @Query("name") String type,
            @Query("key") String key);

    /** Get near by places by search using name of the place or point or establishment **/
    @GET("maps/api/place/nearbysearch/json")
    Observable<NearByPlaces> getNearByPlacesByType(
            @Query("location") String location,
            @Query("radius") String radius,
            @Query("type") String type,
            @Query("key") String key);

    /** Get detailed information about a particular place from Place API **/
    @GET("maps/api/place/details/json")
    Observable<PlaceDetails> getPlaceDetails(
            @Query("placeid") String placeID,
            @Query("key") String key);
}
