package com.ajdev.aroundme.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Akshay.Jayakumar on 10/12/2017.
 *
 */

@Getter
public class Result extends RealmObject {

    @SerializedName("name")
    String name;

    @SerializedName("place_id")
    String placeID;

    @SerializedName("vicinity")
    String vicinity;

    @SerializedName("rating")
    float rating;

    @SerializedName("geometry")
    Geometry geometry;

    @SerializedName("photos")
    RealmList<Photo> photos;

    @SerializedName("opening_hours")
    OpeningTime openingTime;

    public Result(){}

    public Result(String name, String placeID, String vicinity,
                  float rating, Geometry geometry, RealmList<Photo> photos, OpeningTime openingTime){
        this.name = name;
        this.placeID = placeID;
        this.vicinity = vicinity;
        this.rating = rating;
        this.geometry = geometry;
        this.photos = photos;
        this.openingTime = openingTime;
    }
}
