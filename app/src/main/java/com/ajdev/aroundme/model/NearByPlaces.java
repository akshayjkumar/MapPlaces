package com.ajdev.aroundme.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Akshay.Jayakumar on 10/12/2017.
 *
 * Realm object to persist near-by-location points
 */

public class NearByPlaces extends RealmObject implements Serializable{

    @SerializedName("results")
    RealmList<Result> results;

    public RealmList<Result> getResults() {
        return results;
    }

    public void setResults(RealmList<Result> results) {
        this.results = results;
    }
}
