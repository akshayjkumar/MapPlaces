package com.ajdev.aroundme.datasource;

import android.location.Location;
import android.util.Log;

import com.ajdev.aroundme.BuildConfig;
import com.ajdev.aroundme.dao.NearByPlacesDAO;
import com.ajdev.aroundme.model.NearByPlaces;
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
 * Data source for Near by place information. Near by place information is fetched from
 * Google Maps API via retrofit network interface. Also this data source serves
 * information about nearby places persisted in the Realm Database.
 *
 * This data source class unifies data retrieving and handling using ReactiveX pattern.
 * When data is requested by the associated presenter , this class provides data either
 * by pulling from the server (Retrofit interface) or fetching from the persisted data (Realm).
 */

public class NearByPlacesDS {
    // Retrofit Connection API interface
    private ConnectionAPIInterface connectionAPIInterface;

    // Data access object for NearByPlaces
    private NearByPlacesDAO nearByPlacesDAO;

    // Hold the instance of a subscriber.
    private Subscription nbpListSubscription;

    /**
     * Subject in ReactiveX is the one that can act as Observer and as Observable
     * PublishSubject - Will emit to the observable all subsequent items at the time
     * of Subscription.
     */
    private PublishSubject<NearByPlaces> nbpListPublishSubject;

    public NearByPlacesDS(ConnectionAPIInterface connectionAPIInterface,
                          NearByPlacesDAO nearByPlacesDAO){
        this.connectionAPIInterface = connectionAPIInterface;
        this.nearByPlacesDAO = nearByPlacesDAO;
        this.nbpListPublishSubject = PublishSubject.create();
    }

    /**
     * Returns the subject when requested by the associated presenter.
     * serializing the observable will force its emission and notification
     * to be serialized which otherwise tends to poorly behave by making inappropriate
     * calls to the its emission (onNext) and notification (onComplete / onError)
     * @return
     */
    public Observable<NearByPlaces> getNearByPlaceSubject() {
        return nbpListPublishSubject.asObservable().serialize();
    }

    /**
     * Get the List of NearByPlaces. Below implementation will fetch information from
     * both server and Realm
     */
    public void getNearByPlaces(Location location, String searchQuery, boolean type){
        /**
         * If subscription is already active, then Un subscribe  it.
         */
        if(nbpListSubscription != null && !nbpListSubscription.isUnsubscribed())
            nbpListSubscription.unsubscribe();

        nbpListSubscription = Observable.concat(
                fetchNearByPlaceFromRealm(),
                fetchNearByPlaceFromServer(location, searchQuery, type))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                /**
                 * TakeFirst will emit only when the condition is satisfied.
                 * In this case, emit item from the source observable only when
                 * the list is not null or when it has at least one element.
                 */
                .takeFirst(new Func1<NearByPlaces, Boolean>() {
                    @Override
                    public Boolean call(NearByPlaces nearByPlaces) {
                        return (nearByPlaces != null);
                    }
                }).subscribe(new Action1<NearByPlaces>() {
                    @Override
                    public void call(NearByPlaces nearByPlaces) {
                        NearByPlacesDS.this.nbpListPublishSubject.onNext(nearByPlaces);
                    }
                },new Action1<Throwable>() {
                    @Override
                    public void call(Throwable error) {
                        NearByPlacesDS.this.nbpListPublishSubject.onError(error);
                    }
                });

    }

    /**
     * Retrofit call to get near by places from Google Map APIs.
     * getNearByPlacesByType(location, radius, type, apikey) and
     * getNearByPlaces(location, radius, name, apikey) are the API endpoints
     * as defined in the ConnectionAPI Interface.
     *
     * API search can be performed by type or by name. Boolean type determines the kind of search.
     */
    public Observable<NearByPlaces> fetchNearByPlaceFromServer(Location location, String search, boolean type) {
        String latLng = "0,0";
        try {
            latLng = String.format("%f,%f", location.getLatitude(), location.getLongitude());
        }catch (Exception e){}
        Func1 function = new Func1<NearByPlaces, NearByPlaces>() {
            @Override
            public NearByPlaces call(NearByPlaces nearByPlaces) {
                return nearByPlaces;
            }
        };
        if(type)
            return connectionAPIInterface.getNearByPlacesByType(latLng,"500",search,
                    BuildConfig.MAPS_API_KEY).map(function);
        else
            return connectionAPIInterface.getNearByPlaces(latLng,"500",search,
                    BuildConfig.MAPS_API_KEY).map(function);
    }

    /**
     * Retrieve NearByPlaces information from the Realm tables.
     *
     * This application does not require persisting data as search results vary from
     * time to time. This is however to accommodate data base implementations,
     * may be as future enhancement. Until retrieval from DB is implemented,
     * this method observable emits nothing.
     */
    public Observable<NearByPlaces> fetchNearByPlaceFromRealm() {
        return Observable.empty();
    }

    /**
     * Flush all data regarding NearByPlaces from realm DB
     */
    public void wipeDateFromRealm() {
        nearByPlacesDAO.deleteFromDB();
    }


}
