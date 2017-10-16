package com.ajdev.aroundme.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Akshay.Jayakumar on 10/12/2017.
 *
 */

public class PlaceResult extends RealmObject {

    @SerializedName("formatted_address")
    String formattedAddress;

    @SerializedName("formatted_phone_number")
    String phoneNumber;

    @SerializedName("international_phone_number")
    String phoneNumberInternational;

    @SerializedName("rating")
    float rating;

    @SerializedName("vicinity")
    String vicinity;

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumberInternational() {
        return phoneNumberInternational;
    }

    public void setPhoneNumberInternational(String phoneNumberInternational) {
        this.phoneNumberInternational = phoneNumberInternational;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }
}
