package com.ajdev.aroundme.presenter;

import android.location.Location;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.ajdev.aroundme.datasource.NearByPlacesDS;
import com.ajdev.aroundme.datasource.PlaceDetailsDS;
import com.ajdev.aroundme.model.NearByPlaces;
import com.ajdev.aroundme.model.PlaceDetails;
import com.ajdev.aroundme.view.activity.NearbyPlacesActivity;
import com.ajdev.aroundme.view.fragment.PlaceDetailsBSFragment;

import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Akshay.Jayakumar on 10/12/2017.
 *
 * This class is the presenter for Place Details data.
 * This Presenter class contain methods that will be accessed by the appropriate
 * view inorder to perform necessary tasks including fetching from Google Place API.
 *
 */

public class PlaceDetailsPresenter {

    private WeakReference<PlaceDetailsBSFragment> viewWeakReference;

    // NearByPlace data source instance
    private PlaceDetailsDS placeDetailsDS;

    // Instance of Place Details subscription from the data source
    private Subscription placeDetailsSubscription;

    public PlaceDetailsPresenter(PlaceDetailsDS placeDetailsDS){
        this.placeDetailsDS = placeDetailsDS;
    }

    /**
     * Bind the presenter to the associated view.
     * Weak reference will always retain only a weak reference which will
     * not cause memory leaks unlike those with strong reference.
     */
    public void bindView(PlaceDetailsBSFragment fragment){
        viewWeakReference = new WeakReference<>(fragment);
    }

    /**
     * This method performs the following functions:
     * 1) Search for the places and returns the data from the API
     * 2) Binds the data source to its associated presenter.
     */
    public void search(String placeID){
        // Subscribe to the Observable emitting Place details
        subscribeToNearByPlacesList();
        // Start fetching place details using the data source
        placeDetailsDS.getPlacesDetails(placeID);
        // Show request progress
        if(viewWeakReference != null && viewWeakReference.get() != null){

        }
    }

    private void subscribeToNearByPlacesList(){
        if(placeDetailsSubscription == null || placeDetailsSubscription.isUnsubscribed()){
            placeDetailsSubscription = placeDetailsDS.getPlaceDetailsSubject()
                    /**
                     * Strategy for coping with Observables that produce items
                     * more rapidly than their observers can consume. In this case
                     * drop the items that observables cannot consume
                     */
                    .onBackpressureDrop()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<PlaceDetails>() {
                        @Override
                        public void call(PlaceDetails placeDetails) {
                            if(viewWeakReference != null
                                    && viewWeakReference.get() != null
                                    && placeDetails.getResult() != null){
                                viewWeakReference.get().updateData(placeDetails.getResult());
                            }
                            Log.e("@@@@@@@@@@@@@@@","@@@@ Data " + placeDetails.getResult().getFormattedAddress());
                        }
                    },
                    new Action1<Throwable>() {
                        @Override
                        public void call(Throwable error) {
                            Log.e("@@@@@@@@@@@@@@@@","@@@@@@@@@@@@@@@@@ error in presenter " + error.getLocalizedMessage());
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
        placeDetailsDS.wipeDateFromRealm();
    }


}
