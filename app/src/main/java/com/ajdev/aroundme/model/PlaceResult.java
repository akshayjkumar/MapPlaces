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

    public PlaceResult(){}

    public PlaceResult(String formattedAddress, String phoneNumber,
                       String phoneNumberInternational, float rating, String vicinity){
        this.formattedAddress = formattedAddress;
        this.phoneNumber = phoneNumber;
        this.phoneNumberInternational = phoneNumberInternational;
        this.rating = rating;
        this.vicinity = vicinity;
    }

}
