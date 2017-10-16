package com.ajdev.aroundme.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by Akshay.Jayakumar on 10/12/2017.
 *
 * Realm object to persist, Geographic locations of the point
 */

public class Geometry extends RealmObject {
    @SerializedName("location")
    Location location;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }


}
