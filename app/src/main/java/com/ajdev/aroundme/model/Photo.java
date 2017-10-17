package com.ajdev.aroundme.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Akshay.Jayakumar on 10/14/2017.
 */
@Getter
public class Photo extends RealmObject {

    @SerializedName("photo_reference")
    String photoReference;

    public Photo(){}

    public Photo(String photoReference){
        this.photoReference = photoReference;
    }
}
