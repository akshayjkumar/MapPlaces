package com.ajdev.aroundme.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Akshay.Jayakumar on 10/13/2017.
 */

public class NearByPlaceObject extends RealmObject {


    @SerializedName("results")
    RealmList<Result> results;

}
