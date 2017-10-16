package com.ajdev.aroundme.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Akshay.Jayakumar on 10/12/2017.
 *
 * Realm object to persist detailed information about a place, location or a point.
 */

@Getter
public class PlaceDetails extends RealmObject implements Serializable{

    @SerializedName("result")
    PlaceResult result;

    public PlaceDetails(){}

    public PlaceDetails(PlaceResult result){
        this.result = result;
    }
}
