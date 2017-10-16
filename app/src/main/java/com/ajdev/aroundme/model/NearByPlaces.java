package com.ajdev.aroundme.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Akshay.Jayakumar on 10/12/2017.
 *
 * Realm object to persist near-by-location points
 */

@Getter
public class NearByPlaces extends RealmObject implements Serializable{

    @SerializedName("results")
    RealmList<Result> results;

    public NearByPlaces(){}

    public NearByPlaces(List<Result> results){
        this.results.addAll(results);
    }

    public NearByPlaces(RealmList<Result> results){
        this.results = results;
    }
}
