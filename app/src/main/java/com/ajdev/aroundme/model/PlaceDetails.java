package com.ajdev.aroundme.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Akshay.Jayakumar on 10/12/2017.
 *
 * Realm object to persist detailed information about a place, location or a point.
 */

public class PlaceDetails extends RealmObject implements Serializable{

    @SerializedName("result")
    PlaceResult result;

    public PlaceResult getResult() {
        return result;
    }

    public void setResults(PlaceResult result) {
        this.result = result;
    }
}
