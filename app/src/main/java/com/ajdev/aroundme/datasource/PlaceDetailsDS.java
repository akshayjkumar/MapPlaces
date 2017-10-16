package com.ajdev.aroundme.datasource;

import android.location.Location;
import android.util.Log;

import com.ajdev.aroundme.BuildConfig;
import com.ajdev.aroundme.dao.NearByPlacesDAO;
import com.ajdev.aroundme.dao.PlaceDetailsDAO;
import com.ajdev.aroundme.model.NearByPlaces;
import com.ajdev.aroundme.model.PlaceDetails;
import com.ajdev.aroundme.network.ConnectionAPIInterface;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by Akshay.Jayakumar on 10/12/2017.
 *
 * Data source for Place detailed information. Detailed information for a given place is fetched from
 * Google Places API via retrofit network interface. Also this data source serves
 * information about places persisted in the Realm Database.
 *
 * This data source class unifies data retrieving and handling using ReactiveX pattern.
 * When data is requested by the associated presenter , this class provides data either
 * by pulling from the server (Retrofit interface) or fetching from the persisted data (Realm).
 */

public class PlaceDetailsDS {
    // Retrofit Connection API interface
    private ConnectionAPIInterface connectionAPIInterface;

    // Data access object for NearByPlaces
    private PlaceDetailsDAO placeDetailsDAO;

    // Hold the instance of a subscriber.
    private Subscription placeDetailsSubscription;

    /**
     * Subject in ReactiveX is the one that can act as Observer and as Observable
     * PublishSubject - Will emit to the observable all subsequent items at the time
     * of Subscription.
     */
    private PublishSubject<PlaceDetails> placeDetailsPublishSubject;

    public PlaceDetailsDS(ConnectionAPIInterface connectionAPIInterface,
                          PlaceDetailsDAO placeDetailsDAO){
        this.connectionAPIInterface = connectionAPIInterface;
        this.placeDetailsDAO = placeDetailsDAO;
        this.placeDetailsPublishSubject = PublishSubject.create();
    }

    /**
     * Returns the subject when requested by the associated presenter.
     * serializing the observable will force its emission and notification
     * to be serialized which otherwise tends to poorly behave by making inappropriate
     * calls to the its emission (onNext) and notification (onComplete / onError)
     * @return
     */
    public Observable<PlaceDetails> getPlaceDetailsSubject() {
        return placeDetailsPublishSubject.asObservable().serialize();
    }

    /**
     * Get detailed place information. Below implementation will fetch information from
     * both server and Realm
     */
    public void getPlacesDetails(String placeID){
        /**
         * If subscription is already active, then Un subscribe  it.
         */
        if(placeDetailsSubscription != null && !placeDetailsSubscription.isUnsubscribed())
            placeDetailsSubscription.unsubscribe();

        placeDetailsSubscription = Observable.concat(
                fetchPlaceDetailsFromRealm(),
                fetchPlaceDetailsFromServer(placeID))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                /**
                 * TakeFirst will emit only when the condition is satisfied.
                 * In this case, emit item from the source observable only when
                 * the list is not null or when it has at least one element.
                 */
                .takeFirst(new Func1<PlaceDetails, Boolean>() {
                    @Override
                    public Boolean call(PlaceDetails placeDetails) {
                        return (placeDetails != null);
                    }
                }).subscribe(new Action1<PlaceDetails>() {
                    @Override
                    public void call(PlaceDetails placeDetails) {
                        PlaceDetailsDS.this.placeDetailsPublishSubject.onNext(placeDetails);
                    }
                },new Action1<Throwable>() {
                    @Override
                    public void call(Throwable error) {
                        PlaceDetailsDS.this.placeDetailsPublishSubject.onError(error);
                        Log.e("@@@@@@@@@@@@@@@@","@@@@@@@@@@@@@@@@@ Place details DS " + error.getLocalizedMessage());
                    }
                });

    }

    /**
     * Retrofit call to get near by places from Google Map APIs
     * getPlaceDetails(String, String) is the API endpoint
     * as defined in the ConnectionAPI Interface.
     */
    private Observable<PlaceDetails> fetchPlaceDetailsFromServer(String placeID) {
        return connectionAPIInterface.getPlaceDetails(placeID,
                BuildConfig.MAPS_API_KEY).map(new Func1<PlaceDetails, PlaceDetails>() {
            @Override
            public PlaceDetails call(PlaceDetails placeDetails) {
                return placeDetails;
            }
        });

    }

    /**
     * Retrieve Place detailed information from the Realm table.
     */
    private Observable<PlaceDetails> fetchPlaceDetailsFromRealm() {
        return Observable.empty();
    }

    /**
     * Flush all data regarding Place Details from realm DB
     */
    public void wipeDateFromRealm() {
        placeDetailsDAO.deleteFromDB();
    }


}
