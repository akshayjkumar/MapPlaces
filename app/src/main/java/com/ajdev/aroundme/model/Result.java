package com.ajdev.aroundme.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Akshay.Jayakumar on 10/12/2017.
 *
 */

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

    public RealmList<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(RealmList<Photo> photos) {
        this.photos = photos;
    }

    public OpeningTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(OpeningTime openingTime) {
        this.openingTime = openingTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
