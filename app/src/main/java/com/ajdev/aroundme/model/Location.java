package com.ajdev.aroundme.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Akshay.Jayakumar on 10/12/2017.
 *
 * Realm object to persist geo coordinates
 */

@Getter
public class Location extends RealmObject {

    @SerializedName("lat")
    Double latitude;

    @SerializedName("lng")
    Double longitude;

    public Location(){}

    public Location(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
