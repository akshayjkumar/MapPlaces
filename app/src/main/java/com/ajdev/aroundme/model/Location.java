package com.ajdev.aroundme.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by Akshay.Jayakumar on 10/12/2017.
 *
 * Realm object to persist geo coordinates
 */

public class Location extends RealmObject {

    @SerializedName("lat")
    Double latitude;

    @SerializedName("lng")
    Double longitude;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
