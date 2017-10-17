package com.ajdev.aroundme.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import lombok.Getter;

/**
 * Created by Akshay.Jayakumar on 10/14/2017.
 */

@Getter
public class OpeningTime extends RealmObject {

    @SerializedName("open_now")
    boolean openNow;

    public OpeningTime(){}

    public OpeningTime(boolean openNow){
        this.openNow = openNow;
    }
}
