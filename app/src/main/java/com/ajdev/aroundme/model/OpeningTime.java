package com.ajdev.aroundme.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by Akshay.Jayakumar on 10/14/2017.
 */

public class OpeningTime extends RealmObject {

    @SerializedName("open_now")
    boolean openingHours;

    public boolean isOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(boolean openingHours) {
        this.openingHours = openingHours;
    }
}
