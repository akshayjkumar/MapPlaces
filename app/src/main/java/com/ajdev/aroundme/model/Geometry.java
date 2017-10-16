package com.ajdev.aroundme.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Akshay.Jayakumar on 10/12/2017.
 *
 * Realm object to persist, Geographic locations of the point
 */
@Getter
public class Geometry extends RealmObject {
    @SerializedName("location")
    Location location;

    public Geometry(){}

    public Geometry(Location location){
        this.location = location;
    }
}
