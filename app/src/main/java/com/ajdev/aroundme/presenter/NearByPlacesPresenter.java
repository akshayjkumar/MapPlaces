package com.ajdev.aroundme.presenter;

import android.location.Location;
import android.util.Log;

import com.ajdev.aroundme.datasource.NearByPlacesDS;
import com.ajdev.aroundme.model.NearByPlaces;
import com.ajdev.aroundme.view.activity.NearbyPlacesActivity;

import java.lang.ref.WeakReference;
import java.util.List;

import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Akshay.Jayakumar on 10/12/2017.
 *
 * This class is the presenter for Near By Places data.
 * This Presenter class contain methods that will be accessed by the appropriate
 * view inorder to perform necessary tasks including fetching from Google Map APIs.
 *
 */

public class NearByPlacesPresenter {

    private WeakReference<NearbyPlacesActivity> viewWeakReference;

    // NearByPlace data source instance
    private NearByPlacesDS nearByPlacesDS;

    // Instance of NearByPlacesList subscription from the data source
    private Subscription nbpListSubscription;

    public NearByPlacesPresenter(NearByPlacesDS nearByPlacesDS){
        this.nearByPlacesDS = nearByPlacesDS;
    }

    /**
     * Bind the presenter to the associated view.
     * Weak reference will always retain only a weak reference which will
     * not cause memory leaks unlike those with strong reference.
     */
    public void bindView(NearbyPlacesActivity nearbyPlacesActivity){
        viewWeakReference = new WeakReference<>(nearbyPlacesActivity);
    }

    /**
     * This method performs the following functions:
     * 1) Search for the places and returns the data from the API
     * 2) Binds the data source to its associated presenter.
     */
    public void search(Location location, String searchQuery, boolean type){
        // Subscribe to the Observable emiting NearByPlacesList
        subscribeToNearByPlacesList();
        // Start fetching NearByPlaces using the data source
        nearByPlacesDS.getNearByPlaces(location, searchQuery, type);
        // Show request progress
        if(viewWeakReference != null && viewWeakReference.get() != null)
            viewWeakReference.get().toggleProgressBarVisibility(true);
    }

    public void subscribeToNearByPlacesList(){
        if(nbpListSubscription == null || nbpListSubscription.isUnsubscribed()){
            nbpListSubscription = nearByPlacesDS.getNearByPlaceSubject()
                    /**
                     * Strategy for coping with Observables that produce items
                     * more rapidly than their observers can consume. In this case
                     * drop the items that observables cannot consume
                     */
                    .onBackpressureDrop()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<NearByPlaces>() {
                        @Override
                        public void call(NearByPlaces nearByPlaces) {

                            if(viewWeakReference != null && viewWeakReference.get() != null){
                                if(nearByPlaces != null && nearByPlaces.getResults() != null){
                                    viewWeakReference.get().updateAdapterLocation();
                                    viewWeakReference.get().setSearchResultAdapter(nearByPlaces.getResults());
                                    viewWeakReference.get().toggleProgressBarVisibility(false);
                                }
                            }
                        }
                    },
                    new Action1<Throwable>() {
                        @Override
                        public void call(Throwable error) {
                            if(viewWeakReference != null && viewWeakReference.get() != null)
                                viewWeakReference.get().toggleProgressBarVisibility(false);
                        }
                    });

        }
    }

    /**
     * Clear all data for near by places that is persisted in the
     * Realm database. Use the method wipeDateFromRealm() of the
     * associated Data Source object(in this case NearByPlacesDS).
     */
    public void clearDBData(){
        nearByPlacesDS.wipeDateFromRealm();
    }


}
